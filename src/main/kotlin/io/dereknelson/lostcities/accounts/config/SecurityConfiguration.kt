package io.dereknelson.lostcities.accounts.config

import io.dereknelson.lostcities.accounts.service.AuthUserDetailsService
import io.dereknelson.lostcities.common.AuthoritiesConstants
import io.dereknelson.lostcities.common.auth.JwtFilter
import io.dereknelson.lostcities.common.auth.TokenProvider
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.security.SecurityScheme
import io.swagger.v3.oas.models.PathItem
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher
import org.springframework.web.filter.ForwardedHeaderFilter
import org.springframework.web.filter.GenericFilterBean

@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@SecurityScheme(
    name = "jwt_auth",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer"
)
@Configuration
class SecurityConfiguration(
    private val tokenProvider: TokenProvider,
    private val lostCitiesUserDetailsService: AuthUserDetailsService
) {

    @Bean
    fun forwardedHeaderFilter(): ForwardedHeaderFilter? {
        return ForwardedHeaderFilter()
    }

    @Bean
    fun userDetailsService(authenticationManagerBuilder: AuthenticationManagerBuilder): UserDetailsService {
        authenticationManagerBuilder.userDetailsService(lostCitiesUserDetailsService)
        return lostCitiesUserDetailsService
    }

    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer {
        return WebSecurityCustomizer { web: WebSecurity ->
            web
                .ignoring()
                .requestMatchers(antMatcher(HttpMethod.OPTIONS,"/**"))
                .requestMatchers("/actuator/accounts/**")
                .requestMatchers(

                    "/i18n/**",
                    "/accounts/**",
                    "/swagger-ui/**",
                )
        }
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity, corsConfiguration: CorsConfiguration): DefaultSecurityFilterChain {
        /* ktlint-disable max_line_length */
        // @formatter:off

        http
            .csrf { it.disable() }
            .cors { it.configure(http) }
            .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter::class.java)
            .exceptionHandling {}
            .headers { headersConfigurer ->
                headersConfigurer.contentSecurityPolicy {
                    it.policyDirectives("default-src 'self'; frame-src 'self' data:; script-src 'self' 'unsafe-inline' 'unsafe-eval' https://storage.googleapis.com; style-src 'self' 'unsafe-inline'; img-src 'self' data:; font-src 'self' data:")
                }.referrerPolicy {
                    it.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
                }.cacheControl {  }

            }

            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authorizeHttpRequests{ requests ->
                requests

                    .requestMatchers(
                        "/authenticate",
                        "/register",
                        "/activate",
                        "/reset-password/init",
                        "/reset-password/finish",
                        "/swagger-ui/**",
                        "/openapi/**",
                        "/health",
                        "/accounts/**",
                        "/info",
                        "/prometheus"
                    ).permitAll()
                    .requestMatchers("/health").permitAll()
                    .requestMatchers("/actuator/health").permitAll()
                    .requestMatchers("/api/admin/**").hasAuthority(AuthoritiesConstants.ADMIN)
                    .anyRequest().authenticated()
            }

        /* ktlint-enable max_line_length */
        // @formatter:on
        return http.build()!!
    }

    @Bean
    @Throws(Exception::class)
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager {
        return config.authenticationManager
    }

    @Bean
    fun authenticationProvider(
        encoder: PasswordEncoder,
        userDetailsService: AuthUserDetailsService
    ): AuthenticationProvider {
        val authenticationProvider = DaoAuthenticationProvider()
        authenticationProvider.setUserDetailsService(userDetailsService)
        authenticationProvider.setPasswordEncoder(encoder);
        return authenticationProvider
    }

    private fun jwtFilter(): GenericFilterBean {
        return JwtFilter(tokenProvider)
    }

    @Bean
    fun encoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}
