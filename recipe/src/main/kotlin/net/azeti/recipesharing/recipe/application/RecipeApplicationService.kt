package net.azeti.recipesharing.recipe.application

import net.azeti.recipesharing.core.exceptions.ActionNotAllowedException
import net.azeti.recipesharing.core.exceptions.RecipeNotFoundException
import net.azeti.recipesharing.core.extensions.expectTrueOr
import net.azeti.recipesharing.core.model.CustomUserDetails
import net.azeti.recipesharing.recipe.domain.model.toApi
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
        return recipeService.create(command).toApi()
    }

    fun update(command: UpdateRecipeCommand): RecipeResponse {
        val recipeInDb = recipeQueryService.findById(command.id) ?: throw RecipeNotFoundException(command.id)
        expectTrueOr(recipeInDb.username == command.requester.username) { throw ActionNotAllowedException("not allowed") }
        return recipeService.update(command).toApi()
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
