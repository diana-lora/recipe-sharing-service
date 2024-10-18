package net.azeti.recipesharing.user.application

import net.azeti.recipesharing.core.model.CustomUserDetails
import net.azeti.recipesharing.user.domain.model.User
import net.azeti.recipesharing.user.domain.port.CustomAuthenticationManager
import net.azeti.recipesharing.user.domain.port.JwtService
import net.azeti.recipesharing.user.domain.port.PasswordEncoder
import net.azeti.recipesharing.user.domain.service.UserService
import net.azeti.recipesharing.user.infra.api.commands.RegistrationCommand
import net.azeti.recipesharing.user.infra.api.dto.LoginRequest
import net.azeti.recipesharing.user.infra.api.dto.LoginResponse
import net.azeti.recipesharing.user.infra.api.dto.RegistrationResponse
import org.springframework.stereotype.Service

@Service
class UserApplicationService(
    private val userService: UserService,
    private val jwtService: JwtService,
    private val authenticationManager: CustomAuthenticationManager,
    private val passwordEncoder: PasswordEncoder,
) {
    fun register(command: RegistrationCommand): RegistrationResponse {
        val newUser =
            User(
                username = command.username.value,
                email = command.email.value,
                password = passwordEncoder.encode(command.password),
            )
        val user = userService.register(newUser)
        return RegistrationResponse(user.id)
    }

    fun findByUsername(username: String): CustomUserDetails? = userService.findByUsername(username)?.toUserDetails()

    fun login(request: LoginRequest): LoginResponse {
        val userDetails = authenticationManager.authenticate(request.username, request.password)
        val token = jwtService.createToken(userDetails)
        return LoginResponse(token)
    }
}

fun User.toUserDetails() = CustomUserDetails(id = id, username = username, password = password, email = email)
