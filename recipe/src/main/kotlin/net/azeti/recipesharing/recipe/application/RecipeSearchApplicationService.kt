package net.azeti.recipesharing.recipe.application

import net.azeti.recipesharing.recipe.domain.model.toApi
import net.azeti.recipesharing.recipe.domain.service.RecipeSearchService
import net.azeti.recipesharing.recipe.infra.api.dto.RecipeResponse
import org.springframework.stereotype.Service

@Service
class RecipeSearchApplicationService(
    private val recipeSearchService: RecipeSearchService,
) {
    fun search(
        username: String?,
        title: String?,
    ): List<RecipeResponse> {
        return recipeSearchService.search(username, title).map { it.toApi() }
    }
}
