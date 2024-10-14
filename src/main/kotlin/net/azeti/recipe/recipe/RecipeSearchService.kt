package net.azeti.recipe.recipe

import net.azeti.recipe.api.recipe.dto.RecipeResponse

interface RecipeSearchService {
    fun search(
        username: String?,
        title: String?,
    ): List<RecipeResponse>
}
