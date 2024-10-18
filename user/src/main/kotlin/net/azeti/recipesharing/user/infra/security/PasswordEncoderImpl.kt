package net.azeti.recipesharing.user.infra.security

import net.azeti.recipesharing.core.model.StrongPassword
import net.azeti.recipesharing.user.domain.port.PasswordEncoder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

@Component
class PasswordEncoderImpl(
    private val passwordEncoder: BCryptPasswordEncoder,
) : PasswordEncoder {
    override fun encode(strongPassword: StrongPassword): String {
        return passwordEncoder.encode(strongPassword.value)
    }
}
