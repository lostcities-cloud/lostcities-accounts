package io.dereknelson.lostcities.accounts.user

import org.springframework.security.core.AuthenticationException

class UserNotFoundException(message: String?) : AuthenticationException(message)
