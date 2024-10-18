package net.azeti.recipesharing.user.domain.port

import net.azeti.recipesharing.user.domain.model.User

interface UserRepository {
    fun existsByEmail(email: String): Boolean

    fun existsByUsername(username: String): Boolean

    fun findByUsername(username: String): User?

    fun save(user: User): User
}
