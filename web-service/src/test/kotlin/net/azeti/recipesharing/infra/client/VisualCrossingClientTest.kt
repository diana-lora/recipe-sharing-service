package net.azeti.recipesharing.infra.client

import net.azeti.recipesharing.AbstractExternalServiceIntegrationTest
import net.azeti.recipesharing.core.exceptions.ExternalServiceDataUnavailableException
import net.azeti.recipesharing.core.exceptions.ExternalServiceException
import net.azeti.recipesharing.helpers.expectedBerlinWeatherResponse
import net.azeti.recipesharing.helpers.mockWeatherResponseAPI
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class VisualCrossingClientTest : AbstractExternalServiceIntegrationTest() {
    @Test
    fun `Request weather for today and parse correctly`() {
        wireMockServer.mockWeatherResponseAPI(200, expectedBerlinWeatherResponse("32.0"))
        val response = visualCrossingClient.getBerlinWeatherTodayInCelsius()
        assertEquals(0.0, response)
    }

    @Test
    fun `Request weather for today but response is 404 error`() {
        wireMockServer.mockWeatherResponseAPI(404, "")
        val exception =
            assertThrows<ExternalServiceDataUnavailableException> {
                visualCrossingClient.getBerlinWeatherTodayInCelsius()
            }
        assertEquals("data.unavailable", exception.code)
        assertEquals("Service VisualCrossing Weather API is unavailable", exception.message)
    }

    @Test
    fun `Request weather for today but response is 429`() {
        wireMockServer.mockWeatherResponseAPI(429, "")
        val exception =
            assertThrows<ExternalServiceDataUnavailableException> {
                visualCrossingClient.getBerlinWeatherTodayInCelsius()
            }
        assertEquals("data.unavailable", exception.code)
        assertEquals("Service VisualCrossing Weather API is unavailable", exception.message)
    }

    @Test
    fun `Request weather for today but response is an unexpected error code`() {
        wireMockServer.mockWeatherResponseAPI(500, "")
        val exception =
            assertThrows<ExternalServiceException> {
                visualCrossingClient.getBerlinWeatherTodayInCelsius()
            }
        assertEquals("service.unavailable", exception.code)
        assertEquals("Service VisualCrossing Weather API is unavailable", exception.message)
    }
}
