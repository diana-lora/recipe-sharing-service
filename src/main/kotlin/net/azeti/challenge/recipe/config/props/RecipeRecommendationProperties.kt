package net.azeti.challenge.recipe.config.props

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesScan

@ConfigurationPropertiesScan
@ConfigurationProperties(prefix = "recipe-sharing.recommendations.temperature.celsius")
class RecipeRecommendationProperties(
    val max: Double,
    val min: Double,
)
