package net.azeti.recipesharing.user.domain.port

import net.azeti.recipesharing.core.model.StrongPassword

interface PasswordEncoder {
    fun encode(rawPassword: StrongPassword): String
}
