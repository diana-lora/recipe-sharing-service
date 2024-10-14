package net.azeti.challenge.recipe.api.recipe

import net.azeti.challenge.recipe.api.exception.InvalidParameterException
import net.azeti.challenge.recipe.api.recipe.dto.RecipeResponse
import net.azeti.challenge.recipe.extensions.expectTrueOr
import net.azeti.challenge.recipe.recipe.RecipeSearchService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/recipes")
class RecipeSearchController(
    private val recipeSearchService: RecipeSearchService,
) {
    @GetMapping
    fun searchRecipes(
        @RequestParam(required = false) username: String?,
        @RequestParam(required = false) title: String?,
    ): List<RecipeResponse> {
        expectTrueOr(username != null || title != null) { throw InvalidParameterException("invalid.query", "Either username or title must be provided") }
        return recipeSearchService.search(username, title)
    }
}
