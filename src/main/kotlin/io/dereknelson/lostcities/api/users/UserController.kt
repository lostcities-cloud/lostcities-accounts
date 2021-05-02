package io.dereknelson.lostcities.api.users

import io.dereknelson.lostcities.concerns.users.UserService
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController
@RequestMapping("/api/users", produces=[MediaType.APPLICATION_JSON_VALUE])
class UserController {

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var modelMapper : ModelMapper

    @GetMapping("{id}", produces=[MediaType.APPLICATION_JSON_VALUE])
    fun findUserById(@PathVariable  id: Long) : UserDto? {
        return userService.findById(id)
            .map { modelMapper.map(it, UserDto::class.java) }
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND) }
    }
}