package net.azeti.challenge.recipe.config.props

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesScan

@ConfigurationPropertiesScan
@ConfigurationProperties(prefix = "security.jwt")
class SecurityJwtProperties(
    val secretKey: String,
    val expirationTime: Long,
)
