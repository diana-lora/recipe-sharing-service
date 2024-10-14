package net.azeti.recipe.core.user

import net.azeti.recipe.api.user.dto.LoginRequest
import net.azeti.recipe.api.user.dto.LoginResponse
import net.azeti.recipe.api.user.dto.RegistrationRequest
import net.azeti.recipe.api.user.dto.RegistrationResponse
import net.azeti.recipe.security.auth.CustomUserDetails

interface UserService {
    fun register(registrationRequest: RegistrationRequest): RegistrationResponse

    fun login(loginRequest: LoginRequest): LoginResponse

    fun findByUsername(username: String): CustomUserDetails?
}
