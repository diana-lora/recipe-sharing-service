package net.azeti.recipesharing.user.domain.service

import net.azeti.recipesharing.core.exceptions.DuplicateUserException
import net.azeti.recipesharing.core.extensions.expectTrueOr
import net.azeti.recipesharing.user.domain.model.User
import net.azeti.recipesharing.user.domain.port.UserRepository
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
) : UserService {
    override fun register(newUser: User): User {
        expectTrueOr(!userRepository.existsByUsername(newUser.username)) { throw DuplicateUserException("username.exists", "Username already exists") }
        expectTrueOr(!userRepository.existsByEmail(newUser.email)) { throw DuplicateUserException("email.exists", "Email already exists") }

        return userRepository.save(newUser)
    }

    override fun findByUsername(username: String): User? = userRepository.findByUsername(username)
}
