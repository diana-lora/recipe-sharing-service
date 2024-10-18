package net.azeti.recipesharing.infra.api.recipe

import com.fasterxml.jackson.module.kotlin.readValue
import com.github.tomakehurst.wiremock.WireMockServer
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import net.azeti.recipesharing.AbstractRestIntegrationTest
import net.azeti.recipesharing.core.model.CustomUserDetails
import net.azeti.recipesharing.helpers.defaultCreateRecipeCommand
import net.azeti.recipesharing.helpers.defaultUserRegistration
import net.azeti.recipesharing.helpers.expectedBerlinWeatherResponse
import net.azeti.recipesharing.helpers.fromCelsiusToFahrenheit
import net.azeti.recipesharing.helpers.getRequest
import net.azeti.recipesharing.helpers.mockWeatherResponseAPI
import net.azeti.recipesharing.infra.config.VisualCrossingProperties
import net.azeti.recipesharing.recipe.RecipeTestHelper
import net.azeti.recipesharing.recipe.infra.api.dto.RecipeResponse
import net.azeti.recipesharing.user.UserTestHelper
import net.azeti.recipesharing.utils.mapper
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
    ) : AbstractRestIntegrationTest() {
        private val wireMockServer = WireMockServer(8080)

        @AfterEach
        fun cleanup() {
            wireMockServer.stop()
        }

        @Test
        fun `Recommend no baking recipe because it is to warm today`() {
            val userDetails = userTestHelper.createUser(defaultUserRegistration())
            val randomRecipeId = createRandomRecipe(userDetails)
            val bakingRecipeId = createBakingBreadRecipe(userDetails)
            val chocolateMouseId = createChocolateMousseRecipe(userDetails)
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
            val randomRecipeId = createRandomRecipe(userDetails)
            val bakingRecipeId = createBakingBreadRecipe(userDetails)
            val chocolateMouseId = createChocolateMousseRecipe(userDetails)
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
            val randomRecipeId = createRandomRecipe(userDetails)
            val bakingRecipeId = createBakingBreadRecipe(userDetails)
            val chocolateMouseRecipeId = createChocolateMousseRecipe(userDetails)
            mockVisualCrossingAPIResponse(tempCelsius = 20.0)

            mockMvc.getRequest(uri = "/v1/recipes/recommendations", jwt = token(userDetails))
                .andExpect(status().isOk)
                .andReturn().response.contentAsString
                .let { mapper.readValue<RecipeResponse>(it) }
                .shouldNotBeNull()
                .should { (it.id == bakingRecipeId || it.id == randomRecipeId || it.id == chocolateMouseRecipeId) shouldBe true }
        }

        private fun mockVisualCrossingAPIResponse(tempCelsius: Double) {
            val tempFahrenheit = tempCelsius.fromCelsiusToFahrenheit()
            val expectedResponse = expectedBerlinWeatherResponse(tempFahrenheit.toString())
            wireMockServer.mockWeatherResponseAPI(200, expectedResponse).start()
        }

        private fun createRandomRecipe(user: CustomUserDetails) = recipeTestHelper.createRecipe(defaultCreateRecipeCommand(owner = user))

        private fun createBakingBreadRecipe(user: CustomUserDetails) =
            recipeTestHelper.createRecipe(
                defaultCreateRecipeCommand(
                    owner = user,
                    title = "Bread Recipe",
                    instructions =
                        """
                        1. mix flour with water and yeast
                        2. wait 30 minutes
                        3. make balls of 10 cm in diameter
                        4. put them in the oven for 30 minutes at 180 degrees
                        """.trimIndent(),
                ),
            )

        private fun createChocolateMousseRecipe(user: CustomUserDetails) =
            recipeTestHelper.createRecipe(
                defaultCreateRecipeCommand(
                    owner = user,
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
                ),
            )
    }
