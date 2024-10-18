package net.azeti.recipesharing.infra.api.recipe

import com.fasterxml.jackson.module.kotlin.readValue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import net.azeti.recipesharing.AbstractRestIntegrationTest
import net.azeti.recipesharing.helpers.defaultCreateRecipeCommand
import net.azeti.recipesharing.helpers.defaultCreateRecipeRequest
import net.azeti.recipesharing.helpers.defaultUpdateRecipeRequest
import net.azeti.recipesharing.helpers.defaultUserRegistration
import net.azeti.recipesharing.helpers.postRequest
import net.azeti.recipesharing.helpers.putRequest
import net.azeti.recipesharing.infra.api.exception.ErrorApi
import net.azeti.recipesharing.infra.api.recipe.RecipeUris.RECIPES_ID_URI
import net.azeti.recipesharing.infra.api.recipe.RecipeUris.RECIPES_URI
import net.azeti.recipesharing.recipe.RecipeTestHelper
import net.azeti.recipesharing.recipe.infra.api.dto.IngredientRequest
import net.azeti.recipesharing.recipe.infra.api.dto.IngredientUnitsApi
import net.azeti.recipesharing.user.UserTestHelper
import net.azeti.recipesharing.utils.mapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class RecipeControllerValidationTest
    @Autowired
    constructor(
        private val userTestHelper: UserTestHelper,
        private val recipeTestHelper: RecipeTestHelper,
    ) : AbstractRestIntegrationTest() {
        @Test
        fun `User creates a recipe title is blank, then bad request`() {
            val userDetails = userTestHelper.createUser(defaultUserRegistration())
            val request = defaultCreateRecipeRequest(title = "")
            mockMvc.postRequest(uri = RECIPES_URI, jwt = token(userDetails), body = mapper.writeValueAsString(request))
                .andExpect(status().isBadRequest)
                .andReturn().response.contentAsString
                .shouldNotBeNull()
                .let { mapper.readValue<ErrorApi>(it) }
                .should { it.code shouldBe "title.blank" }
        }

        @Test
        fun `User creates a recipe instructions is blank, then bad request`() {
            val userDetails = userTestHelper.createUser(defaultUserRegistration())
            val request = defaultCreateRecipeRequest(instructions = "")
            mockMvc.postRequest(uri = RECIPES_URI, jwt = token(userDetails), body = mapper.writeValueAsString(request))
                .andExpect(status().isBadRequest)
                .andReturn().response.contentAsString
                .shouldNotBeNull()
                .let { mapper.readValue<ErrorApi>(it) }
                .should { it.code shouldBe "instructions.blank" }
        }

        @Test
        fun `User creates a recipe without ingredients, then bad request`() {
            val userDetails = userTestHelper.createUser(defaultUserRegistration())
            val request = defaultCreateRecipeRequest(ingredients = emptyList())
            mockMvc.postRequest(uri = RECIPES_URI, jwt = token(userDetails), body = mapper.writeValueAsString(request))
                .andExpect(status().isBadRequest)
                .andReturn().response.contentAsString
                .shouldNotBeNull()
                .let { mapper.readValue<ErrorApi>(it) }
                .should { it.code shouldBe "ingredients.empty" }
        }

        @Test
        fun `User creates a recipe and ingredient's type is blank, then bad request`() {
            val userDetails = userTestHelper.createUser(defaultUserRegistration())
            val request =
                defaultCreateRecipeRequest(ingredients = listOf(IngredientRequest(1.0, IngredientUnitsApi.GRAM, "")))
            mockMvc.postRequest(uri = RECIPES_URI, jwt = token(userDetails), body = mapper.writeValueAsString(request))
                .andExpect(status().isBadRequest)
                .andReturn().response.contentAsString
                .shouldNotBeNull()
                .let { mapper.readValue<ErrorApi>(it) }
                .should { it.code shouldBe "ingredient-type.blank" }
        }

        @Test
        fun `User updates a recipe title is blank, then bad request`() {
            val userDetails = userTestHelper.createUser(defaultUserRegistration())
            val recipeId = recipeTestHelper.createRecipe(defaultCreateRecipeCommand(owner = userDetails))
            val request = defaultUpdateRecipeRequest(title = "")
            mockMvc.putRequest(uri = RECIPES_ID_URI, params = listOf(recipeId), jwt = token(userDetails), body = mapper.writeValueAsString(request))
                .andExpect(status().isBadRequest)
                .andReturn().response.contentAsString
                .shouldNotBeNull()
                .let { mapper.readValue<ErrorApi>(it) }
                .should { it.code shouldBe "title.blank" }
        }

        @Test
        fun `User updates a recipe instructions is blank, then bad request`() {
            val userDetails = userTestHelper.createUser(defaultUserRegistration())
            val recipeId = recipeTestHelper.createRecipe(defaultCreateRecipeCommand(owner = userDetails))
            val request = defaultUpdateRecipeRequest(instructions = "")
            mockMvc.putRequest(uri = RECIPES_ID_URI, params = listOf(recipeId), jwt = token(userDetails), body = mapper.writeValueAsString(request))
                .andExpect(status().isBadRequest)
                .andReturn().response.contentAsString
                .shouldNotBeNull()
                .let { mapper.readValue<ErrorApi>(it) }
                .should { it.code shouldBe "instructions.blank" }
        }

        @Test
        fun `User updates a recipe without ingredients, then bad request`() {
            val userDetails = userTestHelper.createUser(defaultUserRegistration())
            val recipeId = recipeTestHelper.createRecipe(defaultCreateRecipeCommand(owner = userDetails))
            val request = defaultUpdateRecipeRequest(ingredients = emptyList())
            mockMvc.putRequest(uri = RECIPES_ID_URI, params = listOf(recipeId), jwt = token(userDetails), body = mapper.writeValueAsString(request))
                .andExpect(status().isBadRequest)
                .andReturn().response.contentAsString
                .shouldNotBeNull()
                .let { mapper.readValue<ErrorApi>(it) }
                .should { it.code shouldBe "ingredients.empty" }
        }

        @Test
        fun `User updates a recipe and ingredient's type is blank, then bad request`() {
            val userDetails = userTestHelper.createUser(defaultUserRegistration())
            val recipeId = recipeTestHelper.createRecipe(defaultCreateRecipeCommand(owner = userDetails))
            val request =
                defaultUpdateRecipeRequest(ingredients = listOf(IngredientRequest(1.0, IngredientUnitsApi.GRAM, "")))
            mockMvc.putRequest(uri = RECIPES_ID_URI, params = listOf(recipeId), jwt = token(userDetails), body = mapper.writeValueAsString(request))
                .andExpect(status().isBadRequest)
                .andReturn().response.contentAsString
                .shouldNotBeNull()
                .let { mapper.readValue<ErrorApi>(it) }
                .should { it.code shouldBe "ingredient-type.blank" }
        }
    }
