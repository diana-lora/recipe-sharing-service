package net.azeti.challenge.recipe.recipe

import java.util.Optional

// This class assumes the Recipe's id is a Long, this can be changed if needed.
interface RecipeManagement {

    fun create(recipe: Recipe): Recipe

    fun getById(id: Long): Optional<Recipe>

    fun update(id: Long, recipe: Recipe): Recipe

    fun delete(id: Long): Recipe

    fun getByUser(username: String): List<Recipe>
}