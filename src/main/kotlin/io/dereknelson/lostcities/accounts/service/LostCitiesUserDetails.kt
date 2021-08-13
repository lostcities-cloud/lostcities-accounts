package io.dereknelson.lostcities.accounts.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User

class LostCitiesUserDetails (
    val id: Long,
    val login: String,
    val email: String,
    val authorities: Set<GrantedAuthority>,
    val accountNonExpired: Boolean,
    val accountNonLocked: Boolean,
    val credentialsNonExpired: Boolean,
    val enabled: Boolean
): User(login, email, authorities)
