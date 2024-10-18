package net.azeti.recipesharing.infra.api.user

import com.fasterxml.jackson.module.kotlin.readValue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import net.azeti.recipesharing.AbstractRestIntegrationTest
import net.azeti.recipesharing.helpers.defaultUserRegistration
import net.azeti.recipesharing.helpers.postRequest
import net.azeti.recipesharing.infra.api.exception.ErrorApi
import net.azeti.recipesharing.user.UserTestHelper
import net.azeti.recipesharing.user.infra.api.dto.LoginRequest
import net.azeti.recipesharing.utils.mapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class UserControllerValidationTest
    @Autowired
    constructor(
        private val userTestHelper: UserTestHelper,
    ) : AbstractRestIntegrationTest() {
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
                .should { it.code shouldBe "username.blank" }
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
