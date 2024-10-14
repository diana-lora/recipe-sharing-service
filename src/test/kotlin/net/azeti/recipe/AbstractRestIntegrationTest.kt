package net.azeti.recipe

import com.github.tomakehurst.wiremock.WireMockServer
import net.azeti.recipe.api.recipe.RecipeTestHelper
import net.azeti.recipe.client.VisualCrossingClient
import net.azeti.recipe.config.props.SecurityJwtProperties
import net.azeti.recipe.config.props.VisualCrossingProperties
import net.azeti.recipe.recipe.IngredientRepository
import net.azeti.recipe.recipe.RecipeRepository
import net.azeti.recipe.security.JwtService
import net.azeti.recipe.security.auth.CustomUserDetails
import net.azeti.recipe.user.UserRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.jdbc.JdbcTestUtils
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get

@ActiveProfiles(value = ["test"])
@SpringBootTest
abstract class AbstractIntegrationTest

@EnableConfigurationProperties(VisualCrossingProperties::class)
abstract class AbstractExternalServiceIntegrationTest : AbstractIntegrationTest() {
    @Autowired
    protected lateinit var visualCrossingClient: VisualCrossingClient

    protected val wireMockServer = WireMockServer(8080)

    @BeforeEach
    fun setup() {
        wireMockServer.start()
    }

    @AfterEach()
    fun cleanup() {
        wireMockServer.stop()
    }
}

@AutoConfigureMockMvc
@SpringBootTest(classes = [RecipeSharingTestConfiguration::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class AbstractRestIntegrationTest : AbstractIntegrationTest() {
    @Autowired
    protected lateinit var jdbcTemplate: JdbcTemplate

    @Autowired
    protected lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var jwtService: JwtService

    @BeforeEach
    fun cleanDb() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "ingredients", "recipes", "users")
    }

    protected fun token(userDetails: CustomUserDetails) = jwtService.createToken(userDetails)
}

abstract class AbstractAllIntegrationTest : AbstractRestIntegrationTest()

@EnableConfigurationProperties(SecurityJwtProperties::class)
@TestConfiguration
class RecipeSharingTestConfiguration {
    @Bean
    fun userTestHelper(
        userRepository: UserRepository,
        passwordEncoder: BCryptPasswordEncoder,
        jwtService: JwtService,
    ) = UserTestHelper(userRepository, passwordEncoder, jwtService)

    @Bean
    fun recipeTestHelper(
        userTestHelper: UserTestHelper,
        recipeRepository: RecipeRepository,
        ingredientRepository: IngredientRepository,
    ) = net.azeti.recipe.api.recipe.RecipeTestHelper(userTestHelper, recipeRepository, ingredientRepository)
}
