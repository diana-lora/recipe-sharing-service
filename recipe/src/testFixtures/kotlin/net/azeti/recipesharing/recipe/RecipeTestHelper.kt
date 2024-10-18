package net.azeti.recipesharing.recipe

import net.azeti.recipesharing.recipe.domain.model.Ingredient
import net.azeti.recipesharing.recipe.domain.model.Recipe
import net.azeti.recipesharing.recipe.domain.model.RecipeAggregate
import net.azeti.recipesharing.recipe.domain.model.toApi
import net.azeti.recipesharing.recipe.domain.model.toDomain
import net.azeti.recipesharing.recipe.domain.port.RecipeRepository
import net.azeti.recipesharing.recipe.infra.api.commmands.CreateRecipeCommand
import net.azeti.recipesharing.recipe.infra.api.dto.RecipeResponse

class RecipeTestHelper(
    private val recipeRepo: RecipeRepository,
) {
    fun findRecipeById(id: Long): RecipeResponse? = recipeRepo.findByIdAndFetchAll(id)?.toDomain()?.toApi()

    fun createRecipe(command: CreateRecipeCommand): Long {
        val recipe =
            Recipe(
                id = 0,
                title = command.title,
                description = command.description,
                username = command.requester.username,
                servings = command.servings,
                instructions = command.instructions,
                ingredients =
                    command.ingredients.map {
                        Ingredient(
                            value = it.value,
                            unit = it.unit.toDomain(),
                            type = it.type,
                        )
                    },
            )
        val aggregate = RecipeAggregate.create(recipe) // checks validity
        val savedRecipe = recipeRepo.save(aggregate)
        return savedRecipe.id
    }
}
