package net.azeti.challenge.recipe.user

import net.azeti.challenge.recipe.api.exception.DuplicateUserException
import net.azeti.challenge.recipe.api.user.dto.LoginRequest
import net.azeti.challenge.recipe.api.user.dto.LoginResponse
import net.azeti.challenge.recipe.api.user.dto.RegistrationRequest
import net.azeti.challenge.recipe.api.user.dto.RegistrationResponse
import net.azeti.challenge.recipe.extensions.expectTrueOr
import net.azeti.challenge.recipe.security.JwtService
import net.azeti.challenge.recipe.security.auth.CustomUserDetails
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val authenticationManager: AuthenticationManager,
    private val jwtService: JwtService,
    private val passwordEncoder: BCryptPasswordEncoder,
) : UserService {
    private val logger: Log = LogFactory.getLog(this.javaClass)

    override fun register(registration: RegistrationRequest): RegistrationResponse {
        expectTrueOr(!userRepository.existsByUsername(registration.username)) { throw DuplicateUserException("username.exists", "Username already exists") }
        expectTrueOr(!userRepository.existsByEmail(registration.email)) { throw DuplicateUserException("email.exists", "Email already exists") }

        val userEntity =
            UserEntity(
                username = registration.username,
                email = registration.email,
                password = passwordEncoder.encode(registration.password),
            )
        userRepository.save(userEntity)
        return RegistrationResponse(userEntity.id)
    }

    override fun login(loginRequest: LoginRequest): LoginResponse {
        val authentication = authenticationManager.authenticate(UsernamePasswordAuthenticationToken(loginRequest.username, loginRequest.password))
        val userDetails = authentication.principal as CustomUserDetails // FIXME: This is a bad practice
        val token = jwtService.createToken(userDetails)
        return LoginResponse(token)
    }

    override fun findByUsername(username: String): UserEntity? = userRepository.findByUsername(username)
}
