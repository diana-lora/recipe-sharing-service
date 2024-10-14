package net.azeti.recipe.extensions

typealias FullRecipeId = String

fun FullRecipeId.toRecipeId() = split("_").last().toLong()

fun FullRecipeId.toUsername() = split("_").first()
