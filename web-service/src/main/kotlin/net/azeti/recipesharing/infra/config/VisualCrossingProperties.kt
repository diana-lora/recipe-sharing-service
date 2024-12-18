package net.azeti.recipesharing.infra.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesScan

@ConfigurationPropertiesScan
@ConfigurationProperties(prefix = "recipe-sharing.visualcrossing")
class VisualCrossingProperties(
    val baseUrl: String,
    val apiKey: String,
)
