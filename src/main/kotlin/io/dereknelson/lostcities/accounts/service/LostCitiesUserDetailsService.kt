package io.dereknelson.lostcities.accounts.service

import io.dereknelson.lostcities.accounts.persistence.UserRepository
import io.dereknelson.lostcities.common.auth.LostCitiesUserDetails
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class LostCitiesUserDetailsService(
    val userRepository: UserRepository
): UserDetailsService {

    override fun loadUserByUsername(username: String): LostCitiesUserDetails {
        return userRepository.findUserForLogin(username)
            .map {
                LostCitiesUserDetails(
                    id = it.id!!,
                    login = it.login!!,
                    email = it.email!!,
                    authorities = it.authorities
                            .map { authorityEntity -> SimpleGrantedAuthority(authorityEntity.name) }.toSet(),
                    password = it.password!!,
                    accountNonExpired = true,
                    accountNonLocked = true,
                    credentialsNonExpired = true,
                    enabled = true,
                )
            }.orElseThrow { UsernameNotFoundException("User was not found: $username") }
    }
}