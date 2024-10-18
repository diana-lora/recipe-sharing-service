package net.azeti.recipesharing.core.model

import net.azeti.recipesharing.core.exceptions.InvalidParameterException
import net.azeti.recipesharing.core.extensions.expectTrueOr
import java.util.regex.Pattern

@JvmInline
value class EmailString(val value: String) {
    init {
        expectTrueOr(value.isNotBlank()) { throw InvalidParameterException("email.blank", "Email must not be blank") }
        expectTrueOr(emailMatcher.matcher(value).matches()) {
            throw InvalidParameterException(
                "email.invalid",
                "Email is invalid",
            )
        }
    }

    companion object {
        private const val EMAIL_REGEX = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+\$"
        private val emailMatcher = Pattern.compile(EMAIL_REGEX)
    }
}
