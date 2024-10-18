package net.azeti.recipesharing

import com.github.tomakehurst.wiremock.WireMockServer
import net.azeti.recipesharing.core.model.CustomUserDetails
import net.azeti.recipesharing.infra.client.VisualCrossingClient
import net.azeti.recipesharing.infra.config.VisualCrossingProperties
import net.azeti.recipesharing.user.domain.port.JwtService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.jdbc.JdbcTestUtils
import org.springframework.test.web.servlet.MockMvc

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

    @AfterEach
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

    protected fun getUsername(token: String) = jwtService.username(token)
}
