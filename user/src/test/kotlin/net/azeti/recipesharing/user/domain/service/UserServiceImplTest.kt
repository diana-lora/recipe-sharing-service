package net.azeti.recipesharing.user.domain.service

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import net.azeti.recipesharing.core.exceptions.DuplicateUserException
import net.azeti.recipesharing.user.domain.model.User
import net.azeti.recipesharing.user.domain.port.UserRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class UserServiceImplTest {
    private val userRepository = mockk<UserRepository>()
    private val userService = UserServiceImpl(userRepository)

    @Test
    fun `User registers successfully`() {
        val newUser = User(username = "testuser", email = "test@example.com", password = "strongPassword123#")
        every { userRepository.existsByUsername(newUser.username) } returns false
        every { userRepository.existsByEmail(newUser.email) } returns false
        val userCaptured = slot<User>()
        every { userRepository.save(capture(userCaptured)) } returns newUser.copy(id = 1L)

        userService.register(newUser).shouldNotBeNull().should { response ->
            response.id shouldBe 1L
        }

        userCaptured.captured.username shouldBe newUser.username
        userCaptured.captured.email shouldBe newUser.email

        verify { userRepository.save(any()) }
    }

    @Test
    fun `User registers, username is taken, then throw DuplicateUserException`() {
        val newUser = User(username = "testuser", email = "test@example.com", password = "strongPassword123#")
        every { userRepository.existsByUsername(newUser.username) } returns true

        val exception =
            assertThrows<DuplicateUserException> {
                userService.register(newUser)
            }

        exception.code shouldBe "username.exists"
        verify { userRepository.existsByUsername(newUser.username) }
    }

    @Test
    fun `User registers, email is taken, then throw DuplicateUserException`() {
        val newUser = User(username = "testuser", email = "test@example.com", password = "strongPassword123#")
        every { userRepository.existsByUsername(newUser.username) } returns false
        every { userRepository.existsByEmail(newUser.email) } returns true

        val exception =
            assertThrows<DuplicateUserException> {
                userService.register(newUser)
            }

        exception.code shouldBe "email.exists"
        verify {
            userRepository.existsByUsername(newUser.username)
            userRepository.existsByEmail(newUser.email)
        }
    }
}
