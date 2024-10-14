package net.azeti.recipe.security.auth

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CustomUserDetails(
    val id: Long,
    val email: String,
    private val username: String,
    private val password: String,
) : UserDetails {
    override fun getAuthorities() = emptyList<GrantedAuthority>()

    override fun getPassword() = password

    override fun getUsername() = username

    override fun isAccountNonExpired() = true

    override fun isAccountNonLocked() = true

    override fun isCredentialsNonExpired() = true

    override fun isEnabled() = true
}
