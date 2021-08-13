package io.dereknelson.lostcities.accounts.service

import io.dereknelson.lostcities.accounts.persistence.UserRepository
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class LostCitiesUserDetailsService(
    val userRepository: UserRepository
): UserDetailsService {

    override fun loadUserByUsername(username: String): LostCitiesUserDetails {
        return userRepository.findOneByLogin(username)
            .map {
                LostCitiesUserDetails(
                    id = it.id!!,
                    login = it.login,
                    email = it.email,
                    authorities = setOf(),
                    accountNonExpired = true,
                    accountNonLocked = true,
                    credentialsNonExpired = true,
                    enabled = true,
                )
            }.get()
    }
}