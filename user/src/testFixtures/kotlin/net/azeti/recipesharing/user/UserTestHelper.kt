package net.azeti.recipesharing.user

import net.azeti.recipesharing.core.model.CustomUserDetails
import net.azeti.recipesharing.user.domain.model.User
import net.azeti.recipesharing.user.domain.port.UserRepository
import net.azeti.recipesharing.user.infra.api.dto.RegistrationRequest
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

class UserTestHelper(
    private val userRepo: UserRepository,
    private val passwordEncoder: BCryptPasswordEncoder,
) {
    fun createUser(registration: RegistrationRequest): CustomUserDetails {
        val user =
            userRepo.save(
                User(
                    email = registration.email,
                    username = registration.username,
                    password = passwordEncoder.encode(registration.password),
                ),
            )
        return CustomUserDetails(id = user.id, username = user.username, password = user.password, email = user.email)
    }

    fun findByUsername(username: String) = userRepo.findByUsername(username)
}
