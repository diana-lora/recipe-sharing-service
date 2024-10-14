package net.azeti.recipe.api.user

import jakarta.validation.Valid
import net.azeti.recipe.api.user.dto.LoginRequest
import net.azeti.recipe.api.user.dto.LoginResponse
import net.azeti.recipe.api.user.dto.RegistrationRequest
import net.azeti.recipe.api.user.dto.RegistrationResponse
import net.azeti.recipe.core.user.UserService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/users")
class UserController(
    private val userService: UserService,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createUser(
        @Valid @RequestBody registration: RegistrationRequest,
    ): RegistrationResponse = userService.register(registration)

    @PostMapping("/login")
    fun login(
        @Valid @RequestBody loginRequest: LoginRequest,
    ): LoginResponse = userService.login(loginRequest)
}
