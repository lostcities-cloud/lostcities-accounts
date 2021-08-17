package io.dereknelson.lostcities.accounts

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories


@EnableJpaRepositories
@ComponentScan(
	"io.dereknelson.lostcities.accounts",
	"io.dereknelson.lostcities.accounts.config",
	"io.dereknelson.lostcities.common.library"
)

@SpringBootApplication(exclude=[ErrorMvcAutoConfiguration::class])
class LostcitiesApplication

fun main(args: Array<String>) {
	runApplication<LostcitiesApplication>(*args)
}
