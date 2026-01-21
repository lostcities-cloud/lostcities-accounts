package io.dereknelson.lostcities.accounts.registration

import io.dereknelson.lostcities.accounts.registration.InvalidActivationKeyException
import io.dereknelson.lostcities.accounts.user.UserDto
import io.dereknelson.lostcities.accounts.user.UserService
import io.dereknelson.lostcities.common.model.User
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.modelmapper.ModelMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.Optional

@Tag(name = "Registration api")
@RestController
class RegistrationController(
    private val registrationService: RegistrationService,
    private val modelMapper: ModelMapper,
) {

    private val logger: Logger = LoggerFactory.getLogger(RegistrationController::class.java)

    @Operation(summary = "Register a new user.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = ""),
            ApiResponse(responseCode = "409", description = "User already exists."),
        ],
    )
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@RequestBody registrationDto: RegistrationDto): UserDto? {
        val registration = modelMapper.map(registrationDto, Registration::class.java)
        val user = registrationService.register(registration)

        return UserDto(user.id, user.login, user.email, user.langKey)
    }

    @Operation(summary = "Activate a user.")
    @GetMapping("/activate")
    fun activateAccount(@RequestParam(required = true) key: String) {
        val user: Optional<User> = registrationService.activateRegistration(key)
        user.orElseThrow { InvalidActivationKeyException() }
    }
}
