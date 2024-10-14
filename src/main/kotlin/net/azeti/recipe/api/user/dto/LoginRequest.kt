package net.azeti.recipe.api.user.dto

import jakarta.validation.constraints.NotBlank

data class LoginRequest(
    @field:NotBlank(message = "username.blank")
    val username: String,
    @field:NotBlank(message = "password.blank")
    val password: String,
)
