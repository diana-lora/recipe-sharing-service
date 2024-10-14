package net.azeti.recipe.api.recipe

import com.fasterxml.jackson.module.kotlin.readValue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import net.azeti.recipe.AbstractRestIntegrationTest
import net.azeti.recipe.UserTestHelper
import net.azeti.recipe.api.exception.ErrorApi
import net.azeti.recipe.api.recipe.RecipeUris.RECIPES_ID_URI
import net.azeti.recipe.api.recipe.RecipeUris.RECIPES_URI
import net.azeti.recipe.api.recipe.RecipeUris.USER_RECIPES_URI
import net.azeti.recipe.api.recipe.dto.IngredientRequest
import net.azeti.recipe.api.recipe.dto.IngredientUnitsApi
import net.azeti.recipe.api.recipe.dto.RecipeRequest
import net.azeti.recipe.api.recipe.dto.RecipeResponse
import net.azeti.recipe.api.recipe.dto.toEnum
import net.azeti.recipe.defaultRecipeRequest
import net.azeti.recipe.defaultUserRegistration
import net.azeti.recipe.deleteRequest
import net.azeti.recipe.extensions.FullRecipeId
import net.azeti.recipe.extensions.toRecipeId
import net.azeti.recipe.getRequest
import net.azeti.recipe.postRequest
import net.azeti.recipe.putRequest
import net.azeti.recipe.recipe.IngredientEntity
import net.azeti.recipe.recipe.IngredientRepository
import net.azeti.recipe.recipe.RecipeEntity
import net.azeti.recipe.recipe.RecipeRepository
import net.azeti.recipe.security.auth.CustomUserDetails
import net.azeti.recipe.utils.mapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class RecipeControllerTest
    @Autowired
    constructor(
        private val userTestHelper: UserTestHelper,
        private val recipeTestHelper: net.azeti.recipe.api.recipe.RecipeTestHelper,
    ) : AbstractRestIntegrationTest() {
        @Test
        fun `User creates a recipe`() {
            val userDetails = userTestHelper.createUser(defaultUserRegistration())
            val token = token(userDetails)
            val recipeRequest = defaultRecipeRequest(username = userDetails.username)
            mockMvc.postRequest(uri = RECIPES_URI, jwt = token, body = mapper.writeValueAsString(recipeRequest))
                .andExpect(status().isCreated)
                .andReturn().response.contentAsString
                .let { mapper.readValue<RecipeResponse>(it) }
                .let { response ->
                    response.id shouldStartWith "${userDetails.username}_"
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
                    val recipeId = response.id.substringAfter("_").toLong()
                    recipeTestHelper.findRecipeById(recipeId).shouldNotBeNull()
                }
        }

        @Test
        fun `User creates a recipe, but it is a duplicate, then conflict`() {
            val userDetails = userTestHelper.createUser(defaultUserRegistration())
            val recipeRequest = defaultRecipeRequest(username = userDetails.username)
            recipeTestHelper.createRecipe(recipeRequest)

            mockMvc.postRequest(uri = RECIPES_URI, jwt = token(userDetails), body = mapper.writeValueAsString(recipeRequest))
                .andExpect(status().isConflict)
                .andReturn().response.contentAsString
                .let { mapper.readValue<ErrorApi>(it) }
                .shouldNotBeNull()
                .should { it.code shouldBe "recipe.duplicate" }
        }

        @Test
        fun `User gets their recipes`() {
            val userDetails = userTestHelper.createUser(defaultUserRegistration())
            repeat(3) { recipeTestHelper.createRecipe(defaultRecipeRequest(title = "Recipe $it", username = userDetails.username)).shouldNotBeNull() }

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

        @Test
        fun `User gets one recipe`() {
            val userDetails = userTestHelper.createUser(defaultUserRegistration())
            val recipeRequest = defaultRecipeRequest(username = userDetails.username)
            val fullRecipeId = recipeTestHelper.createRecipe(recipeRequest).shouldNotBeNull()
            mockMvc.getRequest(uri = RECIPES_ID_URI, params = listOf(fullRecipeId), jwt = token(userDetails))
                .andExpect(status().isOk)
                .andReturn().response.contentAsString
                .let { mapper.readValue<RecipeResponse>(it) }
                .shouldNotBeNull()
                .should { response ->
                    response.id shouldBe fullRecipeId
                    response.title shouldBe recipeRequest.title
                    response.description shouldBe recipeRequest.description
                    response.instructions shouldBe recipeRequest.instructions
                    response.servings shouldBe recipeRequest.servings
                    response.ingredients.size shouldBe recipeRequest.ingredients.size
                }
        }

        @Test
        fun `User gets one recipe but it doesn't exist, then not found`() {
            val userDetails = userTestHelper.createUser(defaultUserRegistration())
            val nonExistentRecipeId = "${userDetails.username}_9999"
            mockMvc.getRequest(uri = RECIPES_ID_URI, params = listOf(nonExistentRecipeId), jwt = token(userDetails))
                .andExpect(status().isNotFound)
                .andReturn().response.contentAsString
                .let { mapper.readValue<ErrorApi>(it) }
                .shouldNotBeNull()
                .should { it.code shouldBe "recipe.notFound" }
        }

        @Test
        fun `User updates a recipe`() {
            val userDetails = userTestHelper.createUser(defaultUserRegistration())
            val recipeRequest = defaultRecipeRequest(username = userDetails.username)
            val fullRecipeId = recipeTestHelper.createRecipe(recipeRequest).shouldNotBeNull()
            val ingredientUpdate = IngredientRequest(2.0, IngredientUnitsApi.KILOGRAM, "Updated Ingredient")
            val updateRequest =
                recipeRequest.copy(
                    title = "Updated Title",
                    description = "Updated Description",
                    instructions = "Updated Instructions",
                    servings = 2,
                    ingredients = listOf(ingredientUpdate),
                )
            mockMvc.putRequest(uri = RECIPES_ID_URI, params = listOf(fullRecipeId), jwt = token(userDetails), body = mapper.writeValueAsString(updateRequest))
                .andExpect(status().isOk)
                .andReturn().response.contentAsString
                .let { mapper.readValue<RecipeResponse>(it) }
                .shouldNotBeNull()
                .should { response ->
                    response.id shouldStartWith "${userDetails.username}_"
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
        fun `User updates a recipe, but the title is taken, then conflict`() {
            val userDetails = userTestHelper.createUser(defaultUserRegistration())
            val recipeRequest1 = defaultRecipeRequest(title = "Original Title", username = userDetails.username)
            val recipeRequest2 = defaultRecipeRequest(title = "Taken Title", username = userDetails.username)
            recipeTestHelper.createRecipe(recipeRequest1)
            val fullRecipeId = recipeTestHelper.createRecipe(recipeRequest2)

            val updateRequest = recipeRequest1.copy(title = "Taken Title")
            mockMvc.putRequest(uri = RECIPES_ID_URI, params = listOf(fullRecipeId), jwt = token(userDetails), body = mapper.writeValueAsString(updateRequest))
                .andExpect(status().isConflict)
                .andReturn().response.contentAsString
                .let { mapper.readValue<ErrorApi>(it) }
                .shouldNotBeNull()
                .should { it.code shouldBe "recipe.duplicate" }
        }

        @Test
        fun `User updates a recipe but recipe doesn't exist, then not found`() {
            val userDetails = userTestHelper.createUser(defaultUserRegistration())
            val nonExistentRecipeId = "${userDetails.username}_9999"
            val updateRequest =
                defaultRecipeRequest(
                    title = "Updated Title",
                    description = "Updated Description",
                    instructions = "Updated Instructions",
                    servings = 2,
                    ingredients = listOf(IngredientRequest(2.0, IngredientUnitsApi.KILOGRAM, "Updated Ingredient")),
                    username = userDetails.username,
                )
            mockMvc.putRequest(uri = RECIPES_ID_URI, params = listOf(nonExistentRecipeId), jwt = token(userDetails), body = mapper.writeValueAsString(updateRequest))
                .andExpect(status().isNotFound)
                .andReturn().response.contentAsString
                .let { mapper.readValue<ErrorApi>(it) }
                .shouldNotBeNull()
                .should { it.code shouldBe "recipe.notFound" }
        }

        @Test
        fun `User deletes a recipe, then no content`() {
            val userDetails = userTestHelper.createUser(defaultUserRegistration())
            val fullRecipeId = recipeTestHelper.createRecipe(defaultRecipeRequest(username = userDetails.username)).shouldNotBeNull()
            mockMvc.deleteRequest(uri = RECIPES_ID_URI, params = listOf(fullRecipeId), jwt = token(userDetails)).andExpect(status().isNoContent)
            recipeTestHelper.findRecipeById(fullRecipeId.toRecipeId()).shouldBeNull()
        }

        @Test
        fun `User deletes a recipe but it doesn't exist, then not found`() {
            val userDetails = userTestHelper.createUser(defaultUserRegistration())
            val nonExistentRecipeId = "${userDetails.username}_9999"
            mockMvc.deleteRequest(uri = RECIPES_ID_URI, params = listOf(nonExistentRecipeId), jwt = token(userDetails))
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

class RecipeTestHelper(
    private val userTestHelper: UserTestHelper,
    private val recipeRepository: RecipeRepository,
    private val ingredientRepository: IngredientRepository,
) {
    fun findRecipeById(id: Long): RecipeEntity? {
        val optional = recipeRepository.findById(id)
        return if (optional.isPresent) optional.get() else null
    }

    fun createRecipe(request: RecipeRequest): FullRecipeId {
        val user = userTestHelper.findByUsername(request.username)!!
        val recipe =
            recipeRepository.save(
                RecipeEntity(
                    title = request.title,
                    description = request.description,
                    instructions = request.instructions,
                    servings = request.servings,
                    user = user,
                ),
            )
        val ingredients =
            ingredientRepository.saveAll(
                request.ingredients.map {
                    IngredientEntity(
                        recipeId = recipe.id,
                        value = it.value,
                        unit = it.unit.toEnum(),
                        type = it.type,
                    )
                },
            )
        recipe.ingredients = ingredients
        return "${user.username}_${recipe.id}"
    }
}
