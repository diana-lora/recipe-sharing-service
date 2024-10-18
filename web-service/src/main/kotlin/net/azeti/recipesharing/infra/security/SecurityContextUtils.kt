package net.azeti.recipesharing.infra.security

import net.azeti.recipesharing.core.exceptions.ActionNotAllowedException
import net.azeti.recipesharing.core.model.UserPrincipal
import org.springframework.security.core.context.SecurityContextHolder

object SecurityContextUtils {
    fun principal(): UserPrincipal =
        SecurityContextHolder.getContext().authentication?.principal?.let {
            if (it is UserPrincipal) it else null
        } ?: throw ActionNotAllowedException("No principal in the token.")
}
