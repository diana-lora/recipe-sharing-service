package net.azeti.recipe.security.services

import io.jsonwebtoken.Claims
import net.azeti.recipe.security.auth.CustomUserDetails

interface JwtService {
    fun createToken(user: CustomUserDetails): String

    fun isValid(claims: Claims): Boolean

    fun claims(token: String): Claims?
}
