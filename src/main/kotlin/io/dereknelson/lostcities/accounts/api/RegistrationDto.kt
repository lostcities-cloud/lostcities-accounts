package io.dereknelson.lostcities.accounts.api

import io.dereknelson.lostcities.common.AuthoritiesConstants
import io.dereknelson.lostcities.common.Constants
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Registration")
data class RegistrationDto(

    @Schema(example = "ttesterson", required = true)
    var login: String,

    @Schema(example = "test@example.com", required = true)
    var email: String,

    @Schema(example = "p@ssword", required = true)
    var password: String,

    @Schema(example = "Test", required = true)
    var firstName: String,

    @Schema(example = "Testerson", required = true)
    var lastName: String?,

    @Schema(example = "en_US", required = true)
    var langKey: String = Constants.DEFAULT_LANGUAGE,

    @Schema(example = "AuthoritiesConstants.USER", required = true)
    var authorities: Set<String> = setOf(AuthoritiesConstants.USER),
)
