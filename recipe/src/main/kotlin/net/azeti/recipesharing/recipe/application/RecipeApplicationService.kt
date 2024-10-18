package net.azeti.recipesharing.recipe.application

import net.azeti.recipesharing.core.exceptions.ActionNotAllowedException
import net.azeti.recipesharing.core.exceptions.RecipeNotFoundException
import net.azeti.recipesharing.core.extensions.expectTrueOr
import net.azeti.recipesharing.core.model.CustomUserDetails
import net.azeti.recipesharing.recipe.domain.model.Ingredient
import net.azeti.recipesharing.recipe.domain.model.Recipe
import net.azeti.recipesharing.recipe.domain.model.toApi
import net.azeti.recipesharing.recipe.domain.model.toDomain
import net.azeti.recipesharing.recipe.domain.port.WeatherService
import net.azeti.recipesharing.recipe.domain.service.RecipeQueryService
import net.azeti.recipesharing.recipe.domain.service.RecipeService
import net.azeti.recipesharing.recipe.infra.api.commmands.CreateRecipeCommand
import net.azeti.recipesharing.recipe.infra.api.commmands.DeleteRecipeCommand
import net.azeti.recipesharing.recipe.infra.api.commmands.UpdateRecipeCommand
import net.azeti.recipesharing.recipe.infra.api.dto.RecipeResponse
import net.azeti.recipesharing.recipe.infra.config.RecipeRecommendationProperties
import org.springframework.stereotype.Service

@Service
class RecipeApplicationService(
    private val recipeService: RecipeService,
    private val recipeQueryService: RecipeQueryService,
    private val weatherService: WeatherService,
    private val props: RecipeRecommendationProperties,
) {
    fun create(command: CreateRecipeCommand): RecipeResponse {
        val recipe =
            Recipe(
                title = command.title,
                username = command.requester.username,
                instructions = command.instructions,
                description = command.description,
                servings = command.servings,
                ingredients =
                    command.ingredients.map {
                        Ingredient(
                            value = it.value,
                            unit = it.unit.toDomain(),
                            type = it.type,
                        )
                    },
            )
        return recipeService.create(recipe).toApi()
    }

    fun update(command: UpdateRecipeCommand): RecipeResponse {
        val recipeInDb = recipeQueryService.findById(command.id) ?: throw RecipeNotFoundException(command.id)
        expectTrueOr(recipeInDb.username == command.requester.username) { throw ActionNotAllowedException("not allowed") }
        val newRecipe =
            Recipe(
                id = command.id,
                title = command.title,
                username = command.requester.username,
                instructions = command.instructions,
                description = command.description,
                servings = command.servings,
                ingredients =
                    command.ingredients.map {
                        Ingredient(
                            value = it.value,
                            unit = it.unit.toDomain(),
                            type = it.type,
                        )
                    },
            )
        return recipeService.update(newRecipe).toApi()
    }

    fun delete(command: DeleteRecipeCommand) {
        val recipe = recipeQueryService.findById(command.recipeId) ?: throw RecipeNotFoundException(command.recipeId)
        expectTrueOr(recipe.username == command.requester.username) { throw ActionNotAllowedException("not allowed") }
        return recipeService.delete(command.recipeId)
    }

    fun findByUser(owner: CustomUserDetails): List<RecipeResponse> {
        return recipeQueryService.findByUser(owner).map { it.toApi() }
    }

    fun recommendRecipe(): RecipeResponse? {
        val tempCelsius = weatherService.getBerlinWeatherTodayInCelsius()

        val recommendation =
            when {
                tempCelsius > props.max -> recipeQueryService.findByNoBackingRequired()
                tempCelsius < props.min -> recipeQueryService.findByNoFrozenIngredients()
                else -> recipeQueryService.findRandom()
            }
        return recommendation?.toApi()
    }
}
