package net.azeti.recipe.api.user

import com.fasterxml.jackson.module.kotlin.readValue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import net.azeti.recipe.AbstractRestIntegrationTest
import net.azeti.recipe.api.exception.ErrorApi
import net.azeti.recipe.api.user.dto.LoginRequest
import net.azeti.recipe.helpers.UserTestHelper
import net.azeti.recipe.helpers.defaultUserRegistration
import net.azeti.recipe.helpers.postRequest
import net.azeti.recipe.utils.mapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class UserControllerValidationTest
    @Autowired
    constructor(
        private val userTestHelper: UserTestHelper,
    ) : AbstractRestIntegrationTest() {
        @Test
        fun `Register user but username has underscore, return bad request`() {
            val user = defaultUserRegistration(username = "invalid_username")
            mockMvc.postRequest(uri = "/v1/users", body = mapper.writeValueAsString(user))
                .andExpect(status().isBadRequest)
                .andReturn().response.contentAsString
                .let { mapper.readValue<ErrorApi>(it) }
                .shouldNotBeNull()
                .should { it.code shouldBe "username.invalid" }
        }

        @Test
        fun `Register user but email is empty, return bad request`() {
            val user = defaultUserRegistration(email = "")
            mockMvc.postRequest(uri = "/v1/users", body = mapper.writeValueAsString(user))
                .andExpect(status().isBadRequest)
                .andReturn().response.contentAsString
                .let { mapper.readValue<ErrorApi>(it) }
                .shouldNotBeNull()
                .should { it.code shouldBe "email.blank" }
        }

        @Test
        fun `Register user but email is invalid, return bad request`() {
            val user = defaultUserRegistration(email = "invalid-email")
            mockMvc.postRequest(uri = "/v1/users", body = mapper.writeValueAsString(user))
                .andExpect(status().isBadRequest)
                .andReturn().response.contentAsString
                .let { mapper.readValue<ErrorApi>(it) }
                .shouldNotBeNull()
                .should { it.code shouldBe "email.invalid" }
        }

        @Test
        fun `Register user but username is empty, return bad request`() {
            val user = defaultUserRegistration(username = "")
            mockMvc.postRequest(uri = "/v1/users", body = mapper.writeValueAsString(user))
                .andExpect(status().isBadRequest)
                .andReturn().response.contentAsString
                .let { mapper.readValue<ErrorApi>(it) }
                .shouldNotBeNull()
                .should { it.code shouldBe "username.invalid" }
        }

        @Test
        fun `Register user but password is empty, return bad request`() {
            val user = defaultUserRegistration(password = "")
            mockMvc.postRequest(uri = "/v1/users", body = mapper.writeValueAsString(user))
                .andExpect(status().isBadRequest)
                .andReturn().response.contentAsString
                .let { mapper.readValue<ErrorApi>(it) }
                .shouldNotBeNull()
                .should { it.code shouldBe "password.weak" }
        }

        @Test
        fun `Register user but password is weak, return bad request`() {
            val user = defaultUserRegistration(password = "12345")
            mockMvc.postRequest(uri = "/v1/users", body = mapper.writeValueAsString(user))
                .andExpect(status().isBadRequest)
                .andReturn().response.contentAsString
                .let { mapper.readValue<ErrorApi>(it) }
                .shouldNotBeNull()
                .should { it.code shouldBe "password.weak" }
        }

        @Test
        fun `Register user but password is too long, return bad request`() {
            val user = defaultUserRegistration(password = "StrongAndSuperLongPassword1234567890!@#$%^&*()")
            mockMvc.postRequest(uri = "/v1/users", body = mapper.writeValueAsString(user))
                .andExpect(status().isBadRequest)
                .andReturn().response.contentAsString
                .let { mapper.readValue<ErrorApi>(it) }
                .shouldNotBeNull()
                .should { it.code shouldBe "password.2long" }
        }

        @Test
        fun `Login with empty username, return bad request`() {
            val loginRequest = LoginRequest(username = "", password = "password")
            mockMvc.postRequest(uri = "/v1/users/login", body = mapper.writeValueAsString(loginRequest))
                .andExpect(status().isBadRequest)
                .andReturn().response.contentAsString
                .let { mapper.readValue<ErrorApi>(it) }
                .shouldNotBeNull()
                .should { it.code shouldBe "username.blank" }
        }

        @Test
        fun `Login with empty password, return bad request`() {
            userTestHelper.createUser(defaultUserRegistration())
            val loginRequest = LoginRequest(username = "username", password = "")
            mockMvc.postRequest(uri = "/v1/users/login", body = mapper.writeValueAsString(loginRequest))
                .andExpect(status().isBadRequest)
                .andReturn().response.contentAsString
                .let { mapper.readValue<ErrorApi>(it) }
                .shouldNotBeNull()
                .should { it.code shouldBe "password.blank" }
        }
    }
