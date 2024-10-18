package net.azeti.recipesharing.user.infra.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import net.azeti.recipesharing.core.model.CustomUserDetails
import net.azeti.recipesharing.core.model.UserPrincipal
import net.azeti.recipesharing.user.domain.port.JwtService
import org.springframework.stereotype.Service
import java.util.Date
import java.util.concurrent.TimeUnit

@Service
class JwtServiceImpl(
    private val props: SecurityJwtProperties,
) : JwtService {
    private val signInKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(props.secretKey))

    override fun createToken(user: CustomUserDetails): String {
        val claims = Jwts.claims().setSubject(user.username)
        claims["userId"] = user.id
        claims["email"] = user.email
        val tokenCreateTime = Date()
        val tokenValidity = Date(tokenCreateTime.time + TimeUnit.MINUTES.toMillis(props.expirationTime))
        return Jwts.builder()
            .setClaims(claims)
            .setExpiration(tokenValidity)
            .signWith(signInKey, SignatureAlgorithm.HS256)
            .compact()
    }

    override fun username(token: String): String? = claims(token)?.subject

    override fun getPrincipal(token: String): UserPrincipal? =
        claims(token)?.let { claims ->
            if (!isValid(claims)) return null
            val username = claims.subject
            val email = claims["email"] as String
            val userId = claims["userId"] as Int
            UserPrincipal(userId.toLong(), username, email)
        }

    private fun isValid(claims: Claims): Boolean =
        try {
            claims.expiration.after(Date())
        } catch (e: Exception) {
            false
        }

    private fun claims(token: String): Claims? =
        try {
            Jwts
                .parserBuilder()
                .setSigningKey(signInKey)
                .build()
                .parseClaimsJws(token)
                .body
        } catch (e: Exception) {
            null
        }
}
