package com.coursach.weazero.model

data class WeatherData(
    val name: String,
    val main: Main,
    val weather: List<Weather> // Этот список должен содержать объекты Weather с полем 'description'
)

data class Main(
    val temp: Float,
    val humidity: Float
)

data class Weather(
    val description: String
)
