package net.azeti.recipesharing.infra.api.recipe

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import net.azeti.recipesharing.core.exceptions.InvalidParameterException
import net.azeti.recipesharing.core.extensions.expectTrueOr
import net.azeti.recipesharing.infra.api.exception.ErrorApi
import net.azeti.recipesharing.recipe.application.RecipeSearchApplicationService
import net.azeti.recipesharing.recipe.infra.api.dto.RecipeResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/recipes")
class RecipeSearchController(
    private val recipeSearchService: RecipeSearchApplicationService,
) {
    // @formatter:off
    @Operation(summary = "Get recipes by username and title")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "400",
                description = "Either username or title are required",
                content = [Content(mediaType = "application/json", array = (ArraySchema(schema = Schema(implementation = ErrorApi::class))))],
            ),
            ApiResponse(responseCode = "403", description = "Action not allowed"),
        ],
    )
    // @formatter:on
    @GetMapping
    fun searchRecipes(
        @RequestParam(required = false) username: String?,
        @Parameter(description = "Separate words with '+'.")
        @RequestParam(required = false) title: String?,
    ): List<RecipeResponse> {
        expectTrueOr(username != null || title != null) { throw InvalidParameterException("invalid.query", "Either username or title must be provided") }
        return recipeSearchService.search(username, title)
    }
}
