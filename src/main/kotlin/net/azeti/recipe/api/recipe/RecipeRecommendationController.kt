package net.azeti.recipe.api.recipe

import net.azeti.recipe.api.exception.RecipeNotFoundException
import net.azeti.recipe.api.recipe.dto.RecipeResponse
import net.azeti.recipe.recipe.RecipeRecommendationService
import net.azeti.recipe.security.SecurityContextUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/recipes/recommendations")
class RecipeRecommendationController(
    private val recipeRecommendationService: RecipeRecommendationService,
) {
    @GetMapping
    fun getRecommendation(): RecipeResponse {
        val principal = SecurityContextUtils.principal()
        return recipeRecommendationService.recommendRecipe(principal.id) ?: throw RecipeNotFoundException("No recommendation found")
    }
}
