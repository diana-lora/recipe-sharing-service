package net.azeti.recipesharing.infra.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import net.azeti.recipesharing.infra.security.JwtAuthorizationHeader.TOKEN_HEADER
import net.azeti.recipesharing.infra.security.JwtAuthorizationHeader.TOKEN_PREFIX
import net.azeti.recipesharing.user.domain.port.JwtService
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
            jwtService.getPrincipal(accessToken)?.let { principal ->
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
