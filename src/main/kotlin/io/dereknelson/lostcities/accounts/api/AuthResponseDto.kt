package io.dereknelson.lostcities.accounts.api

import com.fasterxml.jackson.annotation.JsonProperty

class AuthResponseDto(
    var login: String,
    var token: String
)