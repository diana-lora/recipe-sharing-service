package net.azeti.recipesharing.infra.api.recipe

import com.fasterxml.jackson.module.kotlin.readValue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import net.azeti.recipesharing.AbstractRestIntegrationTest
import net.azeti.recipesharing.helpers.defaultCreateRecipeCommand
import net.azeti.recipesharing.helpers.defaultCreateRecipeRequest
import net.azeti.recipesharing.helpers.defaultUpdateRecipeRequest
import net.azeti.recipesharing.helpers.defaultUserRegistration
import net.azeti.recipesharing.helpers.deleteRequest
import net.azeti.recipesharing.helpers.postRequest
import net.azeti.recipesharing.helpers.putRequest
import net.azeti.recipesharing.infra.api.exception.ErrorApi
import net.azeti.recipesharing.infra.api.recipe.RecipeUris.RECIPES_ID_URI
import net.azeti.recipesharing.infra.api.recipe.RecipeUris.RECIPES_URI
import net.azeti.recipesharing.recipe.RecipeTestHelper
import net.azeti.recipesharing.recipe.infra.api.dto.IngredientRequest
import net.azeti.recipesharing.recipe.infra.api.dto.IngredientUnitsApi
import net.azeti.recipesharing.recipe.infra.api.dto.RecipeResponse
import net.azeti.recipesharing.user.UserTestHelper
import net.azeti.recipesharing.utils.mapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class RecipeControllerTest
    @Autowired
    constructor(
        private val userTestHelper: UserTestHelper,
        private val recipeTestHelper: RecipeTestHelper,
    ) : AbstractRestIntegrationTest() {
        @Test
        fun `User creates a recipe`() {
            val userDetails = userTestHelper.createUser(defaultUserRegistration())
            val token = token(userDetails)
            val recipeRequest = defaultCreateRecipeRequest()
            mockMvc.postRequest(uri = RECIPES_URI, jwt = token, body = mapper.writeValueAsString(recipeRequest))
                .andExpect(status().isCreated)
                .andReturn().response.contentAsString
                .let { mapper.readValue<RecipeResponse>(it) }
                .let { response ->
                    response.title shouldBe recipeRequest.title
                    response.description shouldBe recipeRequest.description
                    response.instructions shouldBe recipeRequest.instructions
                    response.servings shouldBe recipeRequest.servings
                    response.ingredients.size shouldBe 1
                    response.ingredients.first().should { ingredient ->
                        ingredient.value shouldBe recipeRequest.ingredients.first().value
                        ingredient.unit shouldBe recipeRequest.ingredients.first().unit
                        ingredient.type shouldBe recipeRequest.ingredients.first().type
                    }
                    recipeTestHelper.findRecipeById(response.id).shouldNotBeNull()
                }
        }

        @Test
        fun `User updates a recipe`() {
            val userDetails = userTestHelper.createUser(defaultUserRegistration())
            val createRecipeCommand = defaultCreateRecipeCommand(owner = userDetails)
            val recipeId = recipeTestHelper.createRecipe(createRecipeCommand).shouldNotBeNull()
            val ingredientUpdate = IngredientRequest(2.0, IngredientUnitsApi.KILOGRAM, "Updated Ingredient")
            val updateRequest =
                defaultUpdateRecipeRequest(
                    title = "Updated Title",
                    description = "Updated Description",
                    instructions = "Updated Instructions",
                    servings = 2,
                    ingredients = listOf(ingredientUpdate),
                )
            mockMvc.putRequest(uri = RECIPES_ID_URI, params = listOf(recipeId), jwt = token(userDetails), body = mapper.writeValueAsString(updateRequest))
                .andExpect(status().isOk)
                .andReturn().response.contentAsString
                .let { mapper.readValue<RecipeResponse>(it) }
                .shouldNotBeNull()
                .should { response ->
                    response.id shouldBe recipeId
                    response.title shouldBe updateRequest.title
                    response.description shouldBe updateRequest.description
                    response.instructions shouldBe updateRequest.instructions
                    response.servings shouldBe updateRequest.servings
                    response.ingredients.size shouldBe updateRequest.ingredients.size
                    response.ingredients.first().should { ingredient ->
                        ingredient.value shouldBe ingredientUpdate.value
                        ingredient.unit shouldBe ingredientUpdate.unit
                        ingredient.type shouldBe ingredientUpdate.type
                    }
                }
        }

        @Test
        fun `User updates a recipe but recipe doesn't exist, then not found`() {
            val userDetails = userTestHelper.createUser(defaultUserRegistration())
            val updateRequest =
                defaultUpdateRecipeRequest(
                    title = "Updated Title",
                    description = "Updated Description",
                    instructions = "Updated Instructions",
                    servings = 2,
                    ingredients = listOf(IngredientRequest(2.0, IngredientUnitsApi.KILOGRAM, "Updated Ingredient")),
                )
            mockMvc.putRequest(uri = RECIPES_ID_URI, params = listOf(999L), jwt = token(userDetails), body = mapper.writeValueAsString(updateRequest))
                .andExpect(status().isNotFound)
                .andReturn().response.contentAsString
                .let { mapper.readValue<ErrorApi>(it) }
                .shouldNotBeNull()
                .should { it.code shouldBe "recipe.notFound" }
        }

        @Test
        fun `User deletes a recipe, then no content`() {
            val userDetails = userTestHelper.createUser(defaultUserRegistration())
            val recipeId = recipeTestHelper.createRecipe(defaultCreateRecipeCommand(owner = userDetails)).shouldNotBeNull()
            mockMvc.deleteRequest(uri = RECIPES_ID_URI, params = listOf(recipeId), jwt = token(userDetails)).andExpect(status().isNoContent)
            recipeTestHelper.findRecipeById(recipeId).shouldBeNull()
        }

        @Test
        fun `User deletes a recipe but it doesn't exist, then not found`() {
            val userDetails = userTestHelper.createUser(defaultUserRegistration())
            mockMvc.deleteRequest(uri = RECIPES_ID_URI, params = listOf(999L), jwt = token(userDetails))
                .andExpect(status().isNotFound)
                .andReturn().response.contentAsString
                .let { mapper.readValue<ErrorApi>(it) }
                .shouldNotBeNull()
                .should { it.code shouldBe "recipe.notFound" }
        }
    }

object RecipeUris {
    const val RECIPES_URI = "/v1/recipes"
    const val USER_RECIPES_URI = "/v1/users/{username}/recipes"
    const val RECIPES_ID_URI = "/v1/recipes/{id}"
}
