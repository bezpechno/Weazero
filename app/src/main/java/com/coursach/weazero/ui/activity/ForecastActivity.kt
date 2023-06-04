package com.coursach.weazero.ui.activity

import ForecastData
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.coursach.Weazero.R
import com.coursach.weazero.MainActivity
import com.coursach.weazero.api.RetrofitInstance
import com.coursach.weazero.manager.PreferencesManager
import com.coursach.weazero.repository.WeatherRepository
import com.coursach.weazero.ui.viewmodels.WeatherViewModel
import com.coursach.weazero.ui.viewmodels.WeatherViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

class ForecastActivity : AppCompatActivity() {
    private val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val outputFormat = SimpleDateFormat("d MMMM", Locale.getDefault())

    private val viewModel by lazy {
        ViewModelProvider(
            this,
            WeatherViewModelFactory(WeatherRepository(RetrofitInstance.api, preferencesManager))
        ).get(WeatherViewModel::class.java)
    }

    private val preferencesManager: PreferencesManager by lazy {
        PreferencesManager(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forecast)
        supportActionBar?.hide()

        val forecastContainer: LinearLayout = findViewById(R.id.forecast_container)

        val layoutParams = generateLayoutParams()

        viewModel.forecastData.observe(this, { updateUI(it) })

        // get the city name from intent and request forecast
        val cityName = intent.getStringExtra("cityName")
        if (cityName != null) {
            viewModel.getForecast(cityName)
            val cityNameTextView: TextView = findViewById(R.id.tv_city_name)
            cityNameTextView.text = cityName
        }

        findViewById<Button>(R.id.btn_now).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun generateLayoutParams(): LinearLayout.LayoutParams {
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val marginInDp = (16 * resources.displayMetrics.density).toInt()
        layoutParams.setMargins(0, 0, 0, marginInDp)
        return layoutParams
    }

    private fun updateUI(forecast: ForecastData) {
        val forecastContainer: LinearLayout = findViewById(R.id.forecast_container)

        val forecastsByDay = forecast.list.groupBy { forecastItem ->
            val date = inputFormat.parse(forecastItem.dt_txt)
            outputFormat.format(date)
        }

        for ((day, forecasts) in forecastsByDay) {
            val dayTextView = TextView(this)
            dayTextView.text = day
            dayTextView.layoutParams = generateLayoutParams()
            dayTextView.gravity = Gravity.CENTER_HORIZONTAL
            dayTextView.textSize = 24f
            dayTextView.setTypeface(null, Typeface.BOLD)
            dayTextView.setTextColor(Color.BLACK)
            dayTextView.typeface = Typeface.create("sans-serif", Typeface.BOLD)
            dayTextView.text = day.uppercase(Locale.getDefault())

            forecastContainer.addView(dayTextView)

            for (forecastItem in forecasts) {
                val time = forecastItem.dt_txt.substring(11, 16)
                val textView = TextView(this)
                textView.text = "$time: ${formatTemperature(forecastItem.main.temp)}, ${forecastItem.main.humidity}%"
                textView.typeface = Typeface.create("sans-serif", Typeface.NORMAL)
                textView.textSize = 22f
                textView.setTextColor(Color.BLACK)
                textView.layoutParams = generateLayoutParams()
                textView.gravity = Gravity.CENTER_HORIZONTAL
                forecastContainer.addView(textView)
            }
        }

        findViewById<Button>(R.id.btn_now).setOnClickListener {
            finish()
        }
    }

    private fun formatTemperature(tempCelsius: Float): String {
        val isCelsius = preferencesManager.getTempUnit() == TempUnit.CELSIUS
        val isRounded = preferencesManager.getRoundingOption() == RoundingOption.ROUNDED

        val temp = if (isCelsius) tempCelsius else convertCelsiusToFahrenheit(tempCelsius)
        val formattedTemp = if (isRounded) Math.round(temp).toFloat() else temp

        return "$formattedTemp°${if (isCelsius) "C" else "F"}"
    }

    private fun convertCelsiusToFahrenheit(celsius: Float) = celsius * 9 / 5 + 32
}
