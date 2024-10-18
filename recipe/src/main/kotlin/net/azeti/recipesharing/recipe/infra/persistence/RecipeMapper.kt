package net.azeti.recipesharing.recipe.infra.persistence

import net.azeti.recipesharing.recipe.domain.model.Ingredient
import net.azeti.recipesharing.recipe.domain.model.IngredientUnits
import net.azeti.recipesharing.recipe.domain.model.RecipeAggregate

fun Ingredient.toEntity(recipeId: Long) =
    IngredientEntity(
        value = value,
        unit = unit.toEntity(),
        type = type,
        recipeId = recipeId,
    )

fun IngredientUnits.toEntity() =
    when (this) {
        IngredientUnits.GRAM -> IngredientEntityUnits.GRAM
        IngredientUnits.KILOGRAM -> IngredientEntityUnits.KILOGRAM
        IngredientUnits.MILLILITER -> IngredientEntityUnits.MILLILITER
        IngredientUnits.LITER -> IngredientEntityUnits.LITER
        IngredientUnits.PIECE -> IngredientEntityUnits.PIECE
        IngredientUnits.TEASPOON -> IngredientEntityUnits.TEASPOON
        IngredientUnits.TABLESPOON -> IngredientEntityUnits.TABLESPOON
        IngredientUnits.A_DASH -> IngredientEntityUnits.A_DASH
    }

fun RecipeEntity.toAggregate(): RecipeAggregate =
    RecipeAggregate(
        RecipeAggregate.RecipeState(
            id = id,
            title = title,
            username = username,
            description = description,
            ingredients = ingredients.map { it.toDomain() },
            instructions = instructions,
            servings = servings,
        ),
    )

fun IngredientEntity.toDomain() =
    Ingredient(
        value = value,
        unit = unit.toDomain(),
        type = type,
    )

fun IngredientEntityUnits.toDomain() =
    when (this) {
        IngredientEntityUnits.GRAM -> IngredientUnits.GRAM
        IngredientEntityUnits.KILOGRAM -> IngredientUnits.KILOGRAM
        IngredientEntityUnits.MILLILITER -> IngredientUnits.MILLILITER
        IngredientEntityUnits.LITER -> IngredientUnits.LITER
        IngredientEntityUnits.PIECE -> IngredientUnits.PIECE
        IngredientEntityUnits.TEASPOON -> IngredientUnits.TEASPOON
        IngredientEntityUnits.TABLESPOON -> IngredientUnits.TABLESPOON
        IngredientEntityUnits.A_DASH -> IngredientUnits.A_DASH
    }
