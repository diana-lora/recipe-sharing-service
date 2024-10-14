package net.azeti.recipe.api.recipe.dto

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import net.azeti.recipe.core.recipe.persistence.IngredientUnits

data class RecipeRequest(
    @field:NotBlank(message = "title.blank")
    val title: String,
    @field:NotBlank(message = "username.blank")
    val username: String,
    val description: String?,
    @field:Valid
    @field:Size(min = 1, message = "ingredients.empty")
    val ingredients: List<IngredientRequest>,
    @field:NotBlank(message = "instructions.blank")
    val instructions: String,
    val servings: Int?,
)

data class IngredientRequest(
    val value: Double,
    val unit: IngredientUnitsApi,
    @field:NotBlank(message = "ingredient.type.blank")
    val type: String,
)

enum class IngredientUnitsApi(val abbreviation: String, val description: String) {
    GRAM("g", "Gram"),
    KILOGRAM("kg", "Kilogram"),
    MILLILITER("ml", "Milliliter"),
    LITER("l", "Liter"),
    PIECE("pc", "Piece"),
    TEASPOON("tsp", "Teaspoon"),
    TABLESPOON("tbsp", "Tablespoon"),
    A_DASH("pinch", "A dash"),
}

// TODO move to converter
fun IngredientUnitsApi.toEnum() =
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
