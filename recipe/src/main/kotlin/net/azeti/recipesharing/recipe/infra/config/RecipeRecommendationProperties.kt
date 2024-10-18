package net.azeti.recipesharing.recipe.infra.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesScan

@ConfigurationPropertiesScan
@ConfigurationProperties(prefix = "recipe-sharing.recommendations.temperature.celsius")
class RecipeRecommendationProperties(
    val max: Double,
    val min: Double,
)
