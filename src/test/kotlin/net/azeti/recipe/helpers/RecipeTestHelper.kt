package net.azeti.recipe.helpers

import net.azeti.recipe.api.recipe.dto.RecipeRequest
import net.azeti.recipe.api.recipe.dto.toEnum
import net.azeti.recipe.extensions.FullRecipeId
import net.azeti.recipe.recipe.persistence.IngredientEntity
import net.azeti.recipe.recipe.persistence.IngredientRepository
import net.azeti.recipe.recipe.persistence.RecipeEntity
import net.azeti.recipe.recipe.persistence.RecipeRepository

class RecipeTestHelper(
    private val userTestHelper: UserTestHelper,
    private val recipeRepository: RecipeRepository,
    private val ingredientRepository: IngredientRepository,
) {
    fun findRecipeById(id: Long): RecipeEntity? {
        val optional = recipeRepository.findById(id)
        return if (optional.isPresent) optional.get() else null
    }

    fun createRecipe(request: RecipeRequest): FullRecipeId {
        val user = userTestHelper.findByUsername(request.username)!!
        val recipe =
            recipeRepository.save(
                RecipeEntity(
                    title = request.title,
                    description = request.description,
                    instructions = request.instructions,
                    servings = request.servings,
                    user = user,
                ),
            )
        val ingredients =
            ingredientRepository.saveAll(
                request.ingredients.map {
                    IngredientEntity(
                        recipeId = recipe.id,
                        value = it.value,
                        unit = it.unit.toEnum(),
                        type = it.type,
                    )
                },
            )
        recipe.ingredients = ingredients
        return "${user.username}_${recipe.id}"
    }
}
