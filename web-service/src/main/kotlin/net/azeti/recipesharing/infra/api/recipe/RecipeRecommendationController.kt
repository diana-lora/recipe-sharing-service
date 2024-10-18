package net.azeti.recipesharing.infra.api.recipe

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import net.azeti.recipesharing.recipe.application.RecipeApplicationService
import net.azeti.recipesharing.recipe.infra.api.dto.RecipeResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/recipes/recommendations")
class RecipeRecommendationController(
    private val recipeService: RecipeApplicationService,
) {
    // @formatter:off
    @Operation(summary = "Recommends a recipe depending on the weather")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Recommend a recipe if there is one"),
            ApiResponse(responseCode = "403", description = "Action not allowed"),
        ],
    )
    // @formatter:on
    @GetMapping
    fun getRecommendation(): RecipeResponse? = recipeService.recommendRecipe()
}
