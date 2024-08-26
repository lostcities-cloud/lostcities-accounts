import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    id("org.springframework.boot") version "3.1.+"
    // id("org.graalvm.buildtools.native") version "0.10.+"
	id("io.spring.dependency-management") version "1.1.4"
    id("org.jetbrains.dokka") version "1.6.10"
    id("com.google.cloud.tools.jib") version "3.4.3"
    //id("org.springframework.experimental.aot") version "0.11.4"
    id("com.gorylenko.gradle-git-properties") version "2.4.0"

	kotlin("jvm") version "2.0.+"
	kotlin("plugin.spring") version "2.0.+"
	kotlin("plugin.jpa") version "2.0.+"
    kotlin("plugin.noarg") version "2.0.+"

}

group = "io.dereknelson"
version = "0.0.2-SNAPSHOT"

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	maven {
		url = uri("https://maven.pkg.github.com/lostcities-cloud/lostcities-common")
		credentials {
			username = System.getenv("GITHUB_ACTOR")
			password = System.getenv("GITHUB_TOKEN")
		}
	}
	mavenCentral()
}


extra["snippetsDir"] = file("build/generated-snippets")

val ktlint by configurations.creating

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2022.0.5")
    }
}

dependencies {

    runtimeOnly("org.springframework.boot:spring-boot-properties-migrator")
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")
    compileOnly("org.springframework.boot:spring-boot-starter-undertow")

    implementation("org.hibernate:hibernate-core:6.4.4.Final")
    implementation("org.hibernate:hibernate-micrometer:6.4.4.Final")
    implementation("org.hibernate:hibernate-jcache:6.4.4.Final")
    implementation("org.ehcache.modules:ehcache-107:3.9.2")
    implementation("org.ehcache:ehcache:3.9.2")

    implementation("org.springframework.boot:spring-boot-devtools")
    implementation(project(":lostcities-common"))
	implementation("org.apache.commons:commons-lang3:3.12.0")

	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-mail")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-validation")

    implementation("org.springframework.cloud:spring-cloud-starter-consul-discovery")
    implementation("org.springframework.cloud:spring-cloud-starter-consul-config")

	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.2")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-hppc")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
	implementation("com.fasterxml.jackson.module:jackson-module-jaxb-annotations")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-hibernate5")
	implementation("com.fasterxml.jackson.core:jackson-annotations")
	implementation("com.fasterxml.jackson.core:jackson-databind")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

	implementation("org.zalando:problem-spring-web:0.21.0")

	// implementation("org.springdoc:springdoc-openapi-core:1.5.10")
	implementation("org.springdoc:springdoc-openapi-webmvc-core:1.7.0")
	implementation("org.springdoc:springdoc-openapi-ui:1.7.0")
	implementation("org.springdoc:springdoc-openapi-kotlin:1.7.0")

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

	ktlint("com.pinterest:ktlint:0.44.0") {
		attributes {
			attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
		}
	}
    dokkaHtmlPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:1.6.10")

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

val outputDir = "${project.buildDir}/reports/ktlint/"
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
		image = "registry://amd64/eclipse-temurin:21-alpine"
	}
	to {
		image = "ghcr.io/lostcities-cloud/${project.name}:latest"

		auth {
			username = System.getenv("GH_ACTOR")
    		password = System.getenv("GH_TOKEN")
		}
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

