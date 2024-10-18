package net.azeti.recipesharing.recipe.infra.api.dto

import net.azeti.recipesharing.core.model.CustomUserDetails
import net.azeti.recipesharing.recipe.infra.api.commmands.CreateRecipeCommand
import net.azeti.recipesharing.recipe.infra.api.commmands.IngredientCommand
import net.azeti.recipesharing.recipe.infra.api.commmands.UpdateRecipeCommand

data class CreateRecipeRequest(
    val title: String,
    val description: String?,
    val ingredients: List<IngredientRequest>,
    val instructions: String,
    val servings: Int?,
)

data class UpdateRecipeRequest(
    val title: String,
    val description: String?,
    val ingredients: List<IngredientRequest>,
    val instructions: String,
    val servings: Int?,
)

data class IngredientRequest(
    val value: Double,
    val unit: IngredientUnitsApi,
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

fun CreateRecipeRequest.toCommand(requester: CustomUserDetails) =
    CreateRecipeCommand(
        title = title,
        description = description,
        requester = requester,
        instructions = instructions,
        servings = servings,
        ingredients =
            ingredients.map {
                IngredientCommand(
                    value = it.value,
                    unit = it.unit,
                    type = it.type,
                )
            },
    )

fun UpdateRecipeRequest.toCommand(
    recipeId: Long,
    owner: CustomUserDetails,
) = UpdateRecipeCommand(
    id = recipeId,
    title = title,
    description = description,
    instructions = instructions,
    servings = servings,
    requester = owner,
    ingredients =
        ingredients.map {
            IngredientCommand(
                value = it.value,
                unit = it.unit,
                type = it.type,
            )
        },
)
