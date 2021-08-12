package io.dereknelson.lostcities.accounts.service

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.lang.RuntimeException

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "User already exists.")
class UserAlreadyExistsException(message: String): RuntimeException(message)