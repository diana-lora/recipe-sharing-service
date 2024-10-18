package net.azeti.recipesharing.recipe.domain.service

import net.azeti.recipesharing.recipe.domain.model.Recipe

interface RecipeService {
    fun create(recipe: Recipe): Recipe

    fun update(recipe: Recipe): Recipe

    fun delete(recipeId: Long)
}
