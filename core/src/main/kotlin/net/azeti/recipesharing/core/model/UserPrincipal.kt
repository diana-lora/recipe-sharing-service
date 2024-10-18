package net.azeti.recipesharing.core.model

class UserPrincipal(
    id: Long,
    username: String,
    email: String,
) : CustomUserDetails(id = id, email = email, username = username, password = "")
