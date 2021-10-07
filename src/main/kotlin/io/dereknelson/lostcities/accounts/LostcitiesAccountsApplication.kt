package io.dereknelson.lostcities.accounts

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.servers.Server
import io.undertow.UndertowOptions
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories


@EnableJpaRepositories
@ComponentScan(
	"io.dereknelson.lostcities.accounts",
	"io.dereknelson.lostcities.accounts.config",
	"io.dereknelson.lostcities.accounts.library",
	"io.dereknelson.lostcities.common.auth",
	"io.dereknelson.lostcities.common.library"
)
@SpringBootApplication(exclude=[ErrorMvcAutoConfiguration::class])
@OpenAPIDefinition(servers = [Server(url="lostcities.com")])
class LostcitiesApplication

fun main(args: Array<String>) {
	runApplication<LostcitiesApplication>(*args)
}

@Bean
fun embeddedServletContainerFactory(): UndertowServletWebServerFactory {
	val factory = UndertowServletWebServerFactory()
	//factory.addBuilderCustomizers( { it.} )
		//{ builder -> builder.setServerOption(UndertowOptions.ENABLE_HTTP2, true) },
		//{ builder -> builder.addHttpListener(port, host) }
	return factory
}
