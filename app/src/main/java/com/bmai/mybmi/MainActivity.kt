package com.bmai.mybmi

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bmai.mybmi.databinding.ActivityMainBinding
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCalculate.setOnClickListener {
            calculateBmi()
        }

        binding.btnReset.setOnClickListener {
            resetFields()
        }
    }

    private fun calculateBmi() {
        val heightStr = binding.etHeight.text.toString()
        val weightStr = binding.etWeight.text.toString()

        if (heightStr.isEmpty() || weightStr.isEmpty()) {
            Toast.makeText(this, getString(R.string.invalid_input), Toast.LENGTH_SHORT).show()
            return
        }

        val height = heightStr.toDouble()
        val weight = weightStr.toDouble()

        if (height <= 0 || weight <= 0) {
            Toast.makeText(this, getString(R.string.invalid_input), Toast.LENGTH_SHORT).show()
            return
        }

        // BMI Formula: weight (kg) / (height(m) * height(m))
        val heightInMeters = height / 100
        val bmi = weight / (heightInMeters * heightInMeters)

        displayResult(bmi)
        hideKeyboard()
        clearFocus()
    }

    private fun displayResult(bmi: Double) {
        binding.tvBmiValue.text = String.format(Locale.getDefault(), getString(R.string.bmi_result_format), bmi)

        val (category, color, message) = when {
            bmi < 18.5 -> Triple(getString(R.string.category_underweight), R.color.underweight, getString(R.string.msg_underweight))
            bmi < 25.0 -> Triple(getString(R.string.category_normal), R.color.normal, getString(R.string.msg_normal))
            bmi < 30.0 -> Triple(getString(R.string.category_overweight), R.color.overweight, getString(R.string.msg_overweight))
            else -> Triple(getString(R.string.category_obese), R.color.obese, getString(R.string.msg_obese))
        }

        binding.tvCategory.text = category
        binding.llResultBackground.setBackgroundColor(ContextCompat.getColor(this, color))
        binding.tvMessage.text = message

        // Animate Progress (BMI range 0 to 40)
        val progress = (bmi / 40.0 * 100).toInt().coerceIn(0, 100)
        binding.indicatorBmi.setProgress(progress, true)

        binding.cardResult.visibility = View.VISIBLE
        binding.cardResult.alpha = 0f
        binding.cardResult.animate().alpha(1f).setDuration(500).start()
    }

    private fun resetFields() {
        binding.etHeight.text?.clear()
        binding.etWeight.text?.clear()
        binding.cardResult.visibility = View.GONE
        clearFocus()
    }

    private fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun clearFocus() {
        binding.etHeight.clearFocus()
        binding.etWeight.clearFocus()
    }
}
