package net.azeti.recipesharing.infra.security

import net.azeti.recipesharing.core.model.CustomUserDetails
import net.azeti.recipesharing.user.domain.port.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(private val userRepository: UserRepository) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByUsername(username) ?: throw UsernameNotFoundException("User not found")
        return CustomUserDetails(id = user.id, username = user.username, password = user.password, email = user.email)
    }
}
