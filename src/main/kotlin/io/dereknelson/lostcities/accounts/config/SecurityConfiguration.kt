package io.dereknelson.lostcities.accounts.config

import io.dereknelson.lostcities.accounts.service.AuthUserDetailsService
import io.dereknelson.lostcities.common.auth.JwtFilter
import io.dereknelson.lostcities.common.auth.TokenProvider
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.security.SecurityScheme
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher
import org.springframework.web.filter.ForwardedHeaderFilter

@EnableWebSecurity(debug = true)
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@SecurityScheme(
    name = "jwt_auth",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer",
)
@Configuration
class SecurityConfiguration(
    private val tokenProvider: TokenProvider,
    private val lostCitiesUserDetailsService: AuthUserDetailsService,
) {

    @Bean
    fun forwardedHeaderFilter(): ForwardedHeaderFilter? {
        return ForwardedHeaderFilter()
    }

    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        corsConfiguration: CorsConfiguration,
        authenticationManager: AuthenticationManager,
    ): DefaultSecurityFilterChain {
        // @formatter:off

        http
            .csrf { it.disable() }
            .cors { it.configure(http) }
            .addFilterBefore(JwtFilter(tokenProvider), UsernamePasswordAuthenticationFilter::class.java)
            .authenticationManager(authenticationManager)
            .exceptionHandling {}
            .headers { headersConfigurer ->
                headersConfigurer.contentSecurityPolicy {
                    it.policyDirectives("default-src 'self'; frame-src 'self' data:; script-src 'self' 'unsafe-inline' 'unsafe-eval' https://storage.googleapis.com; style-src 'self' 'unsafe-inline'; img-src 'self' data:; font-src 'self' data:")
                }.referrerPolicy {
                    it.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
                }.cacheControl { }
            }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authorizeHttpRequests { requests ->
                requests
                    .requestMatchers(
                        "/actuator",
                        "/actuator/**",
                        "/authenticate",
                        "/register",
                        "/activate",
                        "/reset-password/init",
                        "/reset-password/finish",
                        "/swagger-ui/**",
                        "/health",
                        "/accounts/**",
                        "/info",
                        "/prometheus",
                    ).permitAll()
                    .requestMatchers("/actuator/**").permitAll()
                    .anyRequest().authenticated()
            }
        // @formatter:on
        return http.build()!!
    }

    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer {
        return WebSecurityCustomizer { web: WebSecurity ->
            web
                .ignoring()
                .requestMatchers(antMatcher(HttpMethod.OPTIONS, "/**"))
                .requestMatchers(antMatcher(HttpMethod.GET, "/actuator/**"))
                .requestMatchers(

                    "/i18n/**",
                    "/content/**",
                    "/accounts/**",
                    "/swagger-ui/**",
                )
        }
    }

    @Bean
    @Throws(Exception::class)
    fun authenticationManager(
        http: HttpSecurity,
        bCryptPasswordEncoder: PasswordEncoder,
        userDetailService: AuthUserDetailsService,
    ): AuthenticationManager {
        val builder = http.getSharedObject(
            AuthenticationManagerBuilder::class.java,
        )
        builder.userDetailsService(userDetailService)
            .passwordEncoder(bCryptPasswordEncoder)
        return builder.build()
    }

    @Autowired
    fun injectUserDetailsService(
        authenticationManagerBuilder: AuthenticationManagerBuilder,
    ): AuthenticationManagerBuilder {
        authenticationManagerBuilder.userDetailsService(lostCitiesUserDetailsService)
        return authenticationManagerBuilder
    }

    @Bean
    fun encoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}
