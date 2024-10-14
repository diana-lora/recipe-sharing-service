package net.azeti.challenge.recipe

import net.azeti.challenge.recipe.config.props.RecipeRecommendationProperties
import net.azeti.challenge.recipe.config.props.SecurityJwtProperties
import net.azeti.challenge.recipe.config.props.VisualCrossingProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(SecurityJwtProperties::class, VisualCrossingProperties::class, RecipeRecommendationProperties::class)
class RecipeSharingChallengeKotlinApplication

fun main(args: Array<String>) {
    runApplication<RecipeSharingChallengeKotlinApplication>(*args)
}
