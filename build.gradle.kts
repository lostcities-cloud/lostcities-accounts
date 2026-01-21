import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.spotbugs.snom.Confidence
import com.github.spotbugs.snom.Effort
import com.github.spotbugs.snom.SpotBugsTask
import org.gradle.internal.configuration.problems.firstTypeFrom
import org.springframework.boot.gradle.tasks.run.BootRun

plugins {
    jacoco

    id("org.springframework.boot") version "3.2.+"
    id("org.owasp.dependencycheck") version "11.0.0"
    id("com.github.rising3.semver") version "0.8.2"
    // id("org.graalvm.buildtools.native") version "0.10.+"
	id("io.spring.dependency-management") version "1.1.4"
    id("org.jetbrains.dokka") version "2.1.0"
    id("com.google.cloud.tools.jib") version "3.4.4"
    //id("org.springframework.experimental.aot") version "0.11.4"
    id("com.gorylenko.gradle-git-properties") version "2.4.0"
    id("org.openrewrite.rewrite") version "6.27.+"
    id("com.github.spotbugs") version "6.4.4"
    id("org.graalvm.buildtools.native") version "0.11.1"

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
            username = System.getenv("GITHUB_ACTOR")
            password = System.getenv("GITHUB_TOKEN")
        }
    }
    maven {
        url = uri("https://maven.pkg.github.com/lostcities-cloud/lostcities-models")
        credentials {
            username = System.getenv("GITHUB_ACTOR")
            password = System.getenv("GITHUB_TOKEN")
        }
    }

    google()
	mavenCentral()
    gradlePluginPortal()
}


val ktlint by configurations.creating



/*rewrite {
    activeRecipe("org.openrewrite.java.spring.boot3.UpgradeSpringBoot_3_3")
    //exportDatatables = true

    sizeThresholdMb = 10
}*/

tasks.named<BootRun>("bootRun") {
    if(rootProject.hasProperty("debug")) {
        systemProperty("spring.profiles.active", "local")
    }
}

dependencies {
    implementation(platform(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES))

    //rewrite("org.openrewrite:rewrite-kotlin:1.21.2")
    //rewrite("org.openrewrite.recipe:rewrite-spring:5.22.0")
    spotbugs("com.github.spotbugs:spotbugs:4.9.8")
    spotbugsPlugins("com.h3xstream.findsecbugs:findsecbugs-plugin:1.14.0")
    runtimeOnly("org.springframework.boot:spring-boot-properties-migrator")
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")
    // runtimeOnly("io.micrometer:micrometer-tracing-bridge-brave")
    // runtimeOnly("io.opentelemetry.instrumentation:opentelemetry-spring-boot-starter")
    // runtimeOnly("io.zipkin.contrib.otel:encoder-brave:0.1.0")
    // runtimeOnly("io.opentelemetry:opentelemetry-logging-exporter")

    implementation("org.hibernate:hibernate-core:${rootProject.extra["hibernate.version"]}")
    implementation("org.hibernate:hibernate-micrometer:${rootProject.extra["hibernate.version"]}")

    implementation("org.apache.httpcomponents.client5:httpclient5:${rootProject.extra["httpclient5.version"]}")
    implementation("org.apache.httpcomponents.core5:httpcore5:${rootProject.extra["httpcore5.version"]}")


    if(rootProject.hasProperty("debug") || project.hasProperty("debug")){
        implementation("org.springframework.boot:spring-boot-devtools")
        implementation(project(":lostcities-common"))
        implementation(project(":lostcities-models"))
    } else {
        implementation("io.dereknelson.lostcities-cloud:lostcities-common:${rootProject.extra["lostcities-common.version"]}")
        implementation("io.dereknelson.lostcities-cloud:lostcities-models:${rootProject.extra["lostcities-models.version"]}")
    }

    implementation("org.apache.commons:commons-lang3:${rootProject.extra["commonsLang3.version"]}")

	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-mail")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-validation")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-hibernate6")

	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${rootProject.extra["springdoc.version"]}")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-api:${rootProject.extra["springdoc.version"]}")
    // implementation("org.springdoc:springdoc-openapi-starter-webmvc-scalar:${rootProject.extra["springdoc.version"]}")

	implementation("org.modelmapper:modelmapper:2.4.1")
    runtimeOnly("org.flywaydb:flyway-core:10.8.1")
    runtimeOnly("org.flywaydb:flyway-database-postgresql:10.8.1")

	implementation("io.jsonwebtoken:jjwt-api:${rootProject.extra["jjwt.version"]}")
	implementation("io.jsonwebtoken:jjwt-impl:${rootProject.extra["jjwt.version"]}")
	implementation("io.jsonwebtoken:jjwt-jackson:${rootProject.extra["jjwt.version"]}")

	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("org.postgresql:postgresql")

	ktlint("com.pinterest:ktlint:${rootProject.extra["ktlint.version"]}") {
		attributes {
			attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
		}
	}

	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

	testImplementation("org.junit.jupiter:junit-jupiter")
	testImplementation("org.junit.jupiter:junit-jupiter-engine")
	testImplementation("org.mockito:mockito-junit-jupiter")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("org.mockito.kotlin:mockito-kotlin:3.2.0")
	testImplementation("org.assertj:assertj-core:3.19.0")
} val outputDir = "${project.layout.buildDirectory}/reports/ktlint/"
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

tasks.bootBuildImage {
    docker.host = "unix:///run/user/1000/podman/podman.sock"
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}
tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
}


spotbugs {
    ignoreFailures = true
    showStackTraces = true
    showProgress = true
    //effort = Effort.MAX
    //reportLevel = Confidence.HIGH
    //visitors = listOf("FindSqlInjection", "SwitchFallthrough")
    //omitVisitors = listOf("FindNonShortCircuit")
    //chooseVisitors = listOf("-FindNonShortCircuit", "+TestASM")
    //reportsDir = file("${project.layout.buildDirectory}/reports/spotbugs")
    //includeFilter = file("include.xml")
    //excludeFilter = file("exclude.xml")
    //baselineFile = file("baseline.xml")
    //onlyAnalyze = listOf("com.foobar.MyClass", "com.foobar.mypkg.*")
    maxHeapSize = "1g"
    extraArgs = listOf("-nested:false")
    //jvmArgs = listOf()
}

tasks.spotbugsMain {
    val html = reports.create("html")
    html.required.set(true)
    //html.outputLocation = file("${project.layout.buildDirectory}/reports/spotbugs/spotbugs.html")
    html.setStylesheet("fancy-hist.xsl")

}

tasks.withType<KotlinCompile>() {

    compilerOptions {
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_2)
        languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_2)

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
        image = "registry://public.ecr.aws/amazoncorretto/amazoncorretto:21.0.8-al2023-headless"
	}

	to {
        image = "ghcr.io/lostcities-cloud/${project.name}:${project.version}"
        tags = mutableSetOf("latest", "${project.version}")
		auth {
            username = System.getenv("GITHUB_ACTOR")
            password = System.getenv("GITHUB_TOKEN")
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

