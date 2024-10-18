package net.azeti.recipesharing.recipe.domain.service

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import net.azeti.recipesharing.core.exceptions.InvalidParameterException
import net.azeti.recipesharing.core.exceptions.RecipeNotFoundException
import net.azeti.recipesharing.recipe.domain.model.Ingredient
import net.azeti.recipesharing.recipe.domain.model.IngredientUnits
import net.azeti.recipesharing.recipe.domain.model.RecipeAggregate
import net.azeti.recipesharing.recipe.domain.model.defaultRecipe
import net.azeti.recipesharing.recipe.domain.port.RecipeRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class RecipeServiceImplTest {
    private val recipeRepository: RecipeRepository = mockk()
    private val service = RecipeServiceImpl(recipeRepository)

    @Test
    fun `Create recipe successfully`() {
        val recipe = defaultRecipe()

        val aggregateCaptured = slot<RecipeAggregate>()
        every { recipeRepository.save(capture((aggregateCaptured))) } returns RecipeAggregate.create(recipe)

        service.create(recipe).shouldNotBeNull()

        aggregateCaptured.captured should {
            it.title shouldBe recipe.title
            it.description shouldBe recipe.description
            it.username shouldBe recipe.username
            it.instructions shouldBe recipe.instructions
            it.servings shouldBe recipe.servings
            it.ingredients.size shouldBe 1
            val ingredient = recipe.ingredients.first()
            it.ingredients[0].value shouldBe ingredient.value
            it.ingredients[0].unit shouldBe ingredient.unit
            it.ingredients[0].type shouldBe ingredient.type
        }

        verify { recipeRepository.save(any()) }
    }

    @Test
    fun `User creates a recipe, the recipe is invalid, then throw exception`() {
        val command = defaultRecipe(title = "")

        val exception = assertThrows<InvalidParameterException> { service.create(command) }

        exception.code shouldBe "title.blank"
        verify(exactly = 0) { recipeRepository.save(any()) }
    }

    @Test
    fun `Update recipe successfully`() {
        val recipe =
            defaultRecipe(
                id = 1,
                title = "update title",
                description = "update description",
                username = "username",
                instructions = "update instructions",
                servings = 2,
                ingredients = listOf(Ingredient(2.0, IngredientUnits.KILOGRAM, "update type")),
            )
        val aggregate = defaultRecipeAggregate()
        every { recipeRepository.findByIdAndFetchAll(any()) } returns aggregate
        val aggregateCaptured = slot<RecipeAggregate>()
        every { recipeRepository.update(capture(aggregateCaptured)) } returns aggregate

        service.update(recipe)

        aggregateCaptured.captured should {
            it.id shouldBe recipe.id
            it.title shouldBe recipe.title
            it.description shouldBe recipe.description
            it.username shouldBe recipe.username
            it.instructions shouldBe recipe.instructions
            it.servings shouldBe recipe.servings
            it.ingredients.size shouldBe 1
            val ingredient = recipe.ingredients.first()
            it.ingredients[0].value shouldBe ingredient.value
            it.ingredients[0].unit shouldBe ingredient.unit
            it.ingredients[0].type shouldBe ingredient.type
        }
        verify {
            recipeRepository.findByIdAndFetchAll(1L)
            recipeRepository.update(any())
        }
    }

    @Test
    fun `Update recipe but it doesn't exists, then throws RecipeNotFoundException`() {
        val recipe = defaultRecipe(id = 1, ingredients = emptyList())

        every { recipeRepository.findByIdAndFetchAll(any()) } returns null

        val exception = assertThrows<RecipeNotFoundException> { service.update(recipe) }

        exception.code shouldBe "recipe.notFound"
        exception.message shouldBe "Recipe 1 not found"

        verify { recipeRepository.findByIdAndFetchAll(1L) }
        verify(exactly = 0) { recipeRepository.update(any()) }
    }

    @Test
    fun `Update recipe with invalid data throws InvalidParameterException`() {
        val recipe = defaultRecipe(id = 1, ingredients = emptyList())

        val aggregate = defaultRecipeAggregate()
        every { recipeRepository.findByIdAndFetchAll(any()) } returns aggregate

        val exception = assertThrows<InvalidParameterException> { service.update(recipe) }

        exception.code shouldBe "ingredients.empty"

        verify { recipeRepository.findByIdAndFetchAll(1L) }
        verify(exactly = 0) { recipeRepository.update(any()) }
    }

    private fun defaultRecipeAggregate() =
        RecipeAggregate(
            RecipeAggregate.RecipeState(
                id = 1,
                title = "title",
                description = "description",
                username = "username",
                instructions = "instructions",
                servings = 1,
                ingredients = listOf(Ingredient(1.0, IngredientUnits.KILOGRAM, "type")),
            ),
        )
}
