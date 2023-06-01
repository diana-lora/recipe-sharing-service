package net.azeti.challenge.recipe.user

import java.util.Optional

interface UserManagement {

    fun register(registration: Registration): RegistrationResult

    fun login(login: Login): Optional<Token>
}