import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    jacoco

    id("org.springframework.boot") version "3.3.+"
    id("org.owasp.dependencycheck") version "11.0.0"
    id("com.github.rising3.semver") version "0.8.2"
    // id("org.graalvm.buildtools.native") version "0.10.+"
	id("io.spring.dependency-management") version "1.1.4"
    id("org.jetbrains.dokka") version "2.0.0-Beta"
    id("com.google.cloud.tools.jib") version "3.4.4"
    //id("org.springframework.experimental.aot") version "0.11.4"
    id("com.gorylenko.gradle-git-properties") version "2.4.0"
    id("org.openrewrite.rewrite") version "6.27.+"


	kotlin("jvm") version "2.0.+"
	kotlin("plugin.spring") version "2.0.+"
	kotlin("plugin.jpa") version "2.0.+"
    kotlin("plugin.noarg") version "2.0.+"

}

group = "io.dereknelson"
version = project.property("version")!!



configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
    maven {
        url = uri("https://maven.pkg.github.com/lostcities-cloud/lostcities-common")
        credentials {
            username = System.getenv("GH_USER")
            password = System.getenv("GH_TOKEN")
        }
    }
    maven {
        url = uri("https://maven.pkg.github.com/lostcities-cloud/lostcities-models")
        credentials {
            username = System.getenv("GH_USER")
            password = System.getenv("GH_TOKEN")
        }
    }

    google()
	mavenCentral()
    gradlePluginPortal()
}


extra["snippetsDir"] = file("build/generated-snippets")


val ktlint by configurations.creating

dependencyManagement {
    imports {
        mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
        // mavenBom("io.opentelemetry.instrumentation:opentelemetry-instrumentation-bom:2.6.0")
    }
}

configurations.matching { it.name.startsWith("dokka") }.configureEach {
    resolutionStrategy.eachDependency {
        if (requested.group.startsWith("com.fasterxml.jackson")) {
            useVersion("2.15.3")
        }
    }
}

rewrite {
    activeRecipe("org.openrewrite.java.spring.boot3.UpgradeSpringBoot_3_3")
    //exportDatatables = true

    sizeThresholdMb = 10
}

dependencies {
    rewrite("org.openrewrite:rewrite-kotlin:1.21.2")
    rewrite("org.openrewrite.recipe:rewrite-spring:5.22.0")

    runtimeOnly("org.springframework.boot:spring-boot-properties-migrator")
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")
    // runtimeOnly("io.micrometer:micrometer-tracing-bridge-brave")
    // runtimeOnly("io.opentelemetry.instrumentation:opentelemetry-spring-boot-starter")
    // runtimeOnly("io.zipkin.contrib.otel:encoder-brave:0.1.0")
    // runtimeOnly("io.opentelemetry:opentelemetry-logging-exporter")

    implementation("org.hibernate:hibernate-core:6.4.4.Final")
    implementation("org.hibernate:hibernate-micrometer:6.4.4.Final")
    implementation("org.hibernate:hibernate-jcache:6.4.4.Final")
    implementation("org.ehcache.modules:ehcache-107:3.9.2")
    implementation("org.ehcache:ehcache:3.9.2")

    implementation("org.springframework.boot:spring-boot-devtools")

    if(rootProject.hasProperty("debug")){
        implementation(project(":lostcities-common"))
        implementation(project(":lostcities-models"))
    } else {
        implementation("io.dereknelson.lostcities-cloud:lostcities-common:0.0.7")
        implementation("io.dereknelson.lostcities-cloud:lostcities-models:0.0.6")
    }

	implementation("org.apache.commons:commons-lang3:3.12.0")

	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-mail")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-validation")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-hibernate6")

	implementation("org.zalando:problem-spring-web:0.21.0")

	// implementation("org.springdoc:springdoc-openapi-core:1.5.10")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.+")
	//implementation("org.springdoc:springdoc-openapi-starter-webmvc-api:2.6.+")
	implementation("org.springdoc:springdoc-openapi-kotlin:1.8.0")

	implementation("org.modelmapper:modelmapper:2.4.1")
    runtimeOnly("org.flywaydb:flyway-core:10.8.1")
    runtimeOnly("org.flywaydb:flyway-database-postgresql:10.8.1")

	implementation("io.jsonwebtoken:jjwt-api:0.11.2")
	implementation("io.jsonwebtoken:jjwt-impl:0.11.2")
	implementation("io.jsonwebtoken:jjwt-jackson:0.11.2")

	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("com.h2database:h2")

	ktlint("com.pinterest:ktlint:0.49.1") {
		attributes {
			attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
		}
	}
    dokkaHtmlPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:2.0.0-Beta")

	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

	testImplementation("org.junit.jupiter:junit-jupiter:5.6.2")
	testImplementation("org.junit.jupiter:junit-jupiter-engine:5.6.2")
	testImplementation("org.mockito:mockito-junit-jupiter:2.23.0")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("org.mockito.kotlin:mockito-kotlin:3.2.0")
	testImplementation("org.assertj:assertj-core:3.19.0")
}

val outputDir = "${project.layout.buildDirectory}/reports/ktlint/"
val inputFiles = project.fileTree(mapOf("dir" to "src", "include" to "**/*.kt"))

val ktlintCheck by tasks.creating(JavaExec::class) {
	inputs.files(inputFiles)
	outputs.dir(outputDir)

	description = "Check Kotlin code style."
	classpath = ktlint
	mainClass.set("com.pinterest.ktlint.Main")
	args = listOf("src/**/*.kt")
}

val ktlintFormat by tasks.creating(JavaExec::class) {
	inputs.files(inputFiles)
	outputs.dir(outputDir)
	description = "Fix Kotlin code style deviations."
	classpath = ktlint
	mainClass.set("com.pinterest.ktlint.Main")
	args = listOf("-F", "src/**/*.kt")
	jvmArgs("--add-opens", "java.base/java.lang=ALL-UNNAMED")
}

noArg {
    annotation("jakarta.persistence.Entity")
}

semver {
    noGitPush = false
}



tasks.test {
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}
tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
}



tasks.withType<KotlinCompile>() {

    compilerOptions {
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0)
        languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0)

        freeCompilerArgs.addAll(listOf(
            "-Xjsr305=strict", "-opt-in=kotlin.RequiresOptIn"
        ))
    }
}

tasks.named("compileKotlin", org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask::class.java) {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

jib {
	from {
        image = "registry://docker.io/library/amazoncorretto:21-alpine-jdk"
	}

	to {
        image = "ghcr.io/lostcities-cloud/${project.name}:${project.version}"
        tags = mutableSetOf("latest", "${project.version}")
		auth {
			username = System.getenv("GH_USER")
    		password = System.getenv("GH_TOKEN")
		}
	}

}

dependencyCheck {
    failBuildOnCVSS = 11f
    failOnError = false
    formats = mutableListOf("JUNIT", "HTML", "JSON")
    data {
        directory = "${rootDir}/owasp"
    }
    //suppressionFiles = ['shared-owasp-suppressions.xml']
    analyzers {
        assemblyEnabled = false
    }
    nvd {
        apiKey = System.getenv("NVD_KEY")
        delay = 16000
    }
}

tasks.withType<Test> {
	useJUnitPlatform()
}

