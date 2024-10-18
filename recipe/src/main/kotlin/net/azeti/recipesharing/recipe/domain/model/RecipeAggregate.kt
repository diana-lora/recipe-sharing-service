package net.azeti.recipesharing.recipe.domain.model

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

    fun updateState(recipe: Recipe): RecipeAggregate {
        val newState =
            RecipeState(
                id = state.id,
                title = recipe.title,
                description = recipe.description,
                username = state.username,
                instructions = recipe.instructions,
                servings = recipe.servings,
                ingredients =
                    recipe.ingredients.map {
                        Ingredient(
                            value = it.value,
                            unit = it.unit,
                            type = it.type,
                        )
                    },
            )
        val newAggregate = RecipeAggregate(newState)
        newAggregate.checkValidity()
        state = newState
        return newAggregate
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
        fun create(recipe: Recipe): RecipeAggregate {
            val state =
                RecipeState(
                    title = recipe.title,
                    description = recipe.description,
                    username = recipe.username,
                    instructions = recipe.instructions,
                    servings = recipe.servings,
                    ingredients =
                        recipe.ingredients.map {
                            Ingredient(
                                value = it.value,
                                unit = it.unit,
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
