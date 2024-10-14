package net.azeti.recipe.recipe

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.azeti.recipe.api.recipe.dto.RecipeResponse
import net.azeti.recipe.client.VisualCrossingClient
import net.azeti.recipe.client.WeatherResponse
import net.azeti.recipe.config.props.RecipeRecommendationProperties
import net.azeti.recipe.core.recipe.RecipeRecommendationServiceImpl
import net.azeti.recipe.core.recipe.RecipeService
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class RecipeRecommendationServiceImplTest {
    private val recipeService: RecipeService = mockk()
    private val visualCrossingClient: VisualCrossingClient = mockk()
    private val props = RecipeRecommendationProperties(min = 10.0, max = 25.0)
    private val recommendationService = RecipeRecommendationServiceImpl(recipeService, visualCrossingClient, props)

    @Test
    fun `recommend recipe when temperature is above max`() {
        every { visualCrossingClient.getBerlinWeatherTodayInFahrenheit() } returns mockWeatherResponse(86.0)
        every { recipeService.findByNoBackingRequired() } returns listOf(mockRecipeResponse())

        val result = recommendationService.recommendRecipe(1L)

        assertNotNull(result)
        verify { recipeService.findByNoBackingRequired() }
    }

    @Test
    fun `recommend recipe when temperature is below min`() {
        every { visualCrossingClient.getBerlinWeatherTodayInFahrenheit() } returns mockWeatherResponse(32.0)
        every { recipeService.findByNoFrozenIngredients() } returns listOf(mockRecipeResponse())

        val result = recommendationService.recommendRecipe(1L)

        assertNotNull(result)
        verify { recipeService.findByNoFrozenIngredients() }
    }

    @Test
    fun `recommend recipe when temperature is within range`() {
        every { visualCrossingClient.getBerlinWeatherTodayInFahrenheit() } returns mockWeatherResponse(68.0)
        every { recipeService.findByUser(1L) } returns listOf(mockRecipeResponse())

        val result = recommendationService.recommendRecipe(1L)

        assertNotNull(result)
        verify { recipeService.findByUser(1L) }
    }

    private fun mockWeatherResponse(tempFahrenheit: Double) =
        WeatherResponse(
            days = listOf(WeatherResponse.DayTemp(temp = tempFahrenheit)),
            address = "Berlin,DE",
        )

    private fun mockRecipeResponse() =
        RecipeResponse(
            id = "username_1",
            title = "Test Recipe",
            instructions = "Test instructions",
            ingredients = listOf(),
            servings = 1,
            description = "Test description",
            username = "username",
        )
}
