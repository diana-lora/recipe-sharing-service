package net.azeti.recipe.security

import net.azeti.recipe.extensions.FullRecipeId
import net.azeti.recipe.extensions.toUsername
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
