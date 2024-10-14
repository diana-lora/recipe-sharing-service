package net.azeti.recipe.core.recipe

import net.azeti.recipe.api.recipe.dto.RecipeResponse

interface RecipeRecommendationService {
    fun recommendRecipe(userId: Long): RecipeResponse?
}
