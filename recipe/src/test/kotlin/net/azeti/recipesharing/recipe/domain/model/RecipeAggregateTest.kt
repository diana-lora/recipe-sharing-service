package net.azeti.recipesharing.recipe.domain.model

import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class RecipeAggregateTest {
    @Test
    fun `Create RecipeAggregate successfully`() {
        val recipe = defaultRecipe()

        val aggregate = RecipeAggregate.create(recipe)

        aggregate.title shouldBe "title"
        aggregate.instructions shouldBe "instructions"
        aggregate.ingredients.shouldNotBeEmpty().first().should {
            it.value shouldBe 1.0
            it.unit shouldBe IngredientUnits.GRAM
            it.type shouldBe "type"
        }
    }

    @Test
    fun `RecipeAggregate with invalid title throws exception when checking validity`() {
        val aggregate = defaultRecipeAggregate(title = "")

        val exception = assertThrows<IllegalStateException> { aggregate.checkValidity() }

        exception.message shouldBe "title.blank"
    }

    @Test
    fun `RecipeAggregate with invalid instructions throws exception when checking validity`() {
        val aggregate = defaultRecipeAggregate(instructions = "")

        val exception = assertThrows<IllegalStateException> { aggregate.checkValidity() }

        exception.message shouldBe "instructions.blank"
    }

    @Test
    fun `RecipeAggregate with invalid ingredients throws exception when checking validity`() {
        val aggregate = defaultRecipeAggregate(ingredients = emptyList())

        val exception = assertThrows<IllegalStateException> { aggregate.checkValidity() }

        exception.message shouldBe "ingredients.empty"
    }

    @Test
    fun `RecipeAggregate with invalid ingredient's value throws exception when checking validity`() {
        val aggregate = defaultRecipeAggregate(ingredients = listOf(Ingredient(value = 0.0, unit = IngredientUnits.GRAM, type = "type")))

        val exception = assertThrows<IllegalStateException> { aggregate.checkValidity() }

        exception.message shouldBe "ingredient-value.empty"
    }

    @Test
    fun `Create RecipeAggregate with invalid ingredient's type throws exception`() {
        val aggregate = defaultRecipeAggregate(ingredients = listOf(Ingredient(value = 1.0, unit = IngredientUnits.GRAM, type = "")))

        val exception = assertThrows<IllegalStateException> { aggregate.checkValidity() }

        exception.message shouldBe "ingredient-type.blank"
    }

    @Test
    fun `Update RecipeAggregate successfully`() {
        val aggregate =
            RecipeAggregate(
                RecipeAggregate.RecipeState(
                    id = 1,
                    title = "initial title",
                    username = "user",
                    instructions = "initial instructions",
                    ingredients = listOf(Ingredient(value = 1.0, unit = IngredientUnits.GRAM, type = "type")),
                ),
            )
        val recipe =
            Recipe(
                id = 1,
                title = "updated title",
                description = "updated description",
                username = "user",
                instructions = "updated instructions",
                servings = 2,
                ingredients = listOf(Ingredient(value = 2.0, unit = IngredientUnits.GRAM, type = "type")),
            )

        aggregate.updateState(recipe)

        aggregate.title shouldBe "updated title"
        aggregate.instructions shouldBe "updated instructions"
        aggregate.servings shouldBe 2
        aggregate.ingredients.size shouldBe 1
        aggregate.ingredients.first().should {
            it.value shouldBe 2.0
            it.unit shouldBe IngredientUnits.GRAM
            it.type shouldBe "type"
        }
    }

    private fun defaultRecipeAggregate(
        id: Long = 1,
        title: String = "title",
        username: String = "username",
        instructions: String = "instructions",
        servings: Int = 1,
        description: String = "description",
        ingredients: List<Ingredient> = listOf(Ingredient(value = 1.0, unit = IngredientUnits.GRAM, type = "type")),
    ) = RecipeAggregate(
        RecipeAggregate.RecipeState(
            id = id,
            title = title,
            username = username,
            instructions = instructions,
            servings = servings,
            description = description,
            ingredients = ingredients,
        ),
    )
}

fun defaultRecipe(
    id: Long = 0,
    username: String = "username",
    title: String = "title",
    description: String = "description",
    instructions: String = "instructions",
    servings: Int = 1,
    ingredients: List<Ingredient> = listOf(Ingredient(1.0, IngredientUnits.GRAM, "type")),
) = Recipe(
    id = id,
    title = title,
    description = description,
    instructions = instructions,
    servings = servings,
    ingredients = ingredients,
    username = username,
)
