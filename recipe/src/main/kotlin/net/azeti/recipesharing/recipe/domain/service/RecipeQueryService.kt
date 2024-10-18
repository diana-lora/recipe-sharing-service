package net.azeti.recipesharing.recipe.domain.service

import net.azeti.recipesharing.core.model.CustomUserDetails
import net.azeti.recipesharing.recipe.domain.model.Recipe

interface RecipeQueryService {
    fun findRandom(): Recipe?

    fun findByUser(owner: CustomUserDetails): List<Recipe>

    fun findById(recipeId: Long): Recipe?

    fun findByNoBackingRequired(): Recipe?

    fun findByNoFrozenIngredients(): Recipe?
}
