package net.azeti.recipesharing.recipe.application

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.azeti.recipesharing.core.defaultUserDetails
import net.azeti.recipesharing.core.exceptions.ActionNotAllowedException
import net.azeti.recipesharing.core.exceptions.RecipeNotFoundException
import net.azeti.recipesharing.core.model.CustomUserDetails
import net.azeti.recipesharing.recipe.domain.model.Recipe
import net.azeti.recipesharing.recipe.domain.port.WeatherService
import net.azeti.recipesharing.recipe.domain.service.RecipeQueryService
import net.azeti.recipesharing.recipe.domain.service.RecipeService
import net.azeti.recipesharing.recipe.infra.api.commmands.CreateRecipeCommand
import net.azeti.recipesharing.recipe.infra.api.commmands.DeleteRecipeCommand
import net.azeti.recipesharing.recipe.infra.api.commmands.IngredientCommand
import net.azeti.recipesharing.recipe.infra.api.commmands.UpdateRecipeCommand
import net.azeti.recipesharing.recipe.infra.api.dto.IngredientUnitsApi
import net.azeti.recipesharing.recipe.infra.config.RecipeRecommendationProperties
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class RecipeApplicationServiceTest {
    private val recipeService: RecipeService = mockk()
    private val recipeQueryService: RecipeQueryService = mockk()
    private val weatherService: WeatherService = mockk()
    private val props = RecipeRecommendationProperties(min = 2.0, max = 28.0)
    private val service = RecipeApplicationService(recipeService, recipeQueryService, weatherService, props)

    @Test
    fun `User creates a recipe successfully`() {
        val command =
            CreateRecipeCommand(
                title = "title",
                description = "description",
                requester = defaultUserDetails(),
                instructions = "instructions",
                servings = 1,
                ingredients = emptyList(),
            )
        every { recipeService.create(any()) } returns mockRecipeResponse()

        val result = service.create(command)

        assertNotNull(result)
        verify { recipeService.create(any()) }
    }

    @Test
    fun `User updates a recipe successfully`() {
        val command =
            UpdateRecipeCommand(
                id = 1,
                title = "title",
                description = "description",
                requester = defaultUserDetails(),
                instructions = "instructions",
                servings = 1,
                ingredients = listOf(IngredientCommand(1.0, IngredientUnitsApi.GRAM, "type")),
            )
        every { recipeQueryService.findById(any()) } returns mockRecipeResponse()
        every { recipeService.update(any()) } returns mockRecipeResponse()

        val result = service.update(command)

        assertNotNull(result)
        verify {
            recipeQueryService.findById(any())
            recipeService.update(any())
        }
    }

    @Test
    fun `User updates a recipe but the recipe doesn't exist, then throw not found exception`() {
        val command =
            UpdateRecipeCommand(
                id = 1,
                title = "title",
                description = "description",
                requester = defaultUserDetails(),
                instructions = "instructions",
                servings = 1,
                ingredients = listOf(IngredientCommand(1.0, IngredientUnitsApi.GRAM, "type")),
            )

        every { recipeQueryService.findById(any()) } returns null

        val exception = assertThrows<RecipeNotFoundException> { service.update(command) }

        exception.code shouldBe "recipe.notFound"
        exception.message shouldBe "Recipe ${command.id} not found"
        verify { recipeQueryService.findById(1L) }
    }

    @Test
    fun `User deletes recipe successfully`() {
        val command = DeleteRecipeCommand(recipeId = 1L, requester = defaultUserDetails())
        every { recipeQueryService.findById(command.recipeId) } returns mockRecipeResponse()
        every { recipeService.delete(command.recipeId) } returns Unit

        service.delete(command)

        verify { recipeService.delete(command.recipeId) }
    }

    @Test
    fun `User deletes recipe, they are not the owner, then throw action not allowed exception`() {
        val user = CustomUserDetails(id = 2L, username = "user2", password = "password", email = "user2@example.com")
        val command = DeleteRecipeCommand(recipeId = 1L, requester = user)
        every { recipeQueryService.findById(command.recipeId) } returns
            Recipe(
                id = 1L,
                title = "Test Recipe",
                instructions = "Test instructions",
                ingredients = listOf(),
                servings = 1,
                description = "Test description",
                username = "username",
            )

        assertThrows<ActionNotAllowedException> {
            service.delete(command)
        }
    }

    @Test
    fun `User deletes recipe, the recipe doesn't exists, then throw not found exception`() {
        val user = CustomUserDetails(id = 2L, username = "user2", password = "password", email = "user2@example.com")
        val command = DeleteRecipeCommand(recipeId = 1L, requester = user)
        every { recipeQueryService.findById(command.recipeId) } returns null

        assertThrows<RecipeNotFoundException> {
            service.delete(command)
        }
    }

    @Test
    fun `recommend a no-baking recipe when temperature is above max`() {
        every { weatherService.getBerlinWeatherTodayInCelsius() } returns 29.0
        every { recipeQueryService.findByNoBackingRequired() } returns mockRecipeResponse()

        val result = service.recommendRecipe()

        assertNotNull(result)
        verify { recipeQueryService.findByNoBackingRequired() }
    }

    @Test
    fun `recommend a no-frozen-ingredients recipe when temperature is below min`() {
        every { weatherService.getBerlinWeatherTodayInCelsius() } returns 1.0
        every { recipeQueryService.findByNoFrozenIngredients() } returns mockRecipeResponse()

        val result = service.recommendRecipe()

        assertNotNull(result)
        verify { recipeQueryService.findByNoFrozenIngredients() }
    }

    @Test
    fun `recommend a random recipe when temperature is within range`() {
        every { weatherService.getBerlinWeatherTodayInCelsius() } returns 20.0
        every { recipeQueryService.findRandom() } returns mockRecipeResponse()

        val result = service.recommendRecipe()

        assertNotNull(result)
        verify { recipeQueryService.findRandom() }
    }

    private fun mockRecipeResponse() =
        Recipe(
            id = 1L,
            title = "Test Recipe",
            instructions = "Test instructions",
            ingredients = listOf(),
            servings = 1,
            description = "Test description",
            username = "username",
        )
}
