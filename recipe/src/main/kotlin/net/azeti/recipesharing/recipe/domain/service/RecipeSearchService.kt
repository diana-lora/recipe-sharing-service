package net.azeti.recipesharing.recipe.domain.service

import net.azeti.recipesharing.recipe.domain.model.Recipe

interface RecipeSearchService {
    fun search(
        username: String?,
        title: String?,
        expectedServings: Int?,
    ): List<Recipe>
}
