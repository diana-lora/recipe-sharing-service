package net.azeti.recipesharing.user.domain.model

data class User(
    val username: String,
    val password: String,
    val email: String,
    var id: Long = 0,
)
