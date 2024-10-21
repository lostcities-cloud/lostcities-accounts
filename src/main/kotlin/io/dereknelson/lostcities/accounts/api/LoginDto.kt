package io.dereknelson.lostcities.accounts.api

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Login")
class LoginDto(
    @Schema(example = "ttesterson", required = true)
    val login: String? = null,

    @Schema(example = "test@example.com", required = true)
    val password: String? = null,

    @Schema(example = "test@example.com", required = false, defaultValue = "false")
    val rememberMe: Boolean = false,
)
