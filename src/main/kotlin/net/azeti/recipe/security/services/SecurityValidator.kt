package net.azeti.recipe.security.services

import net.azeti.recipe.extensions.FullRecipeId
import net.azeti.recipe.extensions.toUsername
import net.azeti.recipe.security.SecurityContextUtils
import org.springframework.stereotype.Component

@Component
class SecurityValidator {
    fun requesterHasAccessToUserRecipes(username: String): Boolean {
        val principal = SecurityContextUtils.principal()
        return principal.username == username
    }

    fun requesterHasAccessToRecipe(recipeId: FullRecipeId): Boolean {
        val principal = SecurityContextUtils.principal()
        return principal.username == recipeId.toUsername()
    }
}
