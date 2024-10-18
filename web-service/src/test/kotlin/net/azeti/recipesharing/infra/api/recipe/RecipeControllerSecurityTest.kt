package net.azeti.recipesharing.infra.api.recipe

import io.kotest.matchers.nulls.shouldNotBeNull
import net.azeti.recipesharing.AbstractRestIntegrationTest
import net.azeti.recipesharing.helpers.defaultCreateRecipeCommand
import net.azeti.recipesharing.helpers.defaultUpdateRecipeRequest
import net.azeti.recipesharing.helpers.defaultUserRegistration
import net.azeti.recipesharing.helpers.deleteRequest
import net.azeti.recipesharing.helpers.getRequest
import net.azeti.recipesharing.helpers.putRequest
import net.azeti.recipesharing.infra.api.recipe.RecipeUris.RECIPES_ID_URI
import net.azeti.recipesharing.infra.api.recipe.RecipeUris.USER_RECIPES_URI
import net.azeti.recipesharing.recipe.RecipeTestHelper
import net.azeti.recipesharing.recipe.infra.api.dto.IngredientRequest
import net.azeti.recipesharing.recipe.infra.api.dto.IngredientUnitsApi
import net.azeti.recipesharing.user.UserTestHelper
import net.azeti.recipesharing.utils.mapper
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
            val createRecipeCommand = defaultCreateRecipeCommand(owner = userDetails)
            val recipeId = recipeTestHelper.createRecipe(createRecipeCommand)
            val updateRequest =
                defaultUpdateRecipeRequest(
                    title = "Updated Title",
                    description = "Updated Description",
                    instructions = "Updated Instructions",
                    servings = 2,
                    ingredients = listOf(IngredientRequest(2.0, IngredientUnitsApi.KILOGRAM, "Updated Ingredient")),
                )
            mockMvc.putRequest(uri = RECIPES_ID_URI, params = listOf(recipeId), jwt = token(unauthorized), body = mapper.writeValueAsString(updateRequest))
                .andExpect(status().isForbidden)
        }

        @Test
        fun `Unauthorized user deletes a recipe, then forbidden`() {
            val unauthorized = unauthorizedUser()
            val userDetails = userTestHelper.createUser(defaultUserRegistration())
            val recipeId = recipeTestHelper.createRecipe(defaultCreateRecipeCommand(owner = userDetails)).shouldNotBeNull()
            mockMvc.deleteRequest(uri = RECIPES_ID_URI, params = listOf(recipeId), jwt = token(unauthorized))
                .andExpect(status().isForbidden)
        }

        private fun unauthorizedUser() = userTestHelper.createUser(defaultUserRegistration(username = "unauthorized", email = "unauthorized@example.com"))
    }
