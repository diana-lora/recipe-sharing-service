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
import net.azeti.recipesharing.user.infra.api.dto.LoginResponse
import net.azeti.recipesharing.utils.mapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class UserControllerTest
    @Autowired
    constructor(
        private val userTestHelper: UserTestHelper,
        private val passwordEncoder: BCryptPasswordEncoder,
    ) : AbstractRestIntegrationTest() {
        @Test
        fun `Register a user, return created`() {
            val user = defaultUserRegistration()
            mockMvc.postRequest(uri = "/v1/users", body = mapper.writeValueAsString(user)).andExpect(status().isCreated)
            userTestHelper.findByUsername(user.username).shouldNotBeNull().should {
                it.email shouldBe user.email
                passwordEncoder.matches(user.password, it.password) shouldBe true
            }
        }

        @Test
        fun `Register user but username already exists, return conflict`() {
            val user = defaultUserRegistration()
            userTestHelper.createUser(user)

            val duplicateUser = defaultUserRegistration(username = user.username)
            mockMvc.postRequest(uri = "/v1/users", body = mapper.writeValueAsString(duplicateUser))
                .andExpect(status().isConflict)
                .andReturn().response.contentAsString
                .let { mapper.readValue<ErrorApi>(it) }
                .shouldNotBeNull()
                .should { it.code shouldBe "username.exists" }
        }

        @Test
        fun `Register user but email already exists, return conflict`() {
            val user = defaultUserRegistration()
            userTestHelper.createUser(user)

            val duplicateUser = defaultUserRegistration(username = "another.username", email = user.email)
            mockMvc.postRequest(uri = "/v1/users", body = mapper.writeValueAsString(duplicateUser))
                .andExpect(status().isConflict)
                .andReturn().response.contentAsString
                .let { mapper.readValue<ErrorApi>(it) }
                .shouldNotBeNull()
                .should { it.code shouldBe "email.exists" }
        }

        @Test
        fun `valid user logs in, then ok`() {
            val registrationRequest = defaultUserRegistration()
            userTestHelper.createUser(registrationRequest)
            val loginRequest = LoginRequest(username = registrationRequest.username, password = registrationRequest.password)
            mockMvc.postRequest(uri = "/v1/users/login", body = mapper.writeValueAsString(loginRequest))
                .andExpect(status().isOk)
                .andReturn().response.contentAsString
                .let { mapper.readValue<LoginResponse>(it) }
                .shouldNotBeNull()
                .should {
                    getUsername(it.accessToken) shouldBe loginRequest.username
                }
        }

        @Test
        fun `Login but user doesn't exist, return forbidden`() {
            val loginRequest = LoginRequest(username = "nonexistentuser", password = "password")
            mockMvc.postRequest(uri = "/v1/users/login", body = mapper.writeValueAsString(loginRequest))
                .andExpect(status().isForbidden)
        }

        @Test
        fun `Login but password doesn't match, return forbidden`() {
            val registrationRequest = defaultUserRegistration()
            userTestHelper.createUser(registrationRequest)
            val loginRequest = LoginRequest(username = registrationRequest.username, password = "wrongPassword123!")
            mockMvc.postRequest(uri = "/v1/users/login", body = mapper.writeValueAsString(loginRequest))
                .andExpect(status().isForbidden)
        }
    }
