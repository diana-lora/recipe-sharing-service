package net.azeti.recipe.security.services

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import net.azeti.recipe.config.props.SecurityJwtProperties
import net.azeti.recipe.security.auth.CustomUserDetails
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

    override fun isValid(claims: Claims): Boolean =
        try {
            claims.expiration.after(Date())
        } catch (e: Exception) {
            false
        }

    override fun claims(token: String): Claims? =
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
