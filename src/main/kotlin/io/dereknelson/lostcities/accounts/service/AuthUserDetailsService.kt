package io.dereknelson.lostcities.accounts.service

import io.dereknelson.lostcities.accounts.persistence.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class AuthUserDetailsService(
    val userRepository: UserRepository,
) : UserDetailsService {

    override fun loadUserByUsername(username: String): AuthUserDetails {
        return userRepository.findUserForLogin(username)
            .map {
                AuthUserDetails(
                    id = it.id!!,
                    login = it.login!!,
                    email = it.email!!,
                    authorities = it.authorities
                        .map { authorityEntity -> SimpleGrantedAuthority(authorityEntity.name) }
                        .toSet(),
                    password = it.password!!,
                    accountNonExpired = true,
                    accountNonLocked = true,
                    credentialsNonExpired = true,
                    enabled = true,
                )
            }.orElseThrow { UsernameNotFoundException("User was not found: $username") }
    }
}
