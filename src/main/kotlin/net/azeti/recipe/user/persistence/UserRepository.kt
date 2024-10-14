package net.azeti.recipe.user.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<UserEntity, Long> {
    fun existsByEmail(email: String): Boolean

    fun existsByUsername(username: String): Boolean

    fun findByUsername(username: String): UserEntity?
}
