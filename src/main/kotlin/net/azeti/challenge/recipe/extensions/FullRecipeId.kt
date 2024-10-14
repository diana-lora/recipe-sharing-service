package net.azeti.challenge.recipe.extensions

typealias FullRecipeId = String

fun FullRecipeId.toRecipeId() = split("_").last().toLong()

fun FullRecipeId.toUsername() = split("_").first()
