package io.dereknelson.lostcities.accounts.user

import io.dereknelson.lostcities.common.RoleConstants
import io.dereknelson.lostcities.common.auth.LostCitiesUserDetails
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.security.access.annotation.Secured
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@Tag(name = "User api")
@RestController
@Secured(value = [RoleConstants.USER, RoleConstants.ADMIN])
class UserController(
    private val userService: UserService,
) {
    private val logger: Logger = LoggerFactory.getLogger(UserController::class.java)

    @Operation(summary = "Get the current user.")
    @GetMapping("/user", "/user/")
    fun findUserById(
        @AuthenticationPrincipal @Parameter(hidden = true)
        userDetails: LostCitiesUserDetails,
    ): UserDto? {

        return userService.findById(userDetails.getId())
            .map { UserDto(id = it.id, login = it.login, email = it.email, langKey = it.langKey) }
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND) }
    }

    @Operation(summary = "Find a user.")
    @GetMapping("/user/{id}")
    @Secured(value = [RoleConstants.ADMIN])
    fun findUserById(@PathVariable id: Long): UserDto? {
        return userService.findById(id)
            .map { UserDto(id = it.id, login = it.login, email = it.email, langKey = it.langKey) }
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND) }
    }

    @Operation(summary = "List all users")
    @GetMapping("/users")
    @Secured(value = [RoleConstants.ADMIN])
    fun findAllUsers(page: Pageable): Page<UserDto> {
        return userService.findAll(page)
            .map { UserDto(id = it.id, login = it.login, email = it.email, langKey = it.langKey) }
    }
}
