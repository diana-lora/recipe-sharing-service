package net.azeti.challenge.recipe.recipe

import net.azeti.challenge.recipe.api.recipe.dto.RecipeResponse
import net.azeti.challenge.recipe.client.VisualCrossingClient
import net.azeti.challenge.recipe.config.props.RecipeRecommendationProperties
import net.azeti.challenge.recipe.extensions.ifNotEmpty
import org.springframework.stereotype.Service

interface RecipeRecommendationService {
    fun recommendRecipe(userId: Long): RecipeResponse?
}

@Service
class RecipeRecommendationServiceImpl(
    private val recipeService: RecipeService,
    private val visualCrossingClient: VisualCrossingClient,
    private val props: RecipeRecommendationProperties,
) : RecipeRecommendationService {
    override fun recommendRecipe(userId: Long): RecipeResponse? {
        val weather = visualCrossingClient.getBerlinWeatherTodayInFahrenheit()
        val tempCelsius = fromFahrenheitToCelsius(weather.days.first().temp)

        val recommendations =
            when {
                tempCelsius > props.max -> recipeService.findByNoBackingRequired()
                tempCelsius < props.min -> recipeService.findByNoFrozenIngredients()
                else -> recipeService.findByUser(userId)
            }

        return recommendations.ifNotEmpty { it.random() }
    }

    private fun fromFahrenheitToCelsius(value: Double) = (value - 32) / 1.8
}
