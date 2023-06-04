package com.coursach.weazero.manager

import android.content.Context
import android.content.SharedPreferences
import com.coursach.weazero.ui.activity.RoundingOption
import com.coursach.weazero.ui.activity.TempUnit

class PreferencesManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)

    fun getCities(): List<String> {
        return sharedPreferences.getStringSet("citySet", mutableSetOf())?.toList() ?: listOf()
    }

    fun saveCity(city: String) {
        val cities = getCities().toMutableSet()
        cities.add(city)
        with(sharedPreferences.edit()) {
            putStringSet("citySet", cities)
            commit()
        }
    }

    fun removeCity(city: String) {
        val cities = getCities().toMutableSet()
        cities.remove(city)
        with(sharedPreferences.edit()) {
            putStringSet("citySet", cities)
            commit()
        }
    }

    fun getTempUnit(): TempUnit {
        val unit = sharedPreferences.getString("tempUnit", TempUnit.CELSIUS.name)
        return TempUnit.valueOf(unit ?: TempUnit.CELSIUS.name)
    }

    fun setTempUnit(unit: TempUnit) {
        with(sharedPreferences.edit()) {
            putString("tempUnit", unit.name)
            commit()
        }
    }

    fun getRoundingOption(): RoundingOption {
        val option = sharedPreferences.getString("roundingOption", RoundingOption.ROUNDED.name)
        return RoundingOption.valueOf(option ?: RoundingOption.ROUNDED.name)
    }

    fun setRoundingOption(option: RoundingOption) {
        with(sharedPreferences.edit()) {
            putString("roundingOption", option.name)
            commit()
        }
    }
    // Добавьте здесь любые другие методы для работы с SharedPreferences, если они вам нужны.
}
