package net.azeti.challenge.recipe.security

import net.azeti.challenge.recipe.extensions.FullRecipeId
import net.azeti.challenge.recipe.extensions.toUsername
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
