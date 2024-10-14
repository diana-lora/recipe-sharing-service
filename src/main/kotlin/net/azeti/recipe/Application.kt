package net.azeti.recipe

import net.azeti.recipe.config.props.RecipeRecommendationProperties
import net.azeti.recipe.config.props.SecurityJwtProperties
import net.azeti.recipe.config.props.VisualCrossingProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(SecurityJwtProperties::class, VisualCrossingProperties::class, RecipeRecommendationProperties::class)
class RecipeSharingChallengeKotlinApplication

fun main(args: Array<String>) {
    runApplication<RecipeSharingChallengeKotlinApplication>(*args)
}
