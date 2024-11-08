package net.azeti.recipesharing.recipe.domain.service

import net.azeti.recipesharing.recipe.domain.model.Recipe
import net.azeti.recipesharing.recipe.infra.api.commmands.CreateRecipeCommand
import net.azeti.recipesharing.recipe.infra.api.commmands.UpdateRecipeCommand

interface RecipeService {
    fun create(command: CreateRecipeCommand): Recipe

    fun update(command: UpdateRecipeCommand): Recipe

    fun delete(recipeId: Long)
}
