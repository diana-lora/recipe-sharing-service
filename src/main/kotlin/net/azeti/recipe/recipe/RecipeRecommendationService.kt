package net.azeti.recipe.recipe

import net.azeti.recipe.api.recipe.dto.RecipeResponse

interface RecipeRecommendationService {
    fun recommendRecipe(userId: Long): RecipeResponse?
}
