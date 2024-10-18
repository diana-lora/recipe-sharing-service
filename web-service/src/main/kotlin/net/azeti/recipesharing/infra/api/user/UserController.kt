package net.azeti.recipesharing.infra.api.user

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import jakarta.validation.Valid
import net.azeti.recipesharing.core.model.EmailString
import net.azeti.recipesharing.core.model.StrongPassword
import net.azeti.recipesharing.core.model.Username
import net.azeti.recipesharing.infra.api.exception.ErrorApi
import net.azeti.recipesharing.user.application.UserApplicationService
import net.azeti.recipesharing.user.infra.api.commands.RegistrationCommand
import net.azeti.recipesharing.user.infra.api.dto.LoginRequest
import net.azeti.recipesharing.user.infra.api.dto.LoginResponse
import net.azeti.recipesharing.user.infra.api.dto.RegistrationRequest
import net.azeti.recipesharing.user.infra.api.dto.RegistrationResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/users")
class UserController(
    private val userService: UserApplicationService,
) {
    // @formatter:off
    @Operation(summary = "Register a new user")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Successful registration"),
            ApiResponse(responseCode = "400", description = "Invalid email, username or password"),
            ApiResponse(
                responseCode = "409",
                description = "Duplicate user. Either email or username are taken.",
                content = [(Content(mediaType = "application/json", array = (ArraySchema(schema = Schema(implementation = ErrorApi::class)))))],
            ),
        ],
    )
    // @formatter:on
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createUser(
        @Valid @RequestBody request: RegistrationRequest,
    ): RegistrationResponse {
        val command = RegistrationCommand(EmailString(request.email), Username(request.username), StrongPassword(request.password))
        return userService.register(command)
    }

    // @formatter:off
    @Operation(summary = "Login user")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Successful registration"),
            ApiResponse(responseCode = "400", description = "Invalid username or password"),
            ApiResponse(responseCode = "403", description = "Username or password are incorrect"),
        ],
    )
    // @formatter:on
    @PostMapping("/login")
    fun login(
        @Valid @RequestBody request: LoginRequest,
    ): LoginResponse = userService.login(request)
}
