package io.dereknelson.lostcities.accounts

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.servers.Server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication(exclude = [ErrorMvcAutoConfiguration::class])
@ComponentScan(
    "io.dereknelson.lostcities.accounts",
    "io.dereknelson.lostcities.accounts.config",
    "io.dereknelson.lostcities.accounts.library",
    "io.dereknelson.lostcities.common.auth",
    "io.dereknelson.lostcities.common.library"
)
@EnableJpaRepositories
@OpenAPIDefinition(servers = [Server(url = "lostcities.com")])
class LostcitiesAccountsApplication

fun main(args: Array<String>) {
    runApplication<LostcitiesAccountsApplication>(*args)
}
