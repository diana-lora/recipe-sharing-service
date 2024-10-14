package net.azeti.recipe.security

import net.azeti.recipe.api.exception.UnauthorizedException
import net.azeti.recipe.security.auth.UserPrincipal
import org.springframework.security.core.context.SecurityContextHolder

object SecurityContextUtils {
    fun principal(): UserPrincipal =
        SecurityContextHolder.getContext().authentication?.principal?.let {
            if (it is UserPrincipal) it else null
        } ?: throw UnauthorizedException("No principal in the token.")
}
