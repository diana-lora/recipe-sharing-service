package net.azeti.recipesharing.user.domain.port

import net.azeti.recipesharing.core.model.CustomUserDetails

interface CustomAuthenticationManager {
    fun authenticate(
        username: String,
        password: String,
    ): CustomUserDetails
}
