package io.dereknelson.lostcities.accounts.config

import io.dereknelson.lostcities.accounts.service.AuthUserDetailsService
import io.dereknelson.lostcities.common.AuthoritiesConstants
import io.dereknelson.lostcities.common.auth.JwtConfigurer
import io.dereknelson.lostcities.common.auth.JwtFilter
import io.dereknelson.lostcities.common.auth.TokenProvider
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
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
                .requestMatchers("/api/accounts/authenticate")
                .requestMatchers("/api/accounts/register")
                .requestMatchers("/**")
                .requestMatchers("/**")

                .requestMatchers("/app/**/*.{js,html}")
                .requestMatchers("/i18n/**")
                .requestMatchers("/content/**")
                .requestMatchers("/h2-console/**")
                .requestMatchers("/swagger-ui/**")
                .requestMatchers("/test/**")
        }
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): DefaultSecurityFilterChain {
        /* ktlint-disable max_line_length */
        // @formatter:off

        http
            .csrf { it.init(http) }
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

                    .requestMatchers(AntPathRequestMatcher("/api/accounts/authenticate")).permitAll()
                    .requestMatchers(AntPathRequestMatcher("/api/accounts/register")).permitAll()
                    .requestMatchers(AntPathRequestMatcher("/api/accounts/activate")).permitAll()
                    .requestMatchers(AntPathRequestMatcher("/api/accounts/reset-password/init")).permitAll()
                    .requestMatchers(AntPathRequestMatcher("/api/accounts/reset-password/finish")).permitAll()
                    .requestMatchers(AntPathRequestMatcher("/api/**")).authenticated()
                    .requestMatchers(AntPathRequestMatcher("/swagger-ui/**")).permitAll()
                    .requestMatchers(AntPathRequestMatcher("/api/admin/**")).hasAuthority(AuthoritiesConstants.ADMIN)
//
                    .requestMatchers(AntPathRequestMatcher("/management/health")).permitAll()
                    .requestMatchers(AntPathRequestMatcher("/management/health/**")).permitAll()
                    .requestMatchers(AntPathRequestMatcher("/management/info")).permitAll()
                    .requestMatchers(AntPathRequestMatcher("/management/prometheus")).permitAll()
                    .requestMatchers(AntPathRequestMatcher("/management/**")).hasAuthority(AuthoritiesConstants.ADMIN)
                    .anyRequest().denyAll()
            }
            .addFilterBefore(jwtFilter(),  UsernamePasswordAuthenticationFilter::class.java)


        /* ktlint-enable max_line_length */
        // @formatter:on
        return http.build()!!
    }

    private fun jwtFilter(): GenericFilterBean {
        return JwtFilter(tokenProvider)
    }

    @Bean
    fun encoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}
