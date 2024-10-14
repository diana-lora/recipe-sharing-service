package net.azeti.recipe.helpers

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo

fun WireMockServer.mockWeatherResponseAPI(
    status: Int,
    expectedResponse: String,
): WireMockServer {
    stubFor(
        get(urlPathEqualTo("/VisualCrossingWebServices/rest/services/timeline/Berlin,DE/today"))
            .withQueryParam("key", equalTo("test-api-key"))
            .withQueryParam("include", equalTo("days"))
            .withQueryParam("elements", equalTo("temp"))
            .willReturn(
                aResponse()
                    .withStatus(status)
                    .withHeader("Content-Type", "application/json")
                    .withBody(expectedResponse),
            ),
    )
    return this
}

fun expectedBerlinWeatherResponse(temp: String) =
    """
    {
      "queryCost" : 1,
      "latitude" : 52.516,
      "longitude" : 13.3769,
      "resolvedAddress" : "Berlin, Deutschland",
      "address" : "Berlin,DE",
      "timezone" : "Europe/Berlin",
      "tzoffset" : 2.0,
      "days" : [
        {
          "temp" : $temp
        }
      ]
    }
    """.trimIndent()
