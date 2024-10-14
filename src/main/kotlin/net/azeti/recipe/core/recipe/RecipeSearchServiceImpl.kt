package net.azeti.recipe.core.recipe

import net.azeti.recipe.api.recipe.dto.RecipeResponse
import net.azeti.recipe.core.recipe.persistence.RecipeRepository
import org.springframework.stereotype.Service

@Service
class RecipeSearchServiceImpl(
    private val recipeRepository: RecipeRepository,
) : RecipeSearchService {
    override fun search(
        username: String?,
        title: String?,
    ): List<RecipeResponse> {
        return recipeRepository.findAll(RecipeRepository.search(username, title)).map { it.toApi() }
    }
}
