package io.dereknelson.lostcities.accounts.service.exceptions

import org.springframework.security.core.AuthenticationException

class UserNotFoundException(message: String?) : AuthenticationException(message)