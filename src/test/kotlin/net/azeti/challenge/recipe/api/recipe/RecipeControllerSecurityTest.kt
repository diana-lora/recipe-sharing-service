package net.azeti.challenge.recipe.api.recipe

import io.kotest.matchers.nulls.shouldNotBeNull
import net.azeti.challenge.recipe.AbstractRestIntegrationTest
import net.azeti.challenge.recipe.UserTestHelper
import net.azeti.challenge.recipe.api.recipe.RecipeUris.RECIPES_ID_URI
import net.azeti.challenge.recipe.api.recipe.RecipeUris.RECIPES_URI
import net.azeti.challenge.recipe.api.recipe.RecipeUris.USER_RECIPES_URI
import net.azeti.challenge.recipe.api.recipe.dto.IngredientRequest
import net.azeti.challenge.recipe.api.recipe.dto.IngredientUnitsApi
import net.azeti.challenge.recipe.defaultRecipeRequest
import net.azeti.challenge.recipe.defaultUserRegistration
import net.azeti.challenge.recipe.deleteRequest
import net.azeti.challenge.recipe.getRequest
import net.azeti.challenge.recipe.postRequest
import net.azeti.challenge.recipe.putRequest
import net.azeti.challenge.recipe.utils.mapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class RecipeControllerSecurityTest
    @Autowired
    constructor(
        private val userTestHelper: UserTestHelper,
        private val recipeTestHelper: RecipeTestHelper,
    ) : AbstractRestIntegrationTest() {
        @Test
        fun `Unauthorized user creates a recipe for another user, then forbidden`() {
            val unauthorized = unauthorizedUser()
            val userDetails = userTestHelper.createUser(defaultUserRegistration())
            val recipeRequest = defaultRecipeRequest(username = userDetails.username)
            mockMvc.postRequest(uri = RECIPES_URI, jwt = token(unauthorized), body = mapper.writeValueAsString(recipeRequest))
                .andExpect(status().isForbidden)
        }

        @Test
        fun `Unauthorized user gets user's recipes, no token, then forbidden`() {
            val userDetails = userTestHelper.createUser(defaultUserRegistration())
            mockMvc.getRequest(uri = USER_RECIPES_URI, params = listOf(userDetails.username)).andExpect(status().isForbidden)
        }

        @Test
        fun `Unauthorized user gets one recipe, no token, then forbidden`() {
            mockMvc.getRequest(uri = RECIPES_ID_URI, params = listOf("random_1"))
                .andExpect(status().isForbidden)
        }

        @Test
        fun `Unauthorized user updates a recipe, then forbidden`() {
            val unauthorized = unauthorizedUser()
            val userDetails = userTestHelper.createUser(defaultUserRegistration())
            val recipeRequest = defaultRecipeRequest(username = userDetails.username)
            val fullRecipeId = recipeTestHelper.createRecipe(recipeRequest)
            val updateRequest =
                recipeRequest.copy(
                    title = "Updated Title",
                    description = "Updated Description",
                    instructions = "Updated Instructions",
                    servings = 2,
                    ingredients = listOf(IngredientRequest(2.0, IngredientUnitsApi.KILOGRAM, "Updated Ingredient")),
                )
            mockMvc.putRequest(uri = RECIPES_ID_URI, params = listOf(fullRecipeId), jwt = token(unauthorized), body = mapper.writeValueAsString(updateRequest))
                .andExpect(status().isForbidden)
        }

        @Test
        fun `Unauthorized user deletes a recipe, then forbidden`() {
            val unauthorized = unauthorizedUser()
            val userDetails = userTestHelper.createUser(defaultUserRegistration())
            val fullRecipeId = recipeTestHelper.createRecipe(defaultRecipeRequest(username = userDetails.username)).shouldNotBeNull()
            mockMvc.deleteRequest(uri = RECIPES_ID_URI, params = listOf(fullRecipeId), jwt = token(unauthorized))
                .andExpect(status().isForbidden)
        }

        private fun unauthorizedUser() = userTestHelper.createUser(defaultUserRegistration(username = "unauthorized", email = "unauthorized@example.com"))
    }
