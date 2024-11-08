package net.azeti.recipesharing.recipe.domain.service

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import net.azeti.recipesharing.core.defaultUserDetails
import net.azeti.recipesharing.core.exceptions.InvalidParameterException
import net.azeti.recipesharing.core.exceptions.RecipeNotFoundException
import net.azeti.recipesharing.recipe.domain.model.Ingredient
import net.azeti.recipesharing.recipe.domain.model.IngredientUnits
import net.azeti.recipesharing.recipe.domain.model.RecipeAggregate
import net.azeti.recipesharing.recipe.domain.model.defaultCreateRecipeCommand
import net.azeti.recipesharing.recipe.domain.model.defaultUpdateRecipeCommand
import net.azeti.recipesharing.recipe.domain.port.RecipeRepository
import net.azeti.recipesharing.recipe.infra.api.commmands.IngredientCommand
import net.azeti.recipesharing.recipe.infra.api.commmands.UpdateRecipeCommand
import net.azeti.recipesharing.recipe.infra.api.dto.IngredientUnitsApi
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class RecipeServiceImplTest {
    private val recipeRepository: RecipeRepository = mockk()
    private val service = RecipeServiceImpl(recipeRepository)

    @Test
    fun `Create recipe successfully`() {
        val command = defaultCreateRecipeCommand()

        val aggregateCaptured = slot<RecipeAggregate>()
        every { recipeRepository.save(capture((aggregateCaptured))) } returns RecipeAggregate.create(command)

        service.create(command).shouldNotBeNull()

        aggregateCaptured.captured should {
            it.title shouldBe command.title
            it.description shouldBe command.description
            it.username shouldBe command.requester.username
            it.instructions shouldBe command.instructions
            it.servings shouldBe command.servings
            it.ingredients.size shouldBe 1
            val ingredient = command.ingredients.first()
            it.ingredients[0].value shouldBe ingredient.value
            it.ingredients[0].unit.name shouldBe ingredient.unit.name
            it.ingredients[0].type shouldBe ingredient.type
        }

        verify { recipeRepository.save(any()) }
    }

    @Test
    fun `User creates a recipe, the recipe is invalid, then throw exception`() {
        val command = defaultCreateRecipeCommand(title = "")

        val exception = assertThrows<InvalidParameterException> { service.create(command) }

        exception.code shouldBe "title.blank"
        verify(exactly = 0) { recipeRepository.save(any()) }
    }

    @Test
    fun `Update recipe successfully`() {
        val command =
            UpdateRecipeCommand(
                id = 1,
                title = "update title",
                description = "update description",
                requester = defaultUserDetails(username = "username"),
                instructions = "update instructions",
                servings = 2,
                ingredients = listOf(IngredientCommand(2.0, IngredientUnitsApi.KILOGRAM, "update type")),
            )
        val aggregate = defaultRecipeAggregate()
        every { recipeRepository.findByIdAndFetchAll(any()) } returns aggregate
        val aggregateCaptured = slot<RecipeAggregate>()
        every { recipeRepository.update(capture(aggregateCaptured)) } returns aggregate

        service.update(command)

        aggregateCaptured.captured should {
            it.id shouldBe command.id
            it.title shouldBe command.title
            it.description shouldBe command.description
            it.username shouldBe command.requester.username
            it.instructions shouldBe command.instructions
            it.servings shouldBe command.servings
            it.ingredients.size shouldBe 1
            val ingredient = command.ingredients.first()
            it.ingredients[0].value shouldBe ingredient.value
            it.ingredients[0].unit.name shouldBe ingredient.unit.name
            it.ingredients[0].type shouldBe ingredient.type
        }
        verify {
            recipeRepository.findByIdAndFetchAll(1L)
            recipeRepository.update(any())
        }
    }

    @Test
    fun `Update recipe but it doesn't exists, then throws RecipeNotFoundException`() {
        val command = defaultUpdateRecipeCommand(ingredients = emptyList())

        every { recipeRepository.findByIdAndFetchAll(any()) } returns null

        val exception = assertThrows<RecipeNotFoundException> { service.update(command) }

        exception.code shouldBe "recipe.notFound"
        exception.message shouldBe "Recipe 1 not found"

        verify { recipeRepository.findByIdAndFetchAll(1L) }
        verify(exactly = 0) { recipeRepository.update(any()) }
    }

    @Test
    fun `Update recipe with invalid data throws InvalidParameterException`() {
        val command = defaultUpdateRecipeCommand(id = 1, ingredients = emptyList())

        val aggregate = defaultRecipeAggregate()
        every { recipeRepository.findByIdAndFetchAll(any()) } returns aggregate

        val exception = assertThrows<InvalidParameterException> { service.update(command) }

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
