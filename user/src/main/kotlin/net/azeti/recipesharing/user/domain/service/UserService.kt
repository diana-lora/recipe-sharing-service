package net.azeti.recipesharing.user.domain.service

import net.azeti.recipesharing.user.domain.model.User

interface UserService {
    fun register(newUser: User): User

    fun findByUsername(username: String): User?
}
