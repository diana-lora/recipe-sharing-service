package net.azeti.recipe.api.recipe

import jakarta.validation.Valid
import net.azeti.recipe.api.exception.RecipeNotFoundException
import net.azeti.recipe.api.exception.UserNotFoundException
import net.azeti.recipe.api.recipe.dto.RecipeRequest
import net.azeti.recipe.api.recipe.dto.RecipeResponse
import net.azeti.recipe.extensions.FullRecipeId
import net.azeti.recipe.extensions.expectTrueOr
import net.azeti.recipe.extensions.toRecipeId
import net.azeti.recipe.recipe.RecipeService
import net.azeti.recipe.user.UserService
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
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
class RecipeController(
    private val userService: UserService,
    private val recipeService: RecipeService,
) {
    @PostMapping("/recipes")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@securityValidator.requesterHasAccessToUserRecipes(#request.username)")
    fun createRecipe(
        @Valid @RequestBody request: RecipeRequest,
    ): RecipeResponse {
        val user = userService.findByUsername(request.username) ?: throw UserNotFoundException(request.username)
        return recipeService.create(request, user)
    }

    @GetMapping("/users/{username}/recipes")
    fun getUsersRecipes(
        @PathVariable username: String,
    ): List<RecipeResponse> {
        val user = userService.findByUsername(username) ?: throw UserNotFoundException(username)
        return recipeService.findByUser(user.id)
    }

    @GetMapping("/recipes/{id}")
    fun getRecipe(
        @PathVariable id: FullRecipeId,
    ): RecipeResponse {
        val recipeId = id.toRecipeId()
        val recipe = recipeService.findById(recipeId) ?: throw RecipeNotFoundException(id)
        return recipe
    }

    @PutMapping("/recipes/{id}")
    @PreAuthorize("@securityValidator.requesterHasAccessToRecipe(#id) and @securityValidator.requesterHasAccessToUserRecipes(#request.username)")
    fun updateRecipe(
        @PathVariable id: FullRecipeId,
        @RequestBody request: RecipeRequest,
    ): RecipeResponse {
        return recipeService.update(id.toRecipeId(), request) ?: throw RecipeNotFoundException(id)
    }

    @DeleteMapping("/recipes/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@securityValidator.requesterHasAccessToRecipe(#id)")
    fun deleteRecipe(
        @PathVariable id: FullRecipeId,
    ) {
        val recipeId = id.toRecipeId()
        expectTrueOr(recipeService.existsById(recipeId)) { throw RecipeNotFoundException(id) }
        recipeService.delete(recipeId)
    }
}
