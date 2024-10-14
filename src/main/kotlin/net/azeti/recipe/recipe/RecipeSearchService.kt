package net.azeti.recipe.recipe

import net.azeti.recipe.api.recipe.dto.RecipeResponse
import org.springframework.stereotype.Service

interface RecipeSearchService {
    fun search(
        username: String?,
        title: String?,
    ): List<RecipeResponse>
}

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
