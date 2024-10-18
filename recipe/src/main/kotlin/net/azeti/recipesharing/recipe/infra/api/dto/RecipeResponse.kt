package net.azeti.recipesharing.recipe.infra.api.dto

data class RecipeResponse(
    val id: Long,
    val title: String,
    val username: String,
    val description: String?,
    val ingredients: List<IngredientResponse>,
    val instructions: String,
    val servings: Int?,
)

data class IngredientResponse(
    val value: Double,
    val unit: IngredientUnitsApi,
    val type: String,
)
