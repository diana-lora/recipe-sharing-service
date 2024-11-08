package net.azeti.recipesharing.recipe.domain.service

import net.azeti.recipesharing.core.exceptions.InvalidParameterException
import net.azeti.recipesharing.core.exceptions.RecipeNotFoundException
import net.azeti.recipesharing.recipe.domain.model.Ingredient
import net.azeti.recipesharing.recipe.domain.model.Recipe
import net.azeti.recipesharing.recipe.domain.model.RecipeAggregate
import net.azeti.recipesharing.recipe.domain.model.toDomain
import net.azeti.recipesharing.recipe.domain.port.RecipeRepository
import net.azeti.recipesharing.recipe.infra.api.commmands.CreateRecipeCommand
import net.azeti.recipesharing.recipe.infra.api.commmands.UpdateRecipeCommand
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.stereotype.Service

@Service
class RecipeServiceImpl(
    private val recipeRepository: RecipeRepository,
) : RecipeService {
    private val logger: Log = LogFactory.getLog(this.javaClass)

    override fun create(command: CreateRecipeCommand): Recipe {
        try {
            val aggregate = RecipeAggregate.create(command) // checks validity
            val storedRecipe = recipeRepository.save(aggregate)
            return storedRecipe.toDomain()
        } catch (e: IllegalStateException) {
            logger.error("Recipe is not valid", e)
            throw InvalidParameterException(e.message, "Recipe is not valid")
        }
    }

    override fun update(command: UpdateRecipeCommand): Recipe {
        val aggregate = recipeRepository.findByIdAndFetchAll(command.id) ?: throw RecipeNotFoundException(command.id)
        return try {
            aggregate.updateTitle(command.title)
            command.description?.let { aggregate.updateDescription(it) }
            command.servings?.let { aggregate.updateServings(it) }
            aggregate.updateInstructions(command.instructions)
            aggregate.updateIngredients(
                command.ingredients.map {
                    Ingredient(
                        value = it.value,
                        unit = it.unit.toDomain(),
                        type = it.type,
                    )
                },
            )
            aggregate.checkValidity() // checks validity
            recipeRepository.update(aggregate).toDomain()
        } catch (e: IllegalStateException) {
            logger.error("Recipe is not valid", e)
            throw InvalidParameterException(e.message, "Recipe is not valid")
        }
    }

    override fun delete(recipeId: Long) {
        recipeRepository.delete(recipeId)
    }
}
