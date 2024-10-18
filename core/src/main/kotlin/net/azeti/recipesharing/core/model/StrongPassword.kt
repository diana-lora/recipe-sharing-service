package net.azeti.recipesharing.core.model

import net.azeti.recipesharing.core.exceptions.InvalidParameterException
import net.azeti.recipesharing.core.extensions.expectTrueOr
import java.util.regex.Pattern

@JvmInline
value class StrongPassword(val value: String) {
    init {
        expectTrueOr(passwordMatcher.matcher(value).matches()) {
            throw InvalidParameterException(
                "password.weak",
                "A strong password should have at least 3 lower case letters, 1 upper case letter, 1 number, and 1 special character (!@#\$%^&*()\\-__+.).",
            )
        }
    }

    companion object {
        /**
         * ^                               start anchor
         * (?=(.*[a-z]){3,})               lowercase letters. {3,} indicates that you want 3 of this group
         * (?=(.*[A-Z]){1,})               uppercase letters. {1,} indicates that you want 1 of this group
         * (?=(.*[0-9]){2,})               numbers. {1,} indicates that you want 1 of this group
         * (?=(.*[!@#$%^&*()\-__+.]){1,})  all the special characters in the [] fields. The ones used by regex are escaped by using the \ or the character itself. {1,} is redundant, but good practice, in case you change that to more than 1 in the future. Also keeps all the groups consistent
         * {8,}                            indicates that you want 8 or more
         * $                               end anchor
         */
        private const val STRONG_PASSWORD_REGEX = "^(?=(.*[a-z]){3,})(?=(.*[A-Z]){1,})(?=(.*[0-9]){2,})(?=(.*[!@#\$%^&*()\\-__+.]){1,}).{8,}\$"
        private val passwordMatcher = Pattern.compile(STRONG_PASSWORD_REGEX)
    }
}
