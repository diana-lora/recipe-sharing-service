package net.azeti.recipesharing.user.application

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import net.azeti.recipesharing.core.exceptions.DuplicateUserException
import net.azeti.recipesharing.core.model.CustomUserDetails
import net.azeti.recipesharing.core.model.EmailString
import net.azeti.recipesharing.core.model.StrongPassword
import net.azeti.recipesharing.core.model.Username
import net.azeti.recipesharing.user.domain.model.User
import net.azeti.recipesharing.user.domain.port.CustomAuthenticationManager
import net.azeti.recipesharing.user.domain.port.JwtService
import net.azeti.recipesharing.user.domain.port.PasswordEncoder
import net.azeti.recipesharing.user.domain.service.UserService
import net.azeti.recipesharing.user.infra.api.commands.RegistrationCommand
import net.azeti.recipesharing.user.infra.api.dto.LoginRequest
import org.junit.jupiter.api.assertThrows
import javax.security.sasl.AuthenticationException
import kotlin.test.Test

class UserApplicationServiceTest {
    private val userService: UserService = mockk()
    private val jwtService: JwtService = mockk()
    private val authenticationManager: CustomAuthenticationManager = mockk()
    private val passwordEncoder: PasswordEncoder = mockk(relaxed = true)
    private val userApplicationService = UserApplicationService(userService, jwtService, authenticationManager, passwordEncoder)

    @Test
    fun `register with valid data returns RegisterResponse`() {
        val command = RegistrationCommand(username = Username("newUser"), password = StrongPassword("strongPass123!"), email = EmailString("newuser@example.com"))
        val user =
            User(
                id = 1L,
                username = command.username.value,
                password = command.password.value,
                email = command.email.value,
            )

        val userCaptured = slot<User>()
        every { userService.register(capture(userCaptured)) } returns user

        userApplicationService.register(command)

        userCaptured.captured.shouldNotBeNull().should {
            it.username shouldBe command.username.value
            it.email shouldBe command.email.value
        }
        verify { userService.register(any()) }
    }

    @Test
    fun `register with existing username throws UserAlreadyExistsException`() {
        val command = RegistrationCommand(username = Username("newUser"), password = StrongPassword("strongPass123!"), email = EmailString("newuser@example.com"))

        every { userService.register(any()) } throws DuplicateUserException("username.exists", "User already exists")

        val exception = assertThrows<DuplicateUserException> { userApplicationService.register(command) }

        exception.code shouldBe "username.exists"
        verify { userService.register(any()) }
    }

    @Test
    fun `login with valid credentials returns LoginResponse`() {
        val request = LoginRequest(username = "validUser", password = "validPassword")
        val userDetails = CustomUserDetails(id = 1, username = "validUser", password = "validPassword", email = "user@example.com")
        val token = "validToken"

        every { authenticationManager.authenticate(request.username, request.password) } returns userDetails
        every { jwtService.createToken(userDetails) } returns token

        val response = userApplicationService.login(request)

        response.accessToken shouldBe token
        verify { authenticationManager.authenticate(request.username, request.password) }
        verify { jwtService.createToken(userDetails) }
    }

    @Test
    fun `login with invalid credentials throws AuthenticationException`() {
        val request = LoginRequest(username = "invalidUser", password = "invalidPassword")

        every { authenticationManager.authenticate(request.username, request.password) } throws AuthenticationException("Invalid credentials")

        val exception = assertThrows<AuthenticationException> { userApplicationService.login(request) }

        exception.message shouldBe "Invalid credentials"
        verify { authenticationManager.authenticate(request.username, request.password) }
    }
}
