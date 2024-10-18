package net.azeti.recipesharing.user.infra.security

import net.azeti.recipesharing.core.exceptions.BusinessException
import net.azeti.recipesharing.core.model.CustomUserDetails
import net.azeti.recipesharing.user.domain.port.CustomAuthenticationManager
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Component

@Component
class CustomAuthenticationManagerImpl(
    private val authenticationManager: AuthenticationManager,
) : CustomAuthenticationManager {
    override fun authenticate(
        username: String,
        password: String,
    ): CustomUserDetails {
        val authentication = authenticationManager.authenticate(UsernamePasswordAuthenticationToken(username, password))
        return when (authentication.principal) {
            is CustomUserDetails -> authentication.principal as CustomUserDetails
            else -> throw BusinessException(code = "credentials.invalid", message = "Invalid credentials")
        }
    }
}
