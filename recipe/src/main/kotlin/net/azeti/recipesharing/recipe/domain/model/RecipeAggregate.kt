package net.azeti.recipesharing.recipe.domain.model

import net.azeti.recipesharing.recipe.infra.api.commmands.CreateRecipeCommand

class RecipeAggregate(initialState: RecipeState) {
    private var state = initialState

    val id: Long
        get() = state.id
    val title: String
        get() = state.title
    val username: String
        get() = state.username
    val instructions: String
        get() = state.instructions
    val description: String?
        get() = state.description
    val servings: Int?
        get() = state.servings
    val ingredients: List<Ingredient>
        get() = state.ingredients

    fun updateTitle(title: String) {
        if (title == state.title) return
        state = state.copy(title = title)
    }

    fun updateDescription(description: String) {
        if (description == state.description) return
        state = state.copy(description = description)
    }

    fun updateServings(servings: Int) {
        if (servings == state.servings) return
        state = state.copy(servings = servings)
    }

    fun updateInstructions(instructions: String) {
        if (instructions == state.instructions) return
        state = state.copy(instructions = instructions)
    }

    fun updateIngredients(ingredients: List<Ingredient>) {
        if (ingredients == state.ingredients) return
        state = state.copy(ingredients = ingredients)
    }

    fun toDomain(): Recipe {
        return Recipe(
            id = state.id,
            title = state.title,
            username = state.username,
            instructions = state.instructions,
            description = state.description,
            servings = state.servings,
            ingredients = state.ingredients,
        )
    }

    fun checkValidity() {
        check(state.title.isNotBlank()) { "title.blank" }
        check(state.instructions.isNotBlank()) { "instructions.blank" }
        check(state.username.isNotBlank()) { "username.blank" }
        check(state.ingredients.isNotEmpty()) { "ingredients.empty" }
        state.ingredients.forEach { ingredient ->
            check(ingredient.value > 0) { "ingredient-value.empty" }
            check(ingredient.type.isNotBlank()) { "ingredient-type.blank" }
        }
    }

    data class RecipeState(
        var id: Long = 0,
        var title: String,
        val username: String,
        var instructions: String,
        var description: String? = null,
        var servings: Int? = null,
        var ingredients: List<Ingredient> = emptyList(),
    )

    companion object {
        fun create(command: CreateRecipeCommand): RecipeAggregate {
            val state =
                RecipeState(
                    title = command.title,
                    description = command.description,
                    username = command.requester.username,
                    instructions = command.instructions,
                    servings = command.servings,
                    ingredients =
                        command.ingredients.map {
                            Ingredient(
                                value = it.value,
                                unit = it.unit.toDomain(),
                                type = it.type,
                            )
                        },
                )
            val aggregate = RecipeAggregate(state)
            aggregate.checkValidity()
            return aggregate
        }
    }
}
