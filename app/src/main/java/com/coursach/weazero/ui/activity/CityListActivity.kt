package com.coursach.weazero.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.coursach.Weazero.R
import com.coursach.weazero.adapter.CityAdapter
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.coursach.weazero.api.RetrofitInstance
import com.coursach.weazero.manager.PreferencesManager
import com.coursach.weazero.repository.WeatherRepository
import com.coursach.weazero.ui.viewmodels.SharedViewModel
import com.coursach.weazero.ui.viewmodels.WeatherViewModel
import com.coursach.weazero.ui.viewmodels.WeatherViewModelFactory
import com.coursach.weazero.utils.Resource
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton


class CityListActivity : AppCompatActivity() {

    private lateinit var cityAdapter: CityAdapter
    private val cityListKey = "city_list_key"
    private val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
    }
    private val viewModel by lazy {
        ViewModelProvider(
            this,
            WeatherViewModelFactory(WeatherRepository(RetrofitInstance.api, preferencesManager))
        ).get(WeatherViewModel::class.java)
    }

    val sharedViewModel: SharedViewModel by viewModels()
    private val preferencesManager: PreferencesManager by lazy {
        PreferencesManager(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city_list)
        supportActionBar?.hide()

        sharedViewModel.locationCity.observe(this) { city ->
            cityAdapter.setLocationCity(city)
        }


        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        cityAdapter = CityAdapter (
            { city ->
                val resultIntent = Intent().apply {
                    putExtra("selected_city", city)
                }
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            },
            { city ->
                cityAdapter.removeCity(city)
                saveCitiesToPrefs()
                true
            },
            {
                showAddCityDialog()
            }
        )

        recyclerView.adapter = cityAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val currentCity = intent.getStringExtra("current_city") // получаем имя текущего города из Intent
        if (currentCity != null) {
            cityAdapter.setLocationCity(currentCity)  // устанавливаем текущий город на первое место в списке городов
        }
        // Загружаем список городов из SharedPreferences
        val cities = sharedPreferences.getStringSet(cityListKey, mutableSetOf())
            ?: mutableSetOf<String>()
        if (cities.isEmpty()) {
            // добавляем Киев и Лондон если список городов пуст
            cities.add("Kiev")
            cities.add("London")
        }
        cities.forEach { city ->
            cityAdapter.addCity(city)
        }
        fetchCities()

        findViewById<Button>(R.id.settings_button).setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }


    }
    private fun fetchCities() {
        viewModel.getCities() // Запускаем загрузку городов
        viewModel.cities.observe(this, Observer { resource ->
            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    val cities = resource.data
                    cities?.forEach { city ->
                        cityAdapter.addCity(city)
                    }
                }
                Resource.Status.ERROR -> {
                    // Показываем сообщение об ошибке
                    Toast.makeText(this, "Ошибка: Невозможно загрузить список городов", Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        })
    }

    private fun saveCitiesToPrefs() {
        with(sharedPreferences.edit()) {
            putStringSet(cityListKey, cityAdapter.getCities().toSet())
            commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_city_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add_city) {
            showAddCityDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showAddCityDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add City")

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("Add") { _, _ ->
            val city = input.text.toString()
            addCity(city)
        }

        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

        builder.show()
    }
    private fun addCity(city: String) {
        cityAdapter.addCity(city)
        saveCitiesToPrefs()
        // Проверка входных данных
        viewModel.getWeather(city)
        viewModel.weatherData.observe(this, Observer { resource ->
            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    val returnedCity = resource.data?.name
                    if (returnedCity != null && city != returnedCity) {
                        cityAdapter.updateCity(city, returnedCity)
                        saveCitiesToPrefs()
                    }
                }
                Resource.Status.ERROR -> {
                    // Показываем сообщение об ошибке
                    Toast.makeText(this, "Ошибка: Неверное имя города", Toast.LENGTH_SHORT).show()
                    // Удаляем неверный город
                    cityAdapter.removeCity(city)
                    saveCitiesToPrefs()
                }
                else -> {}
            }
        })
    }

    override fun onBackPressed() {
        val cityName = cityAdapter.getCities().firstOrNull()
        if (cityName != null) {
            val intent = Intent().apply {
                putExtra("selected_city", cityName)
            }
            setResult(Activity.RESULT_OK, intent)
        }
        super.onBackPressed()
    }

}
