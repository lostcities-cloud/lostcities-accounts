package io.dereknelson.lostcities.concerns.game

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus


@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "This command could not be played.")
class UnableToPlayCommandException : RuntimeException()
