package net.azeti.challenge.recipe.client

import net.azeti.challenge.recipe.AbstractExternalServiceIntegrationTest
import net.azeti.challenge.recipe.api.exception.ExternalServiceDataUnavailableException
import net.azeti.challenge.recipe.api.exception.ExternalServiceException
import net.azeti.challenge.recipe.expectedBerlinWeatherResponse
import net.azeti.challenge.recipe.mockWeatherResponseAPI
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class VisualCrossingClientTest : AbstractExternalServiceIntegrationTest() {
    @Test
    fun `Request weather for today and parse correctly`() {
        wireMockServer.mockWeatherResponseAPI(200, expectedBerlinWeatherResponse("51.4"))
        val response = visualCrossingClient.getBerlinWeatherTodayInFahrenheit()
        assertEquals("Berlin,DE", response.address)
        assertEquals(51.4, response.days[0].temp)
    }

    @Test
    fun `Request weather for today but response is 404 error`() {
        wireMockServer.mockWeatherResponseAPI(404, "")
        val exception =
            assertThrows<ExternalServiceDataUnavailableException> {
                visualCrossingClient.getBerlinWeatherTodayInFahrenheit()
            }
        assertEquals("data.unavailable", exception.code)
        assertEquals("Service VisualCrossing Weather API is unavailable", exception.message)
    }

    @Test
    fun `Request weather for today but response is 429`() {
        wireMockServer.mockWeatherResponseAPI(429, "")
        val exception =
            assertThrows<ExternalServiceDataUnavailableException> {
                visualCrossingClient.getBerlinWeatherTodayInFahrenheit()
            }
        assertEquals("data.unavailable", exception.code)
        assertEquals("Service VisualCrossing Weather API is unavailable", exception.message)
    }

    @Test
    fun `Request weather for today but response is an unexpected error code`() {
        wireMockServer.mockWeatherResponseAPI(500, "")
        val exception =
            assertThrows<ExternalServiceException> {
                visualCrossingClient.getBerlinWeatherTodayInFahrenheit()
            }
        assertEquals("service.unavailable", exception.code)
        assertEquals("Service VisualCrossing Weather API is unavailable", exception.message)
    }
}
