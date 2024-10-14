package net.azeti.recipe.core.recipe

import net.azeti.recipe.api.recipe.dto.RecipeResponse

interface RecipeSearchService {
    fun search(
        username: String?,
        title: String?,
    ): List<RecipeResponse>
}
