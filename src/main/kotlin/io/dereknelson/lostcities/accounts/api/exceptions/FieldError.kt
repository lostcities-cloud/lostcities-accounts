package io.dereknelson.lostcities.accounts.api.exceptions

import java.io.Serializable

class FieldError (
    val objectName: String,
    val field: String,
    val message: String
) : Serializable