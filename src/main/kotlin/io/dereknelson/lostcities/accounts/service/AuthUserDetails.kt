package io.dereknelson.lostcities.accounts.service

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User
import io.dereknelson.lostcities.common.model.User as CommonUser

class AuthUserDetails(
    val id: Long,
    val login: String,
    val email: String,
    password: String,
    val authorities: Set<GrantedAuthority>,
    val accountNonExpired: Boolean,
    val accountNonLocked: Boolean,
    val credentialsNonExpired: Boolean,
    val enabled: Boolean
): User(login, password, authorities)