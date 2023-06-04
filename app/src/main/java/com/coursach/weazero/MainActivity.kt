package com.coursach.weazero

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.coursach.Weazero.R
import com.coursach.weazero.api.RetrofitInstance
import com.coursach.weazero.manager.PreferencesManager
import com.coursach.weazero.model.WeatherData
import com.coursach.weazero.repository.WeatherRepository
import com.coursach.weazero.ui.activity.CityListActivity
import com.coursach.weazero.ui.activity.ForecastActivity
import com.coursach.weazero.ui.activity.RoundingOption
import com.coursach.weazero.ui.activity.TempUnit
import com.coursach.weazero.ui.viewmodels.SharedViewModel
import com.coursach.weazero.ui.viewmodels.WeatherViewModel
import com.coursach.weazero.ui.viewmodels.WeatherViewModelFactory
import com.coursach.weazero.utils.Resource
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var preferencesManager: PreferencesManager
    private lateinit var weatherRepository: WeatherRepository
    private lateinit var weatherDataText: TextView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val sharedViewModel: SharedViewModel by viewModels()
    private val viewModel: WeatherViewModel by viewModels {
        WeatherViewModelFactory(weatherRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferencesManager = PreferencesManager(this)
        weatherRepository = WeatherRepository(RetrofitInstance.api, preferencesManager)

        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        weatherDataText = findViewById(R.id.weather_data_text)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                1
            )
        } else {
            fetchLocationData()
        }

        viewModel.weatherData.observe(this, Observer { resource ->
            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    updateUI(resource.data)
                }
                Resource.Status.ERROR -> {
                    showError(resource.message)
                }
                Resource.Status.LOADING -> {
                    showLoading()
                }
            }
        })


        sharedViewModel.locationCity.observe(this, Observer { city ->
            city?.let {
                saveCityName(it)
                viewModel.getWeather(it)
                Log.d("MainActivity $city","City: $city")

            }
        })

        findViewById<Button>(R.id.menu_button).setOnClickListener {
            val cityName = getSavedCityName()
            val intent = Intent(this, CityListActivity::class.java).apply {
                putExtra("current_city", cityName)
                putExtra("location_city", sharedViewModel.locationCity.value)
            }
            startActivityForResult(intent, REQUEST_CODE)
        }

        findViewById<Button>(R.id.btn_detailed).setOnClickListener {
            val cityName = getSavedCityName()
            Log.d("MainActivity", "City name: $cityName")
            if (cityName != null) {
                val intent = Intent(this, ForecastActivity::class.java).apply {
                    putExtra("cityName", cityName)
                }
                startActivity(intent)
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocationData()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun fetchLocationData() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                // Initialize ViewModel here
                val viewModel: WeatherViewModel by viewModels {
                    WeatherViewModelFactory(weatherRepository)
                }

                viewModel.weatherData.observe(this, Observer { resource ->
                    when (resource.status) {
                        Resource.Status.SUCCESS -> {
                            updateUI(resource.data)
                        }
                        Resource.Status.ERROR -> {
                            showError(resource.message)
                        }
                        Resource.Status.LOADING -> {
                            showLoading()
                        }
                    }
                })

                viewModel.getWeather(location.latitude, location.longitude)
            } else {
                Log.d("MainActivity", "Failed to get location data")
            }
        }.addOnFailureListener { exception ->
            Log.e("MainActivity", "Error fetching location data: ${exception.message}")
        }
    }





    private fun updateUI(weather: WeatherData?) {
        val verticalTextView: TextView = findViewById(R.id.vertical_text)
        if (weather != null) {
            val tempUnit = preferencesManager.getTempUnit()
            val roundingOption = preferencesManager.getRoundingOption()

            val temp = when (tempUnit) {
                TempUnit.CELSIUS -> weather.main.temp
                TempUnit.FAHRENHEIT -> weather.main.temp * 9 / 5 + 32
            }

            val formattedTemp = when (roundingOption) {
                RoundingOption.ROUNDED -> "%.0f".format(temp)
                RoundingOption.EXACT -> "%.1f".format(temp)
            }

            val temperatureUnit = when (tempUnit) {
                TempUnit.CELSIUS -> "°C"
                TempUnit.FAHRENHEIT -> "°F"
            }
            val weatherDescription = weather.weather[0].description
            val repeatedDescription = List(12) { weatherDescription }.joinToString(separator = "\n")
            weatherDataText.text = repeatedDescription
            verticalTextView.text = "${weather.name}, $formattedTemp$temperatureUnit, ${weather.main.humidity}%"
        } else {
            verticalTextView.text = "N/A, N/A, N/A"
            weatherDataText.text = "N/A"
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            val selectedCity = data?.getStringExtra("selected_city")
            selectedCity?.let {
                onCitySelected(it)
                viewModel.getWeather(it)
            }
        }
    }

    private fun showError(message: String?) {
        // Show error message
    }

    private fun showLoading() {
        // Show loading indicator
    }

    private fun getSavedCityName(): String? {
        val sharedPreferences = getPreferences(Context.MODE_PRIVATE)
        return sharedPreferences.getString("cityName", null)
    }

    private fun saveCityName(cityName: String) {
        val sharedPreferences = getPreferences(Context.MODE_PRIVATE)
        sharedPreferences.edit().apply {
            putString("cityName", cityName)
            apply()
        }
    }

    private fun onCitySelected(cityName: String) {
        saveCityName(cityName)
        sharedViewModel.locationCity.value = cityName
    }

    companion object {
        private const val REQUEST_CODE = 100
    }
}
