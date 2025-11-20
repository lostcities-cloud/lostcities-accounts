package io.dereknelson.lostcities.accounts.keys

import io.dereknelson.lostcities.accounts.api.AuthResponseDto
import io.dereknelson.lostcities.accounts.api.InvalidActivationKeyException
import io.dereknelson.lostcities.accounts.api.LoginDto
import io.dereknelson.lostcities.accounts.api.RegistrationDto
import io.dereknelson.lostcities.accounts.api.UserDto
import io.dereknelson.lostcities.accounts.service.Registration
import io.dereknelson.lostcities.common.auth.JwtFilter
import io.dereknelson.lostcities.common.auth.LostCitiesUserDetails
import io.dereknelson.lostcities.common.auth.PublicKeyDto
import io.dereknelson.lostcities.common.auth.TokenProvider
import io.dereknelson.lostcities.common.model.User
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.util.Base64
import java.util.Optional

@Tag(name = "User actions")
@RestController
class PublicKeyController(
    private val tokenProvider: TokenProvider,
) {

    private val log: Logger = LoggerFactory.getLogger(PublicKeyController::class.java)

    @Operation(summary = "Get Public Key")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Returns the current public Key"),
            ApiResponse(responseCode = "409", description = "User already exists."),
        ],
    )
    @GetMapping("/public-key")
    @ResponseStatus(HttpStatus.OK)
    fun getPublicKey(): PublicKeyDto {
        return PublicKeyDto(
            Base64.getEncoder().encodeToString(tokenProvider.publicKey.encoded),
            tokenProvider.publicKey.algorithm,
            tokenProvider.publicKey.format
        )
    }
}
