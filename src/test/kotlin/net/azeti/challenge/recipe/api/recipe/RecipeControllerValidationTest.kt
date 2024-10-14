package net.azeti.challenge.recipe.api.recipe

import com.fasterxml.jackson.module.kotlin.readValue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import net.azeti.challenge.recipe.AbstractRestIntegrationTest
import net.azeti.challenge.recipe.UserTestHelper
import net.azeti.challenge.recipe.api.exception.ErrorApi
import net.azeti.challenge.recipe.api.recipe.dto.IngredientRequest
import net.azeti.challenge.recipe.api.recipe.dto.IngredientUnitsApi
import net.azeti.challenge.recipe.api.recipe.dto.RecipeRequest
import net.azeti.challenge.recipe.defaultRecipeRequest
import net.azeti.challenge.recipe.defaultUserRegistration
import net.azeti.challenge.recipe.postRequest
import net.azeti.challenge.recipe.utils.mapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class RecipeControllerValidationTest
    @Autowired
    constructor(
        private val userTestHelper: UserTestHelper,
    ) : AbstractRestIntegrationTest() {
        @Test
        fun `User creates a recipe title is blank, then bad request`() {
            val userDetails = userTestHelper.createUser(defaultUserRegistration())
            val recipeRequest = defaultRecipeRequest(title = "", username = userDetails.username)
            mockMvc.postRequest(uri = "/v1/recipes", jwt = token(userDetails), body = mapper.writeValueAsString(recipeRequest))
                .andExpect(status().isBadRequest)
                .andReturn().response.contentAsString
                .shouldNotBeNull()
                .let { mapper.readValue<ErrorApi>(it) }
                .should { it.code shouldBe "title.blank" }
        }

        @Test
        fun `User creates a recipe instructions is blank, then bad request`() {
            val userDetails = userTestHelper.createUser(defaultUserRegistration())
            val recipeRequest = defaultRecipeRequest(instructions = "", username = userDetails.username)
            mockMvc.postRequest(uri = "/v1/recipes", jwt = token(userDetails), body = mapper.writeValueAsString(recipeRequest))
                .andExpect(status().isBadRequest)
                .andReturn().response.contentAsString
                .shouldNotBeNull()
                .let { mapper.readValue<ErrorApi>(it) }
                .should { it.code shouldBe "instructions.blank" }
        }

        @Test
        fun `User creates a recipe username is blank, then bad request`() {
            val userDetails = userTestHelper.createUser(defaultUserRegistration())
            val recipeRequest = defaultRecipeRequest(username = "")
            mockMvc.postRequest(uri = "/v1/recipes", jwt = token(userDetails), body = mapper.writeValueAsString(recipeRequest))
                .andExpect(status().isBadRequest)
                .andReturn().response.contentAsString
                .shouldNotBeNull()
                .let { mapper.readValue<ErrorApi>(it) }
                .should { it.code shouldBe "username.blank" }
        }

        @Test
        fun `User creates a recipe without ingredients, then bad request`() {
            val userDetails = userTestHelper.createUser(defaultUserRegistration())
            val recipeRequest =
                RecipeRequest(
                    title = "Test Recipe",
                    description = "Test Description",
                    ingredients = emptyList(),
                    instructions = "Test Instructions",
                    servings = 1,
                    username = userDetails.username,
                )
            mockMvc.postRequest(uri = "/v1/recipes", jwt = token(userDetails), body = mapper.writeValueAsString(recipeRequest))
                .andExpect(status().isBadRequest)
                .andReturn().response.contentAsString
                .shouldNotBeNull()
                .let { mapper.readValue<ErrorApi>(it) }
                .should { it.code shouldBe "ingredients.empty" }
        }

        @Test
        fun `User creates a recipe and ingredient's type is blank, then bad request`() {
            val userDetails = userTestHelper.createUser(defaultUserRegistration())
            val recipeRequest =
                defaultRecipeRequest(
                    ingredients = listOf(IngredientRequest(1.0, IngredientUnitsApi.GRAM, "")),
                    username = userDetails.username,
                )
            mockMvc.postRequest(uri = "/v1/recipes", jwt = token(userDetails), body = mapper.writeValueAsString(recipeRequest))
                .andExpect(status().isBadRequest)
                .andReturn().response.contentAsString
                .shouldNotBeNull()
                .let { mapper.readValue<ErrorApi>(it) }
                .should { it.code shouldBe "ingredient.type.blank" }
        }
    }
