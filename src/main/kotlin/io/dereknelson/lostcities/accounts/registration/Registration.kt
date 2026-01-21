package io.dereknelson.lostcities.accounts.registration

import io.dereknelson.lostcities.common.Constants
import io.dereknelson.lostcities.common.model.Role
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class Registration(
    val login: String,
    val email: String,
    var password:
    @NotNull
    @Size(min = 60, max = 60)
    String,
    val firstName:
    @NotNull
    @Size(max = 50)
    String,
    val lastName:
    @Size(max = 50)
    String?,
    val langKey:
    @NotNull
    @Size(min = 2, max = 10)
    String = Constants.DEFAULT_LANGUAGE,
    val authorities:
    @Size(min = 1)
    Set<Role> = setOf(Role.USER),
)
