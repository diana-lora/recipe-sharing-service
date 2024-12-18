package net.azeti.recipesharing.infra.client

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.module.kotlin.readValue
import net.azeti.recipesharing.core.exceptions.ExternalServiceDataUnavailableException
import net.azeti.recipesharing.core.exceptions.ExternalServiceException
import net.azeti.recipesharing.infra.config.VisualCrossingProperties
import net.azeti.recipesharing.recipe.domain.port.WeatherService
import net.azeti.recipesharing.utils.mapper
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.stereotype.Component

@Component
class VisualCrossingClient(
    private val visualCrossingProperties: VisualCrossingProperties,
) : WeatherService {
    override fun getBerlinWeatherTodayInCelsius(): Double = fromFahrenheitToCelsius(getBerlinWeatherTodayInFahrenheit().days.first().temp)

    private val client = OkHttpClient()
    private val url =
        "{base-url}/VisualCrossingWebServices/rest/services/timeline/Berlin,DE/today?key={api-key}&&include=days&elements=temp"
            .replace("{base-url}", visualCrossingProperties.baseUrl)
            .replace("{api-key}", visualCrossingProperties.apiKey)

    private fun getBerlinWeatherTodayInFahrenheit(): WeatherResponse {
        val request = Request.Builder().url(url).build()

        return client.newCall(request).execute().use { response ->
            when (response.code) {
                200 -> {
                    val responseObject =
                        response.body?.string()?.let { mapper.readValue<WeatherResponse>(it) }
                            ?: throw ExternalServiceDataUnavailableException(SERVICE_NAME, "unparsable.data")
                    return responseObject
                }
                // 404 NOT_FOUND – The request cannot be matched to any valid API request endpoint structure.
                404 -> throw ExternalServiceDataUnavailableException(SERVICE_NAME)
                // 429 TOO_MANY_REQUESTS – The account has exceeded their assigned limits
                429 -> throw ExternalServiceDataUnavailableException(SERVICE_NAME)
                else -> throw ExternalServiceException(SERVICE_NAME)
            }
        }
    }

    private fun fromFahrenheitToCelsius(value: Double) = (value - 32) / 1.8

    companion object {
        private const val SERVICE_NAME = "VisualCrossing Weather API"
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class WeatherResponse(val address: String, val days: List<DayTemp>) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    class DayTemp(val temp: Double)
}
