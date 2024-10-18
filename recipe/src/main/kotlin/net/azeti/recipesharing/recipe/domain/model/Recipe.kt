package net.azeti.recipesharing.recipe.domain.model

import net.azeti.recipesharing.recipe.infra.api.dto.IngredientResponse
import net.azeti.recipesharing.recipe.infra.api.dto.IngredientUnitsApi
import net.azeti.recipesharing.recipe.infra.api.dto.RecipeResponse

data class Recipe(
    var id: Long = 0,
    var title: String,
    val username: String,
    var instructions: String,
    var description: String? = null,
    var servings: Int? = null,
    var ingredients: List<Ingredient> = emptyList(),
)

data class Ingredient(
    val value: Double,
    val unit: IngredientUnits,
    val type: String,
)

enum class IngredientUnits {
    GRAM,
    KILOGRAM,
    MILLILITER,
    LITER,
    PIECE,
    TEASPOON,
    TABLESPOON,
    A_DASH,
}

fun Recipe.toApi() =
    RecipeResponse(
        id = id,
        title = title,
        description = description,
        ingredients =
            ingredients.map { ingredient ->
                IngredientResponse(
                    value = ingredient.value,
                    unit = ingredient.unit.toApi(),
                    type = ingredient.type,
                )
            },
        instructions = instructions,
        servings = servings,
        username = username,
    )

fun IngredientUnits.toApi() =
    when (this) {
        IngredientUnits.GRAM -> IngredientUnitsApi.GRAM
        IngredientUnits.KILOGRAM -> IngredientUnitsApi.KILOGRAM
        IngredientUnits.MILLILITER -> IngredientUnitsApi.MILLILITER
        IngredientUnits.LITER -> IngredientUnitsApi.LITER
        IngredientUnits.PIECE -> IngredientUnitsApi.PIECE
        IngredientUnits.TEASPOON -> IngredientUnitsApi.TEASPOON
        IngredientUnits.TABLESPOON -> IngredientUnitsApi.TABLESPOON
        IngredientUnits.A_DASH -> IngredientUnitsApi.A_DASH
    }

fun IngredientUnitsApi.toDomain() =
    when (this) {
        IngredientUnitsApi.GRAM -> IngredientUnits.GRAM
        IngredientUnitsApi.KILOGRAM -> IngredientUnits.KILOGRAM
        IngredientUnitsApi.MILLILITER -> IngredientUnits.MILLILITER
        IngredientUnitsApi.LITER -> IngredientUnits.LITER
        IngredientUnitsApi.PIECE -> IngredientUnits.PIECE
        IngredientUnitsApi.TEASPOON -> IngredientUnits.TEASPOON
        IngredientUnitsApi.TABLESPOON -> IngredientUnits.TABLESPOON
        IngredientUnitsApi.A_DASH -> IngredientUnits.A_DASH
    }
