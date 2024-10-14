package net.azeti.recipe.user

import net.azeti.recipe.api.user.dto.LoginRequest
import net.azeti.recipe.api.user.dto.LoginResponse
import net.azeti.recipe.api.user.dto.RegistrationRequest
import net.azeti.recipe.api.user.dto.RegistrationResponse

interface UserService {
    fun register(registrationRequest: RegistrationRequest): RegistrationResponse

    fun login(loginRequest: LoginRequest): LoginResponse

    fun findByUsername(username: String): UserEntity?
}
