package com.coursach.weazero.repository

import ForecastData

import android.util.Log

import com.coursach.weazero.api.WeatherApiService
import com.coursach.weazero.manager.PreferencesManager
import com.coursach.weazero.model.WeatherData

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class WeatherRepository(private val weatherApiService: WeatherApiService, private val preferencesManager: PreferencesManager) {

    suspend fun getWeather(city: String): WeatherData = withContext(Dispatchers.IO) {
        try {
            val response = weatherApiService.getWeather(city)
            Log.d("WeatherRepository", "Data from API: $response")
            return@withContext response
        } catch (exception: Exception) {
            Log.e("WeatherRepository", "Error occurred", exception)
            exception.printStackTrace()
            throw Exception("Problem with API", exception)
        }
    }
    suspend fun getForecast(cityName: String): ForecastData {
        return weatherApiService.getForecast(cityName)
    }
    suspend fun getCities(): List<String> {
        return preferencesManager.getCities()
    }
    suspend fun getWeather(lat: Double, lon: Double): WeatherData = withContext(Dispatchers.IO) {
        try {
            val response = weatherApiService.getWeatherByCoordinates(lat, lon)
            Log.d("WeatherRepository", "Data from API: $response")
            return@withContext response
        } catch (exception: Exception) {
            Log.e("WeatherRepository", "Error occurred", exception)
            exception.printStackTrace()
            throw Exception("Problem with API", exception)
        }
    }


    suspend fun addCity(city: String) {
        withContext(Dispatchers.IO) {
            preferencesManager.saveCity(city)
        }
    }

    suspend fun removeCity(city: String) {
        withContext(Dispatchers.IO) {
            preferencesManager.removeCity(city)
        }
    }

}


