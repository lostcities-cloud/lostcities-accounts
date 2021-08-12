package io.dereknelson.lostcities.accounts.api

import com.fasterxml.jackson.annotation.JsonProperty

class JwtTokenDto(
    @get:JsonProperty("id_token") var idToken: String
)