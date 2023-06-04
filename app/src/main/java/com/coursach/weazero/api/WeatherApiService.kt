package com.coursach.weazero.api

import ForecastData
import com.coursach.Weazero.BuildConfig
import com.coursach.weazero.model.WeatherData
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    @GET("data/2.5/weather")
    suspend fun getWeather(
        @Query("q") city: String,
        @Query("appid") apiKey: String ="d70d898501b83feaf9f1a9a02bcd2004",
        @Query("units") units: String = "metric"
    ): WeatherData

    @GET("data/2.5/weather")
    suspend fun getWeatherByCoordinates(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") appid: String = "d70d898501b83feaf9f1a9a02bcd2004"
    ): WeatherData


    @GET("data/2.5/forecast")
    suspend fun getForecast(
        @Query("q") cityName: String,
        @Query("units") units: String = "metric",
        @Query("appid") apiKey: String = "d70d898501b83feaf9f1a9a02bcd2004"
    ): ForecastData

}
