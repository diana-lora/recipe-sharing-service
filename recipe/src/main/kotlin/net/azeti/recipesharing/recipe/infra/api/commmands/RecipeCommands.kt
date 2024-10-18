package net.azeti.recipesharing.recipe.infra.api.commmands

import net.azeti.recipesharing.core.model.CustomUserDetails
import net.azeti.recipesharing.recipe.infra.api.dto.IngredientUnitsApi

data class CreateRecipeCommand(
    val title: String,
    val requester: CustomUserDetails,
    val description: String?,
    val ingredients: List<IngredientCommand>,
    val instructions: String,
    val servings: Int?,
)

data class IngredientCommand(
    val value: Double,
    val unit: IngredientUnitsApi,
    val type: String,
)

data class UpdateRecipeCommand(
    val id: Long,
    val title: String,
    val requester: CustomUserDetails,
    val description: String?,
    val ingredients: List<IngredientCommand>,
    val instructions: String,
    val servings: Int?,
)

data class DeleteRecipeCommand(
    val recipeId: Long,
    val requester: CustomUserDetails,
)
