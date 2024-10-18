package net.azeti.recipesharing.user.infra.api.dto

data class RegistrationRequest(
    val email: String,
    val username: String,
    val password: String,
)
