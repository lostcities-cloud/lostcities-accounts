package io.dereknelson.lostcities.accounts.library.security

import io.dereknelson.lostcities.accounts.persistence.UserEntity
import io.dereknelson.lostcities.accounts.persistence.UserRepository
import io.dereknelson.lostcities.accounts.service.exceptions.UserNotActivatedException
import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator
import org.slf4j.LoggerFactory
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*
import java.util.stream.Collectors

@Component("userDetailsService")
class CustomUserDetailsService(private val userRepository: UserRepository) : UserDetailsService {
    private val log = LoggerFactory.getLogger(CustomUserDetailsService::class.java)

    @Transactional
    override fun loadUserByUsername(login: String): UserDetails {
        log.debug("Authenticating {}", login)
        if (EmailValidator().isValid(login, null)) {
            return userRepository
                .findOneWithAuthoritiesByEmailIgnoreCase(login)
                .map { user -> createSpringSecurityUser(login, user) }
                .orElseThrow {
                    UsernameNotFoundException(
                        "User with email $login was not found in the database",
                    )
                }
        }
        val lowercaseLogin = login.lowercase(Locale.ENGLISH)
        return userRepository
            .findOneWithAuthoritiesByLogin(lowercaseLogin)
            .map { user -> createSpringSecurityUser(lowercaseLogin, user) }
            .orElseThrow {
                UsernameNotFoundException("User $lowercaseLogin was not found in the database")
            }
    }

    private fun createSpringSecurityUser(
        lowercaseLogin: String,
        userEntity: UserEntity,
    ): User {
        if (!userEntity.activated) {
            throw UserNotActivatedException("User $lowercaseLogin was not activated")
        }
        val grantedAuthorities: List<GrantedAuthority> = userEntity
            .authorities
            .stream()
            .map { authority -> SimpleGrantedAuthority(authority.name) }
            .collect(Collectors.toList())

        return User(
            userEntity.login!!,
            userEntity.password!!,
            grantedAuthorities,
        )
    }
}
