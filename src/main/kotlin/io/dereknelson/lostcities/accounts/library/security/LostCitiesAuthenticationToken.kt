package io.dereknelson.lostcities.accounts.library.security

import io.dereknelson.lostcities.common.model.UserRef
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.SpringSecurityCoreVersion


class LostCitiesAuthenticationToken(
    private val userRef: UserRef,
    private val credentials: String?,
    authorities: MutableCollection<GrantedAuthority>?
) : AbstractAuthenticationToken(authorities) {
    private val serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID

    override fun getPrincipal(): Any {
        return userRef
    }

    override fun getCredentials(): Any {
        return credentials!!
    }
}