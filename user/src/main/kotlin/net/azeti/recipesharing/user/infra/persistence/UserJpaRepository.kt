package net.azeti.recipesharing.user.infra.persistence

import net.azeti.recipesharing.user.domain.model.User
import net.azeti.recipesharing.user.domain.port.UserRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(
    private val repo: UserJpaRepository,
) : UserRepository {
    override fun existsByEmail(email: String): Boolean = repo.existsByEmail(email)

    override fun existsByUsername(username: String): Boolean = repo.existsByUsername(username)

    override fun findByUsername(username: String): User? {
        return repo.findByUsername(username)?.toModel()
    }

    override fun save(user: User): User = repo.save(user.toEntity()).toModel()
}

@Repository
interface UserJpaRepository : JpaRepository<UserEntity, Long> {
    fun existsByEmail(email: String): Boolean

    fun existsByUsername(username: String): Boolean

    fun findByUsername(username: String): UserEntity?
}

fun User.toEntity() =
    UserEntity(
        id = id,
        username = username,
        email = email,
        password = password,
    )

fun UserEntity.toModel() =
    User(
        id = id,
        username = username,
        email = email,
        password = password,
    )
