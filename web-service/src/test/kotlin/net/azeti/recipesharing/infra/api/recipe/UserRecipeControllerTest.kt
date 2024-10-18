package net.azeti.recipesharing.infra.api.recipe

import com.fasterxml.jackson.module.kotlin.readValue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import net.azeti.recipesharing.AbstractRestIntegrationTest
import net.azeti.recipesharing.core.model.CustomUserDetails
import net.azeti.recipesharing.helpers.defaultCreateRecipeCommand
import net.azeti.recipesharing.helpers.defaultUserRegistration
import net.azeti.recipesharing.helpers.getRequest
import net.azeti.recipesharing.infra.api.recipe.RecipeUris.USER_RECIPES_URI
import net.azeti.recipesharing.recipe.RecipeTestHelper
import net.azeti.recipesharing.recipe.infra.api.dto.IngredientUnitsApi
import net.azeti.recipesharing.recipe.infra.api.dto.RecipeResponse
import net.azeti.recipesharing.user.UserTestHelper
import net.azeti.recipesharing.utils.mapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class UserRecipeControllerTest
    @Autowired
    constructor(
        private val userTestHelper: UserTestHelper,
        private val recipeTestHelper: RecipeTestHelper,
    ) : AbstractRestIntegrationTest() {
        @Test
        fun `User gets their recipes`() {
            val userDetails = userTestHelper.createUser(defaultUserRegistration())
            repeat(3) { recipeTestHelper.createRecipe(defaultCreateRecipeCommand(owner = userDetails, title = "Recipe $it")).shouldNotBeNull() }

            mockMvc.getRequest(uri = USER_RECIPES_URI, params = listOf(userDetails.username), jwt = token(userDetails))
                .andExpect(status().isOk)
                .andReturn().response.contentAsString
                .let { mapper.readValue<List<RecipeResponse>>(it) }
                .shouldNotBeNull()
                .should { recipes ->
                    recipes.size shouldBe 3
                    recipes.forEachIndexed { index, recipe ->
                        recipe.title shouldBe "Recipe $index"
                        recipe.description shouldBe "Test Description"
                        recipe.instructions shouldBe "Test Instructions"
                        recipe.servings shouldBe 1
                        recipe.ingredients.size shouldBe 1
                        recipe.ingredients.first().should { ingredient ->
                            ingredient.value shouldBe 1.0
                            ingredient.unit shouldBe IngredientUnitsApi.GRAM
                            ingredient.type shouldBe "Test Ingredient"
                        }
                    }
                }
        }

        @Test
        fun `User gets their recipes, user doesn't exist, then not found`() {
            val userDetails = CustomUserDetails(id = 1L, username = "username", password = "password", email = "email")
            mockMvc.getRequest(uri = USER_RECIPES_URI, params = listOf(userDetails.username), jwt = token(userDetails)).andExpect(status().isNotFound)
        }
    }
