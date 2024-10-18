package net.azeti.recipesharing.recipe.domain.service

import net.azeti.recipesharing.core.exceptions.InvalidParameterException
import net.azeti.recipesharing.core.exceptions.RecipeNotFoundException
import net.azeti.recipesharing.recipe.domain.model.Recipe
import net.azeti.recipesharing.recipe.domain.model.RecipeAggregate
import net.azeti.recipesharing.recipe.domain.port.RecipeRepository
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.stereotype.Service

@Service
class RecipeServiceImpl(
    private val recipeRepository: RecipeRepository,
) : RecipeService {
    private val logger: Log = LogFactory.getLog(this.javaClass)

    override fun create(recipe: Recipe): Recipe {
        try {
            val aggregate = RecipeAggregate.create(recipe) // checks validity
            val storedRecipe = recipeRepository.save(aggregate)
            return storedRecipe.toDomain()
        } catch (e: IllegalStateException) {
            logger.error("Recipe is not valid", e)
            throw InvalidParameterException(e.message, "Recipe is not valid")
        }
    }

    override fun update(recipe: Recipe): Recipe {
        val aggregate = recipeRepository.findByIdAndFetchAll(recipe.id) ?: throw RecipeNotFoundException(recipe.id)
        return try {
            aggregate.updateState(recipe) // checks validity
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
