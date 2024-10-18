package net.azeti.recipesharing.helpers

import net.azeti.recipesharing.core.defaultUserDetails
import net.azeti.recipesharing.core.model.CustomUserDetails
import net.azeti.recipesharing.recipe.infra.api.commmands.CreateRecipeCommand
import net.azeti.recipesharing.recipe.infra.api.commmands.IngredientCommand
import net.azeti.recipesharing.recipe.infra.api.dto.CreateRecipeRequest
import net.azeti.recipesharing.recipe.infra.api.dto.IngredientRequest
import net.azeti.recipesharing.recipe.infra.api.dto.IngredientUnitsApi
import net.azeti.recipesharing.recipe.infra.api.dto.UpdateRecipeRequest
import net.azeti.recipesharing.user.infra.api.dto.RegistrationRequest

fun defaultUserRegistration(
    email: String = "email@example.com",
    username: String = "username",
    password: String = "StrongPass123!@",
) = RegistrationRequest(
    email,
    username,
    password,
)

fun defaultUpdateRecipeRequest(
    title: String = "Recipe",
    description: String = "Test Description",
    instructions: String = "Test Instructions",
    servings: Int = 1,
    ingredients: List<IngredientRequest> = listOf(IngredientRequest(1.0, IngredientUnitsApi.GRAM, "Test Ingredient")),
) = UpdateRecipeRequest(
    title = title,
    description = description,
    ingredients = ingredients,
    instructions = instructions,
    servings = servings,
)

fun defaultCreateRecipeRequest(
    title: String = "Recipe",
    description: String = "Test Description",
    instructions: String = "Test Instructions",
    servings: Int = 1,
    ingredients: List<IngredientRequest> = listOf(IngredientRequest(1.0, IngredientUnitsApi.GRAM, "Test Ingredient")),
) = CreateRecipeRequest(
    title = title,
    description = description,
    ingredients = ingredients,
    instructions = instructions,
    servings = servings,
)

fun defaultCreateRecipeCommand(
    owner: CustomUserDetails = defaultUserDetails(),
    title: String = "Recipe",
    description: String = "Test Description",
    instructions: String = "Test Instructions",
    servings: Int = 1,
    ingredients: List<IngredientCommand> = listOf(IngredientCommand(1.0, IngredientUnitsApi.GRAM, "Test Ingredient")),
) = CreateRecipeCommand(
    title = title,
    description = description,
    instructions = instructions,
    servings = servings,
    ingredients = ingredients,
    requester = owner,
)

fun Double.fromCelsiusToFahrenheit() = this * 1.8 + 32
