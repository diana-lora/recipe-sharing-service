package net.azeti.recipe.core.recipe.services

import net.azeti.recipe.api.recipe.dto.RecipeRequest
import net.azeti.recipe.api.recipe.dto.RecipeResponse
import net.azeti.recipe.security.auth.CustomUserDetails

interface RecipeService {
    fun create(
        recipeRequest: RecipeRequest,
        user: CustomUserDetails,
    ): RecipeResponse

    fun findById(id: Long): RecipeResponse?

    fun update(
        id: Long,
        recipeRequest: RecipeRequest,
    ): RecipeResponse?

    fun delete(id: Long)

    fun findByUser(userId: Long): List<RecipeResponse>

    fun existsById(id: Long): Boolean

    fun findByNoBackingRequired(): List<RecipeResponse>

    fun findByNoFrozenIngredients(): List<RecipeResponse>
}
