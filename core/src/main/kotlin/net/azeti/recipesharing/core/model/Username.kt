package net.azeti.recipesharing.core.model

import net.azeti.recipesharing.core.exceptions.InvalidParameterException
import net.azeti.recipesharing.core.extensions.expectTrueOr

@JvmInline
value class Username(val value: String) {
    init {
        expectTrueOr(value.isNotBlank()) {
            throw InvalidParameterException(
                "username.blank",
                "Username must not be blank",
            )
        }
    }
}
