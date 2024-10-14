package net.azeti.challenge.recipe.api.recipe

import com.fasterxml.jackson.module.kotlin.readValue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import net.azeti.challenge.recipe.AbstractRestIntegrationTest
import net.azeti.challenge.recipe.UserTestHelper
import net.azeti.challenge.recipe.api.recipe.RecipeUris.RECIPES_URI
import net.azeti.challenge.recipe.api.recipe.dto.RecipeResponse
import net.azeti.challenge.recipe.defaultRecipeRequest
import net.azeti.challenge.recipe.defaultUserRegistration
import net.azeti.challenge.recipe.getRequest
import net.azeti.challenge.recipe.security.auth.CustomUserDetails
import net.azeti.challenge.recipe.utils.mapper
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
        fun `User searches for recipes by username, then`() {
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
        fun `User searches for recipes by partial username, then`() {
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
        fun `User searches for recipes by title, then`() {
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
        fun `User searches for recipes by partial username and partial title, title has several words then`() {
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
            ) { recipeIndex -> recipeTestHelper.createRecipe(defaultRecipeRequest(title = "$username - Recipe $recipeIndex", username = userDetails.username)).shouldNotBeNull() }
            return userDetails
        }
    }
