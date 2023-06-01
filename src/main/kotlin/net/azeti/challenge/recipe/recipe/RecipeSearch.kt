package net.azeti.challenge.recipe.recipe

interface RecipeSearch {

    fun recipesByUsername(usernameValue: String): List<Recipe>

    fun recipesByTitle(titleValue: String): List<Recipe>
}