package net.azeti.recipe.recipe

import net.azeti.recipe.api.recipe.dto.RecipeResponse
import net.azeti.recipe.client.VisualCrossingClient
import net.azeti.recipe.config.props.RecipeRecommendationProperties
import net.azeti.recipe.extensions.ifNotEmpty
import org.springframework.stereotype.Service

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
