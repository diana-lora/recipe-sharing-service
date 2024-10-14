package net.azeti.challenge.recipe

import net.azeti.challenge.recipe.api.recipe.dto.IngredientRequest
import net.azeti.challenge.recipe.api.recipe.dto.IngredientUnitsApi
import net.azeti.challenge.recipe.api.recipe.dto.RecipeRequest
import net.azeti.challenge.recipe.api.user.dto.RegistrationRequest

fun defaultUserRegistration(
    email: String = "email@example.com",
    username: String = "username",
    password: String = "StrongPass123!@",
) = RegistrationRequest(
    email,
    username,
    password,
)

fun defaultRecipeRequest(
    title: String = "Recipe",
    description: String = "Test Description",
    instructions: String = "Test Instructions",
    servings: Int = 1,
    username: String = "username",
    ingredients: List<IngredientRequest> = listOf(IngredientRequest(1.0, IngredientUnitsApi.GRAM, "Test Ingredient")),
) = RecipeRequest(
    title = title,
    description = description,
    ingredients = ingredients,
    instructions = instructions,
    servings = servings,
    username = username,
)

fun Double.fromCelsiusToFahrenheit() = this * 1.8 + 32
