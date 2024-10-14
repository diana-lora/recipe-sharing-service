package net.azeti.challenge.recipe

import net.azeti.challenge.recipe.api.user.dto.RegistrationRequest
import net.azeti.challenge.recipe.security.JwtService
import net.azeti.challenge.recipe.security.auth.CustomUserDetails
import net.azeti.challenge.recipe.user.UserEntity
import net.azeti.challenge.recipe.user.UserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

class UserTestHelper(
    private val userRepository: UserRepository,
    private val passwordEncoder: BCryptPasswordEncoder,
    private val jwtService: JwtService,
) {
    fun createUser(registration: RegistrationRequest): CustomUserDetails {
        val user =
            UserEntity(
                email = registration.email,
                username = registration.username,
                password = passwordEncoder.encode(registration.password),
            )
        userRepository.save(user)
        return CustomUserDetails(id = user.id, username = user.username, password = user.password, email = user.email)
    }

    fun findByUsername(username: String) = userRepository.findByUsername(username)

    fun matchPassword(
        raw: String,
        encoded: String,
    ) = passwordEncoder.matches(raw, encoded)

    fun getUsername(token: String) = jwtService.claims(token)?.subject
}
