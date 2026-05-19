package io.dereknelson.lostcities.accounts

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.servers.Server
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan

import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter
import org.springframework.web.servlet.LocaleResolver

import org.springframework.web.servlet.i18n.CookieLocaleResolver
import java.util.Locale

@SpringBootApplication(exclude = [ErrorMvcAutoConfiguration::class])
@ComponentScan(
    "io.dereknelson.lostcities.accounts",
    "io.dereknelson.lostcities.accounts",
    "io.dereknelson.lostcities.common.auth",
    "io.dereknelson.lostcities.common.auditing",
)
@EnableConfigurationProperties
@EnableJpaRepositories(
   "io.dereknelson.lostcities.accounts.user",
    "io.dereknelson.lostcities.accounts.auth.crypto",
    "io.dereknelson.lostcities.common.model"
)
@EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAware")
@EnableTransactionManagement
@OpenAPIDefinition(servers = [Server(url = "lostcities.com", )])
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = false, jsr250Enabled = true)
class LostcitiesAccountsApplication

private val logger: Log = LogFactory.getLog(LostcitiesAccountsApplication::class.java)
fun main(args: Array<String>) {
    logger.info("Starting Accounts service")
    runApplication<LostcitiesAccountsApplication>(*args)
}

const val allowedOrigins = "*"




@Bean
fun mapper(): ObjectMapper =
    jacksonObjectMapper()
        .registerKotlinModule()
        .registerModule(Jdk8Module())
        .registerModule(JavaTimeModule())
        .setSerializationInclusion(JsonInclude.Include.NON_NULL)!!

@Bean
fun localeResolver(): LocaleResolver {
    val cookieLocaleResolver = CookieLocaleResolver("LANG_KEY")
    cookieLocaleResolver.setDefaultLocale(Locale.ENGLISH)
    return cookieLocaleResolver
}





