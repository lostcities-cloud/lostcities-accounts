package io.dereknelson.lostcities.accounts.registration

import io.dereknelson.lostcities.common.Constants
import io.dereknelson.lostcities.common.RoleConstants
import io.swagger.v3.oas.annotations.media.Schema
import java.util.Collections

@Schema(description = "Registration")
class RegistrationDto(

    @Schema(example = "ttesterson", required = true)
    val login: String,

    @Schema(example = "test@example.com", required = true)
    val email: String,

    @Schema(example = "p@ssword", required = true)
    val password: String,

    @Schema(example = "Test", required = true)
    val firstName: String = "unknown-first-name",

    @Schema(example = "Testerson", required = true)
    val lastName: String?,

    @Schema(example = "en_US", required = true)
    val langKey: String = Constants.DEFAULT_LANGUAGE,

    @Schema(example = "AuthoritiesConstants.USER", required = true)
    initialAuthorities: Set<String> = setOf(RoleConstants.USER),
) {
    val authorities: Set<String> = Collections.unmodifiableSet(initialAuthorities)
}
