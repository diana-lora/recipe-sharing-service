package net.azeti.recipesharing.infra.api.recipe

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import jakarta.validation.Valid
import net.azeti.recipesharing.infra.api.exception.ErrorApi
import net.azeti.recipesharing.infra.security.SecurityContextUtils
import net.azeti.recipesharing.recipe.application.RecipeApplicationService
import net.azeti.recipesharing.recipe.infra.api.commmands.DeleteRecipeCommand
import net.azeti.recipesharing.recipe.infra.api.dto.CreateRecipeRequest
import net.azeti.recipesharing.recipe.infra.api.dto.RecipeResponse
import net.azeti.recipesharing.recipe.infra.api.dto.UpdateRecipeRequest
import net.azeti.recipesharing.recipe.infra.api.dto.toCommand
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@RequestMapping("/v1")
class RecipeController(private val recipeService: RecipeApplicationService) {
    // @formatter:off
    @Operation(summary = "Create recipe")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Recipe created"),
            ApiResponse(responseCode = "403", description = "Action not allowed"),
            ApiResponse(
                responseCode = "409",
                description = "Invalid request",
                content = [Content(mediaType = "application/json", array = (ArraySchema(schema = Schema(implementation = ErrorApi::class))))],
            ),
        ],
    )
    // @formatter:on
    @PostMapping("/recipes")
    @ResponseStatus(HttpStatus.CREATED)
    fun createRecipe(
        @Valid @RequestBody request: CreateRecipeRequest,
    ): RecipeResponse {
        val requester = SecurityContextUtils.principal()
        val command = request.toCommand(requester)
        return recipeService.create(command)
    }

    // @formatter:off
    @Operation(summary = "Update recipe")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Recipe updated"),
            ApiResponse(responseCode = "403", description = "Action not allowed"),
            ApiResponse(
                responseCode = "404",
                description = "Recipe doesn't exist",
                content = [Content(mediaType = "application/json", array = (ArraySchema(schema = Schema(implementation = ErrorApi::class))))],
            ),
            ApiResponse(responseCode = "409", description = "Invalid request payload"),
        ],
    )
    // @formatter:on
    @PutMapping("/recipes/{id}")
    fun updateRecipe(
        @PathVariable id: Long,
        @RequestBody request: UpdateRecipeRequest,
    ): RecipeResponse {
        val command = request.toCommand(id, SecurityContextUtils.principal())
        return recipeService.update(command)
    }

    // @formatter:off
    @Operation(summary = "Delete recipe")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Recipe deleted"),
            ApiResponse(responseCode = "403", description = "Action not allowed"),
            ApiResponse(
                responseCode = "404",
                description = "Recipe doesn't exist",
                content = [Content(mediaType = "application/json", array = (ArraySchema(schema = Schema(implementation = ErrorApi::class))))],
            ),
        ],
    )
    // @formatter:on
    @DeleteMapping("/recipes/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteRecipe(
        @PathVariable id: Long,
    ) {
        recipeService.delete(DeleteRecipeCommand(id, SecurityContextUtils.principal()))
    }
}
