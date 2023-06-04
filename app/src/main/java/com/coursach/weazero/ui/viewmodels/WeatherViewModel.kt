package com.coursach.weazero.ui.viewmodels

import ForecastData
import android.util.Log
import androidx.lifecycle.*
import com.coursach.weazero.model.WeatherData
import com.coursach.weazero.repository.WeatherRepository
import com.coursach.weazero.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {

    private val _weatherData = MutableLiveData<Resource<WeatherData>>()
    val weatherData: LiveData<Resource<WeatherData>> = _weatherData
    val forecastData: MutableLiveData<ForecastData> = MutableLiveData()
    private var _cityName = MutableLiveData<String>()
    val cityName: LiveData<String> get() = _cityName
    private val _cities = MutableLiveData<Resource<List<String>>>()
    val cities: LiveData<Resource<List<String>>> get() = _cities

    fun getWeather(city: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _weatherData.postValue(Resource.loading(data = null))
            try {
                val weather = repository.getWeather(city)
                Log.d("WeatherViewModel", "Data from Repository: $weather")
                _weatherData.postValue(Resource.success(data = weather))
                _cityName.postValue(weather?.name)  // сохраняем имя города

                // обновляем список городов после получения нового города
                getCities()
            } catch (exception: Exception) {
                _weatherData.postValue(Resource.error(data = null, message = exception.message ?: "Error occurred!"))
            }
        }
    }
    init {
        getCities()
    }

    fun getForecast(cityName: String) {
        viewModelScope.launch {
            val forecast = repository.getForecast(cityName)
            forecastData.postValue(forecast)
        }
    }
    fun getCities() {
        viewModelScope.launch(Dispatchers.IO) {
            _cities.postValue(Resource.loading(data = null))
            try {
                val cityList = repository.getCities() // реализуйте этот метод в своем WeatherRepository
                _cities.postValue(Resource.success(data = cityList))
            } catch (exception: Exception) {
                _cities.postValue(Resource.error(data = null, message = exception.message ?: "Error occurred!"))
            }
        }
    }
    fun getWeather(lat: Double, lon: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            _weatherData.postValue(Resource.loading(data = null))
            try {
                val weather = repository.getWeather(lat, lon)
                Log.d("WeatherViewModel", "Data from Repository: $weather")
                _weatherData.postValue(Resource.success(data = weather))
                // обновляем список городов после получения нового города
                getCities()
            } catch (exception: Exception) {
                _weatherData.postValue(Resource.error(data = null, message = exception.message ?: "Error occurred!"))
            }
        }
    }



}

