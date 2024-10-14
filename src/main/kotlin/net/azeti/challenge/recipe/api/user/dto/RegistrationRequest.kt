package net.azeti.challenge.recipe.api.user.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class RegistrationRequest(
    @field:Email(message = "email.invalid")
    @field:NotBlank(message = "email.blank")
    val email: String,
    @field:Pattern(
        regexp = "^[a-z][a-z0-9.-]{5,}\$",
        message = "username.invalid",
    )
    val username: String,
    @field:Size(max = 20, message = "password.2long")
    /**
     * ^                               start anchor
     * (?=(.*[a-z]){3,})               lowercase letters. {3,} indicates that you want 3 of this group
     * (?=(.*[A-Z]){2,})               uppercase letters. {2,} indicates that you want 2 of this group
     * (?=(.*[0-9]){2,})               numbers. {2,} indicates that you want 2 of this group
     * (?=(.*[!@#$%^&*()\-__+.]){1,})  all the special characters in the [] fields. The ones used by regex are escaped by using the \ or the character itself. {1,} is redundant, but good practice, in case you change that to more than 1 in the future. Also keeps all the groups consistent
     * {8,}                            indicates that you want 8 or more
     * $                               end anchor
     */
    @field:Pattern(
        regexp = "^(?=(.*[a-z]){3,})(?=(.*[A-Z]){2,})(?=(.*[0-9]){2,})(?=(.*[!@#\$%^&*()\\-__+.]){1,}).{8,}\$",
        message = "password.weak",
    )
    val password: String,
)
