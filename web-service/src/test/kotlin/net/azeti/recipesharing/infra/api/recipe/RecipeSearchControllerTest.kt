package net.azeti.recipesharing.infra.api.recipe

import com.fasterxml.jackson.module.kotlin.readValue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import net.azeti.recipesharing.AbstractRestIntegrationTest
import net.azeti.recipesharing.core.model.CustomUserDetails
import net.azeti.recipesharing.helpers.defaultCreateRecipeCommand
import net.azeti.recipesharing.helpers.defaultUserRegistration
import net.azeti.recipesharing.helpers.getRequest
import net.azeti.recipesharing.infra.api.recipe.RecipeUris.RECIPES_URI
import net.azeti.recipesharing.recipe.RecipeTestHelper
import net.azeti.recipesharing.recipe.infra.api.dto.RecipeResponse
import net.azeti.recipesharing.user.UserTestHelper
import net.azeti.recipesharing.utils.mapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class RecipeSearchControllerTest
    @Autowired
    constructor(
        private val userTestHelper: UserTestHelper,
        private val recipeTestHelper: RecipeTestHelper,
    ) : AbstractRestIntegrationTest() {
        @Test
        fun `User searches for recipes by username, then ok`() {
            val userDetails = createUserAndRecipes("username")
            mockMvc.getRequest(uri = "$RECIPES_URI?username={username}", params = listOf(userDetails.username), jwt = token(userDetails))
                .andExpect(status().isOk)
                .andReturn().response.contentAsString
                .shouldNotBeNull()
                .let { mapper.readValue<List<RecipeResponse>>(it) }
                .should { response ->
                    response.size shouldBe 2
                    response.forEach { recipeResponse -> recipeResponse.username shouldBe userDetails.username }
                }
        }

        @Test
        fun `User searches for recipes by partial username, then ok`() {
            val users = listOf("username", "another.user", "a-third-one").map { createUserAndRecipes(it) }
            val userDetails = users.first()
            val partialSearch = "user"
            mockMvc.getRequest(uri = "$RECIPES_URI?username={username}", params = listOf(partialSearch), jwt = token(userDetails))
                .andExpect(status().isOk)
                .andReturn().response.contentAsString
                .shouldNotBeNull()
                .let { mapper.readValue<List<RecipeResponse>>(it) }
                .should { response ->
                    response.size shouldBe 4 // two users have "user" in their usernames and each one has two recipes
                    response.forEach { recipeResponse -> recipeResponse.username shouldContain partialSearch }
                }
        }

        @Test
        fun `User searches for recipes by title, then ok`() {
            val partialSearch = "Recipe+1"
            val userDetails = createUserAndRecipes("username")
            mockMvc.getRequest(uri = "$RECIPES_URI?title={title}", params = listOf(partialSearch), jwt = token(userDetails))
                .andExpect(status().isOk)
                .andReturn().response.contentAsString
                .shouldNotBeNull()
                .let { mapper.readValue<List<RecipeResponse>>(it) }
                .should { response ->
                    response.size shouldBe 1
                    response.forEach { recipeResponse -> partialSearch.split("+").forEach { recipeResponse.title shouldContain it } }
                }
        }

        @Test
        fun `User searches for recipes by partial username and partial title, title has several words, then ok`() {
            val users = listOf("username", "another.user", "a-third-one").map { createUserAndRecipes(it) }
            val userDetails = users.first()
            val partialSearch = "user+Recipe+1"
            mockMvc.getRequest(uri = "$RECIPES_URI?title={title}", params = listOf(partialSearch), jwt = token(userDetails))
                .andExpect(status().isOk)
                .andReturn().response.contentAsString
                .shouldNotBeNull()
                .let { mapper.readValue<List<RecipeResponse>>(it) }
                .should { response ->
                    response.size shouldBe 2 // two users have "user" in their usernames and each one has one recipe with "Recipe 1" in the title
                    response.forEach { recipeResponse -> partialSearch.split("+").forEach { recipeResponse.title shouldContain it } }
                }
        }

        @Test
        fun `User searches for recipes, no username nor title given, then bad request`() {
            val userDetails = createUserAndRecipes("username")
            mockMvc.getRequest(uri = RECIPES_URI, jwt = token(userDetails))
                .andExpect(status().isBadRequest)
        }

        @Test
        fun `User searches for recipes, nothing found, then return empty array`() {
            val userDetails = createUserAndRecipes("username")
            mockMvc.getRequest(uri = "$RECIPES_URI?username={username}&title={title}", params = listOf("super", "tom+kha+gai"), jwt = token(userDetails))
                .andExpect(status().isOk)
                .andReturn().response.contentAsString
                .shouldNotBeNull()
                .let { mapper.readValue<List<RecipeResponse>>(it) }
                .should { response -> response.size shouldBe 0 }
        }

        private fun createUserAndRecipes(username: String): CustomUserDetails {
            val userDetails = userTestHelper.createUser(defaultUserRegistration(username = username, email = "$username@example.com"))
            repeat(
                2,
            ) { recipeIndex ->
                val createRecipeCommand = defaultCreateRecipeCommand(title = "$username - Recipe $recipeIndex", owner = userDetails)
                val recipeId = recipeTestHelper.createRecipe(createRecipeCommand).shouldNotBeNull()
                recipeTestHelper.findRecipeById(recipeId).shouldNotBeNull()
            }
            return userDetails
        }
    }
