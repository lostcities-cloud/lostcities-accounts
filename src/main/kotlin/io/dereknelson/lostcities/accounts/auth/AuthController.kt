package io.dereknelson.lostcities.accounts.auth

import io.dereknelson.lostcities.accounts.user.UserService
import io.dereknelson.lostcities.common.auth.JwtFilter
import io.dereknelson.lostcities.common.auth.TokenProvider
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@Tag(name = "Authentication API")
@RestController

class AuthController(
    private val userService: UserService,
    private val authenticationManager: AuthenticationManager,
    private val tokenProvider: TokenProvider,
) {

    private val log: Logger = LoggerFactory.getLogger(AuthController::class.java)

    @Operation(summary = "Get my current user information.")
    @GetMapping("/authenticate")
    fun isAuthenticated(request: HttpServletRequest): String? {
        log.info("REST request to check if the current user is authenticated")
        return request.remoteUser
    }

    @Operation(summary = "Authenticate a user.")
    @PostMapping("/authenticate")
    @PreAuthorize("permitAll()")
    fun authorize(@Valid @RequestBody loginDto: LoginDto): ResponseEntity<AuthResponseDto> {

        val authenticationToken = UsernamePasswordAuthenticationToken(
            loginDto.login,
            loginDto.password,
        )

        val authentication = authenticationManager.authenticate(authenticationToken)

        SecurityContextHolder.getContext().authentication = authentication


        val userRef = userService.findRefByLogin(loginDto.login!!).get()
        val jwt = tokenProvider.createToken(authentication, userRef, loginDto.rememberMe)
        val httpHeaders = HttpHeaders()
        httpHeaders.add(JwtFilter.Companion.AUTHORIZATION_HEADER, "Bearer $jwt")

        return ResponseEntity<AuthResponseDto>(
            AuthResponseDto(loginDto.login, jwt),
            httpHeaders,
            HttpStatus.OK,
        )
    }
}
