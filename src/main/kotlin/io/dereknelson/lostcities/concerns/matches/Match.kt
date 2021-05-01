package io.dereknelson.lostcities.concerns.matches

import io.dereknelson.lostcities.concerns.users.User
import java.time.LocalDateTime

data class Match (
    val id: Long,
    val players: UserPair,
    val seed: Long,

    val isReady: Boolean = false,
    val isStarted: Boolean = false,
    val isCompleted: Boolean = false,

    val createdDate: LocalDateTime,
    val lastModifiedDate: LocalDateTime,
    val createdBy: Long
)
