package net.azeti.recipesharing.recipe.domain.port

import net.azeti.recipesharing.recipe.domain.model.RecipeAggregate

interface RecipeRepository {
    fun findByUsername(username: String): List<RecipeAggregate>

    fun findRandom(): RecipeAggregate?

    fun findByIdAndFetchAll(id: Long): RecipeAggregate?

    fun search(
        username: String?,
        title: String?,
    ): List<RecipeAggregate>

    fun save(recipe: RecipeAggregate): RecipeAggregate

    fun delete(recipeId: Long)

    fun findByNoFrozenIngredients(): RecipeAggregate?

    fun findByNoBackingRequired(): RecipeAggregate?

    fun update(recipe: RecipeAggregate): RecipeAggregate
}
