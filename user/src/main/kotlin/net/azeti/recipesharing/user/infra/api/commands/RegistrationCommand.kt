package net.azeti.recipesharing.user.infra.api.commands

import net.azeti.recipesharing.core.model.EmailString
import net.azeti.recipesharing.core.model.StrongPassword
import net.azeti.recipesharing.core.model.Username

data class RegistrationCommand(
    val email: EmailString,
    val username: Username,
    val password: StrongPassword,
)
