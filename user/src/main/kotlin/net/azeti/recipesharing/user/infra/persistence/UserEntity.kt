package net.azeti.recipesharing.user.infra.persistence

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.Email

@Entity
@Table(name = "users")
data class UserEntity(
    val username: String,
    val password: String,
    @Column(unique = true)
    @Email
    val email: String,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
)
