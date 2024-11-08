package net.azeti.recipesharing.recipe.domain.service

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.azeti.recipesharing.recipe.domain.model.Ingredient
import net.azeti.recipesharing.recipe.domain.model.IngredientUnits
import net.azeti.recipesharing.recipe.domain.model.RecipeAggregate
import net.azeti.recipesharing.recipe.domain.port.RecipeRepository
import org.junit.jupiter.api.Test

class RecipeQueryServiceImplTest {
    private val recipeRepository: RecipeRepository = mockk()
    private val recipeQueryService = RecipeQueryServiceImpl(recipeRepository)

    @Test
    fun `search with username and title returns matching recipes`() {
        val username = "user1"
        val title = "title1"
        val expectedRecipes = listOf(mockRecipeAggregate())
        every { recipeRepository.search(username, title) } returns expectedRecipes

        val result = recipeQueryService.search(username, title, null)

        result shouldBe expectedRecipes.map { it.toDomain() }
        verify { recipeRepository.search(username, title) }
    }

    @Test
    fun `search with expected servings adjusts ingredients`() {
        val username = "user1"
        val title = "title1"
        val expectedServings = 4
        val recipeAggregate = mockRecipeAggregate(servings = 2)
        every { recipeRepository.search(username, title) } returns listOf(recipeAggregate)

        val result = recipeQueryService.search(username, title, expectedServings)

        result.first().shouldNotBeNull().should {
            it.servings shouldBe 4
            it.ingredients.first().value shouldBe 4.0
        }
        verify { recipeRepository.search(username, title) }
    }

    private fun mockRecipeAggregate(servings: Int? = 2): RecipeAggregate {
        return RecipeAggregate(
            initialState =
                RecipeAggregate.RecipeState(
                    id = 1L,
                    title = "title1",
                    username = "user1",
                    instructions = "instructions",
                    servings = servings,
                    ingredients = listOf(Ingredient(value = 2.0, unit = IngredientUnits.GRAM, type = "type")),
                ),
        )
    }
}
