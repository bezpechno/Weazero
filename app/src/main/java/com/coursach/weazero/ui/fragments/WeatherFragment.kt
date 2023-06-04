package com.coursach.weazero.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.coursach.Weazero.R
import com.coursach.weazero.api.RetrofitInstance
import com.coursach.weazero.repository.WeatherRepository
import com.coursach.weazero.ui.viewmodels.WeatherViewModel
import com.coursach.weazero.ui.viewmodels.WeatherViewModelFactory
import com.coursach.weazero.utils.Resource

class WeatherFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_weather, container, false)
    }
    /*@SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val weatherRepository = WeatherRepository(RetrofitInstance.api)
        val viewModel: WeatherViewModel by viewModels { WeatherViewModelFactory(weatherRepository) }

        viewModel.weatherData.observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    val weatherData = resource.data
                    if (weatherData != null) {
                        val cityText: TextView = view.findViewById(R.id.city_text)
                        val temperatureText: TextView = view.findViewById(R.id.temperature_text)
                        val humidityText: TextView = view.findViewById(R.id.humidity_text)
                        val weatherConditionText: TextView = view.findViewById(R.id.weather_condition_text)

                        cityText.text = weatherData.name
                        temperatureText.text = "Temperature: ${weatherData.main.temp}°C"
                        humidityText.text = "Humidity: ${weatherData.main.humidity}%"
                        weatherConditionText.text = "Condition: ${weatherData.weather[0].description}"

                        println("$cityText cityText")
                        println("$temperatureText temperatureText")
                        println("$humidityText humidityText")
                        println("$weatherConditionText weatherConditionText")



                        //city_text.text = weatherData.name
                        //temperature_text.text = "Temperature: ${weatherData.main.temp}°C"
                        //humidity_text.text = "Humidity: ${weatherData.main.humidity}%"
                        //weather_condition_text.text = "Condition: ${weatherData.weather[0].description}"
                    }
                }
                Resource.Status.ERROR -> {
                    // Handle error, e.g. show a Toast or Snackbar
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_LONG).show()
                }
                Resource.Status.LOADING -> {
                    // Show loading indicator, if you have one
                }
            }
        }
    }*/



}
