package com.coursach.weazero.ui.activity

import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import com.coursach.Weazero.R
import com.coursach.weazero.manager.PreferencesManager


class SettingsActivity : AppCompatActivity() {

    private lateinit var tempUnitRadioGroup: RadioGroup
    private lateinit var roundingOptionRadioGroup: RadioGroup
    private lateinit var celsiusRadioButton: RadioButton
    private lateinit var fahrenheitRadioButton: RadioButton
    private lateinit var roundOffRadioButton: RadioButton
    private lateinit var exactRadioButton: RadioButton

    private lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        supportActionBar?.hide()

        preferencesManager = PreferencesManager(this)

        tempUnitRadioGroup = findViewById(R.id.temp_unit_radio_group)
        roundingOptionRadioGroup = findViewById(R.id.rounding_option_radio_group)
        celsiusRadioButton = findViewById(R.id.celsius_radio_button)
        fahrenheitRadioButton = findViewById(R.id.fahrenheit_radio_button)
        roundOffRadioButton = findViewById(R.id.round_off_radio_button)
        exactRadioButton = findViewById(R.id.exact_radio_button)

        loadSettings()

        tempUnitRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            saveSettings()
        }

        roundingOptionRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            saveSettings()
        }
    }

    private fun loadSettings() {
        val tempUnit = preferencesManager.getTempUnit()
        val roundingOption = preferencesManager.getRoundingOption()

        if (tempUnit == TempUnit.CELSIUS) {
            celsiusRadioButton.isChecked = true
        } else {
            fahrenheitRadioButton.isChecked = true
        }

        if (roundingOption == RoundingOption.ROUNDED) {
            roundOffRadioButton.isChecked = true
        } else {
            exactRadioButton.isChecked = true
        }
    }

    private fun saveSettings() {
        val tempUnit = if (celsiusRadioButton.isChecked) TempUnit.CELSIUS else TempUnit.FAHRENHEIT
        val roundingOption = if (roundOffRadioButton.isChecked) RoundingOption.ROUNDED else RoundingOption.EXACT

        preferencesManager.setTempUnit(tempUnit)
        preferencesManager.setRoundingOption(roundingOption)
    }
}
