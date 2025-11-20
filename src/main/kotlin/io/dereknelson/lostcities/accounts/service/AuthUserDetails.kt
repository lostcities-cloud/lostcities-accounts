package io.dereknelson.lostcities.accounts.service

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User
import java.util.Collections

class AuthUserDetails: User {
    val id: Long
    val login: String
    val email: String
    val authorities: Set<GrantedAuthority>
    val accountNonExpired: Boolean
    val accountNonLocked: Boolean
    val credentialsNonExpired: Boolean
    val enabled: Boolean

    companion object {
        fun create(
            id: Long,
            login: String,
            email: String,
            password: String,
            authorities: Set<GrantedAuthority>,
            accountNonExpired: Boolean,
            accountNonLocked: Boolean,
            credentialsNonExpired: Boolean,
            enabled: Boolean,
        ): AuthUserDetails {
            return AuthUserDetails(
                id,
                login,
                email,
                password,
                Collections.unmodifiableSet(authorities),
                accountNonExpired,
                accountNonLocked,
                credentialsNonExpired,
                enabled,

            )
        }
    }

    private constructor(
        id: Long,
        login: String,
        email: String,
        password: String,
        authorities: Set<GrantedAuthority>,
        accountNonExpired: Boolean,
        accountNonLocked: Boolean,
        credentialsNonExpired: Boolean,
        enabled: Boolean,
    ): super(login, password, authorities) {
        this.id = id
        this.login = login
        this.email = email
        this.authorities = authorities
        this.accountNonExpired = accountNonExpired
        this.accountNonLocked = accountNonLocked
        this.credentialsNonExpired = credentialsNonExpired
        this.enabled = enabled
    }
}
