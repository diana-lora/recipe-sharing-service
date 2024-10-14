package net.azeti.challenge.recipe.api.recipe

import com.fasterxml.jackson.module.kotlin.readValue
import com.github.tomakehurst.wiremock.WireMockServer
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import net.azeti.challenge.recipe.AbstractRestIntegrationTest
import net.azeti.challenge.recipe.UserTestHelper
import net.azeti.challenge.recipe.api.exception.ErrorApi
import net.azeti.challenge.recipe.api.recipe.dto.RecipeResponse
import net.azeti.challenge.recipe.client.VisualCrossingClient
import net.azeti.challenge.recipe.config.props.VisualCrossingProperties
import net.azeti.challenge.recipe.defaultRecipeRequest
import net.azeti.challenge.recipe.defaultUserRegistration
import net.azeti.challenge.recipe.expectedBerlinWeatherResponse
import net.azeti.challenge.recipe.fromCelsiusToFahrenheit
import net.azeti.challenge.recipe.getRequest
import net.azeti.challenge.recipe.mockWeatherResponseAPI
import net.azeti.challenge.recipe.utils.mapper
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@EnableConfigurationProperties(VisualCrossingProperties::class)
class RecipeRecommendationControllerTest
    @Autowired
    constructor(
        private val userTestHelper: UserTestHelper,
        private val recipeTestHelper: RecipeTestHelper,
        private val visualCrossingClient: VisualCrossingClient,
    ) : AbstractRestIntegrationTest() {
        private val wireMockServer = WireMockServer(8080)

        @AfterEach()
        fun cleanup() {
            wireMockServer.stop()
        }

        @Test
        fun `Recommend no baking recipe because it is to warm today`() {
            val userDetails = userTestHelper.createUser(defaultUserRegistration())
            val randomRecipeId = createRandomRecipe(userDetails.username)
            val bakingRecipeId = createBakingBreadRecipe(userDetails.username)
            val chocolateMouseId = createChocolateMousseRecipe(userDetails.username)
            mockVisualCrossingAPIResponse(tempCelsius = 30.0)

            mockMvc.getRequest(uri = "/v1/recipes/recommendations", jwt = token(userDetails))
                .andExpect(status().isOk)
                .andReturn().response.contentAsString
                .let { mapper.readValue<RecipeResponse>(it) }
                .shouldNotBeNull()
                .should { (it.id == chocolateMouseId || it.id == randomRecipeId) shouldBe true }
        }

        @Test
        fun `Recommend no frozen ingredients recipe because it is to cold today `() {
            val userDetails = userTestHelper.createUser(defaultUserRegistration())
            val randomRecipeId = createRandomRecipe(userDetails.username)
            val bakingRecipeId = createBakingBreadRecipe(userDetails.username)
            val chocolateMouseId = createChocolateMousseRecipe(userDetails.username)
            mockVisualCrossingAPIResponse(tempCelsius = 1.0)

            mockMvc.getRequest(uri = "/v1/recipes/recommendations", jwt = token(userDetails))
                .andExpect(status().isOk)
                .andReturn().response.contentAsString
                .let { mapper.readValue<RecipeResponse>(it) }
                .shouldNotBeNull()
                .should { (it.id == bakingRecipeId || it.id == randomRecipeId) shouldBe true }
        }

        @Test
        fun `Recommend random recipe because the weather is cool`() {
            val userDetails = userTestHelper.createUser(defaultUserRegistration())
            val randomRecipeId = createRandomRecipe(userDetails.username)
            val bakingRecipeId = createBakingBreadRecipe(userDetails.username)
            val chocolateMouseRecipeId = createChocolateMousseRecipe(userDetails.username)
            mockVisualCrossingAPIResponse(tempCelsius = 20.0)

            mockMvc.getRequest(uri = "/v1/recipes/recommendations", jwt = token(userDetails))
                .andExpect(status().isOk)
                .andReturn().response.contentAsString
                .let { mapper.readValue<RecipeResponse>(it) }
                .shouldNotBeNull()
                .should { (it.id == bakingRecipeId || it.id == randomRecipeId || it.id == chocolateMouseRecipeId) shouldBe true }
        }

        @Test
        fun `User gets a recipe recommendation but no recipes available, then not found`() {
            mockVisualCrossingAPIResponse(tempCelsius = 20.0)
            val userDetails = userTestHelper.createUser(defaultUserRegistration())
            mockMvc.getRequest(uri = "/v1/recipes/recommendations", jwt = token(userDetails))
                .andExpect(status().isNotFound)
                .andReturn().response.contentAsString
                .let { mapper.readValue<ErrorApi>(it) }
                .shouldNotBeNull()
                .should { it.code shouldBe "recipe.notFound" }
        }

        private fun mockVisualCrossingAPIResponse(tempCelsius: Double) {
            val tempFahrenheit = tempCelsius.fromCelsiusToFahrenheit()
            val expectedResponse = expectedBerlinWeatherResponse(tempFahrenheit.toString())
            wireMockServer.mockWeatherResponseAPI(200, expectedResponse).start()
        }

        private fun createRandomRecipe(username: String) = recipeTestHelper.createRecipe(defaultRecipeRequest(username = username))

        private fun createBakingBreadRecipe(username: String) =
            recipeTestHelper.createRecipe(
                defaultRecipeRequest(
                    title = "Bread Recipe",
                    instructions =
                        """
                        1. mix flour with water and yeast
                        2. wait 30 minutes
                        3. make balls of 10 cm in diameter
                        4. put them in the oven for 30 minutes at 180 degrees
                        """.trimIndent(),
                    username = username,
                ),
            )

        private fun createChocolateMousseRecipe(username: String) =
            recipeTestHelper.createRecipe(
                defaultRecipeRequest(
                    title = "Chocolate Mousse",
                    instructions =
                        """
                        1. melt chocolate
                        2. separate eggs yolks from whites
                        3. whip eggs whites
                        4. mix yolks with chocolate
                        5. mix whites with chocolate
                        6. put in the fridge for 4 hours
                        """.trimIndent(),
                    username = username,
                ),
            )
    }
