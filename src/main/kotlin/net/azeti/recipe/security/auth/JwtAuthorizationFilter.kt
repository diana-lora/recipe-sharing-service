package net.azeti.recipe.security.auth

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import net.azeti.recipe.security.JwtService
import net.azeti.recipe.security.auth.JwtAuthorizationHeader.TOKEN_HEADER
import net.azeti.recipe.security.auth.JwtAuthorizationHeader.TOKEN_PREFIX
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthorizationFilter(
    private val jwtService: JwtService,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        try {
            val accessToken = resolveToken(request)
            if (accessToken == null) {
                filterChain.doFilter(request, response)
                return
            }
            val claims = jwtService.claims(accessToken)

            if (claims != null && jwtService.isValid(claims)) {
                val username = claims.subject
                val email = claims["email"] as String // FIXME unchecked cast is bad practice
                val userId = claims["userId"] as Int
                val principal = UserPrincipal(userId.toLong(), username, email)
                val authentication: Authentication =
                    UsernamePasswordAuthenticationToken(principal, "", ArrayList())
                SecurityContextHolder.getContext().authentication = authentication
            }
        } catch (e: Exception) {
            logger.error("Failed authentication.", e)
        }
        filterChain.doFilter(request, response)
    }

    fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader(TOKEN_HEADER)
        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length)
        }
        return null
    }
}

object JwtAuthorizationHeader {
    const val TOKEN_HEADER = "Authorization"
    const val TOKEN_PREFIX = "Bearer "
}
