package net.azeti.recipesharing.recipe

import net.azeti.recipesharing.recipe.domain.model.RecipeAggregate
import net.azeti.recipesharing.recipe.domain.model.toApi
import net.azeti.recipesharing.recipe.domain.port.RecipeRepository
import net.azeti.recipesharing.recipe.infra.api.commmands.CreateRecipeCommand
import net.azeti.recipesharing.recipe.infra.api.dto.RecipeResponse

class RecipeTestHelper(
    private val recipeRepo: RecipeRepository,
) {
    fun findRecipeById(id: Long): RecipeResponse? = recipeRepo.findByIdAndFetchAll(id)?.toDomain()?.toApi()

    fun createRecipe(command: CreateRecipeCommand): Long {
        val aggregate = RecipeAggregate.create(command) // checks validity
        val savedRecipe = recipeRepo.save(aggregate)
        return savedRecipe.id
    }
}
