package net.azeti.recipesharing.recipe.domain.port

interface WeatherService {
    fun getBerlinWeatherTodayInCelsius(): Double
}
