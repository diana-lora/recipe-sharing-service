package net.azeti.recipesharing.infra.api.recipe

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import net.azeti.recipesharing.core.exceptions.UserNotFoundException
import net.azeti.recipesharing.infra.api.exception.ErrorApi
import net.azeti.recipesharing.recipe.application.RecipeApplicationService
import net.azeti.recipesharing.recipe.infra.api.dto.RecipeResponse
import net.azeti.recipesharing.user.application.UserApplicationService
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@RequestMapping("/v1/users/{username}")
class UserRecipeController(
    private val userService: UserApplicationService,
    private val recipeService: RecipeApplicationService,
) {
    // @formatter:off
    @Operation(summary = "Get all recipes of a user")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "403", description = "Action not allowed"),
            ApiResponse(
                responseCode = "404",
                description = "User doesn't exist",
                content = [Content(mediaType = "application/json", array = (ArraySchema(schema = Schema(implementation = ErrorApi::class))))],
            ),
        ],
    )
    // @formatter:on
    @GetMapping("/recipes")
    fun getUsersRecipes(
        @PathVariable username: String,
    ): List<RecipeResponse> {
        val user = userService.findByUsername(username) ?: throw UserNotFoundException(username)
        return recipeService.findByUser(user)
    }
}
