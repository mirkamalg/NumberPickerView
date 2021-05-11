package com.mirkamalg.sampleapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.mirkamalg.numberpickerview.NumberPickerView
import com.mirkamalg.sampleapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureViews()
    }

    private fun configureViews() {
        binding.apply {
            rangeSlider.apply {
                valueFrom = 0f
                valueTo = 150f
                setMinSeparationValue(1f)
                minSeparation = 1f
                setValues(0f, 150f)
                stepSize = 1f
                addOnChangeListener { slider, value, _ ->
                    val values = slider.values
                    numberPickerView.minValue = values[0].toInt()
                    numberPickerView.maxValue = values[1].toInt()
                }
            }
            checkBoxVibration.isChecked = numberPickerView.enableVibration
            checkBoxVibration.setOnCheckedChangeListener { _, isChecked ->
                numberPickerView.enableVibration = isChecked
            }

            checkBoxLongPress.isChecked = numberPickerView.enableLongPressToReset
            checkBoxLongPress.setOnCheckedChangeListener { _, isChecked ->
                numberPickerView.enableLongPressToReset = isChecked
            }

            checkBoxGesture.isChecked = numberPickerView.enableSwipeGesture
            checkBoxGesture.setOnCheckedChangeListener { _, isChecked ->
                numberPickerView.enableSwipeGesture = isChecked
            }

            checkBoxDisable.isChecked = !numberPickerView.enableUserInput
            checkBoxDisable.setOnCheckedChangeListener { _, isChecked ->
                numberPickerView.enableUserInput = !isChecked
            }

            adapter = ArrayAdapter(
                applicationContext, android.R.layout.simple_list_item_1, listOf(
                    "Low", "Medium", "High"
                )
            )
            spinnerSensitivity.adapter = adapter
            spinnerSensitivity.setSelection(1)
            spinnerSensitivity.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        numberPickerView.swipeSensitivity =
                            when (position) {
                                0 -> {
                                    NumberPickerView.Companion.SENSITIVITY.LOW
                                }
                                1 -> {
                                    NumberPickerView.Companion.SENSITIVITY.MEDIUM
                                }
                                2 -> {
                                    NumberPickerView.Companion.SENSITIVITY.HIGH
                                }
                                else -> {
                                    NumberPickerView.Companion.SENSITIVITY.MEDIUM
                                }
                            }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }

            fabGithub.setOnClickListener {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW, Uri.parse("https://github.com/Re1r0/NumberPickerView")
                    )
                )
            }
        }
    }
}