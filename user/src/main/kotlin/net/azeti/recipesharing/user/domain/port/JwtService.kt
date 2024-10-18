package net.azeti.recipesharing.user.domain.port

import net.azeti.recipesharing.core.model.CustomUserDetails
import net.azeti.recipesharing.core.model.UserPrincipal

interface JwtService {
    fun createToken(user: CustomUserDetails): String

    fun username(token: String): String?

    fun getPrincipal(token: String): UserPrincipal?
}
