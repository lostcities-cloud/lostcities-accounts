variable "version" {
    type = string
    default = "latest"
}

variable "cpu" {
    type = number
    default = 500
}

variable "memory" {
    type = number
    default = 500
}

variable count {
    type = number
    default = 2
}

variable max_parallel {
    type = number
    default = 6
}

variable profile {
    type = string
    default = "dev"
}

job "accounts" {
  region = "global"
  datacenters = [ "tower-datacenter"]

  update {
    max_parallel = var.max_parallel
  }

  spread {
    attribute = "${node.datacenter}"
    weight    = 100
  }

  group "accounts" {
    count = var.count

    restart {
      attempts = 10
      interval = "5m"
      delay    = "25s"
      mode     = "delay"
    }

    network {
      mode = "bridge"

      port "management-port" {
        to     = 4452
      }

      port "service-port" {
        to = 8080
      }

    }

    service {
      name = "accounts-service"
      port = "service-port"
      tags = ["urlprefix-/api/accounts"]
      #address_mode = "host"

      check {
        type = "http"
        port = "management-port"
        path = "/management/accounts/actuator/health"
        interval = "30s"
        timeout  = "10s"
        failures_before_critical = 20
        failures_before_warning = 10
      }
    }

    service {
      name = "accounts-management"
      port = "management-port"
      tags = ["urlprefix-/management/accounts/actuator"]


      check {
        type = "http"
        port = "management-port"
        path = "/management/accounts/actuator/health"
        interval = "30s"
        timeout  = "10s"
        failures_before_critical = 20
        failures_before_warning = 10
      }
    }

    task "accounts" {
      driver = "podman"

      env {
        SPRING_PROFILES_ACTIVE      = var.profile
      }

      resources {
        cpu    = var.cpu
        memory = var.memory
      }

      config {
        force_pull   = true
        network_mode = "host"
        image        = "ghcr.io/lostcities-cloud/lostcities-accounts:${var.version}"
        ports = ["service-port", "management-port"]
        logging = {
          driver = "nomad"
        }
      }
      template {
        data        = <<EOF
{{ range service "postgres" }}
POSTGRES_IP="{{ .Address }}"
{{ else }}
{{ end }}
{{ range service "redis" }}
REDIS_IP="{{ .Address }}"
{{ else }}
{{ end }}
{{ range service "rabbitmq" }}
RABBITMQ_IP="{{ .Address }}"
{{ else }}
{{ end }}
EOF
        //change_mode   = "signal"
        //change_signal = "SIGHUP"
        destination = "local/discovery.env"
        env         = true
      }
    }

    update {
      max_parallel     = 2
      min_healthy_time = "5s"
      healthy_deadline = "3m"
      auto_revert      = false
      canary           = 1
      auto_promote     = true
    }
  }
}
