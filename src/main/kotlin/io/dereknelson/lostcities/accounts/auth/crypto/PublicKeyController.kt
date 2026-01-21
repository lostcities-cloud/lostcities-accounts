package io.dereknelson.lostcities.accounts.auth.crypto

import io.dereknelson.lostcities.common.auth.PublicKeyDto
import io.dereknelson.lostcities.common.auth.TokenProvider
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.Base64

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
