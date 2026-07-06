package com.bmai.mybmi;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bmai.mybmi.databinding.ActivityMainBinding;

import java.util.Locale;

public class MainActivityJava extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnCalculate.setOnClickListener(v -> calculateBmi());

        binding.btnReset.setOnClickListener(v -> resetFields());
    }

    private void calculateBmi() {
        String heightStr = binding.etHeight.getText().toString();
        String weightStr = binding.etWeight.getText().toString();

        if (heightStr.isEmpty() || weightStr.isEmpty()) {
            Toast.makeText(this, getString(R.string.invalid_input), Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double height = Double.parseDouble(heightStr);
            double weight = Double.parseDouble(weightStr);

            if (height <= 0 || weight <= 0) {
                Toast.makeText(this, getString(R.string.invalid_input), Toast.LENGTH_SHORT).show();
                return;
            }

            // BMI Formula: weight (kg) / (height(m) * height(m))
            double heightInMeters = height / 100;
            double bmi = weight / (heightInMeters * heightInMeters);

            displayResult(bmi);
            hideKeyboard();
            clearFocus();
        } catch (NumberFormatException e) {
            Toast.makeText(this, getString(R.string.invalid_input), Toast.LENGTH_SHORT).show();
        }
    }

    private void displayResult(double bmi) {
        binding.tvBmiValue.setText(String.format(Locale.getDefault(), getString(R.string.bmi_result_format), bmi));

        String category;
        int color;
        String message;

        if (bmi < 18.5) {
            category = getString(R.string.category_underweight);
            color = R.color.underweight;
            message = getString(R.string.msg_underweight);
        } else if (bmi < 25.0) {
            category = getString(R.string.category_normal);
            color = R.color.normal;
            message = getString(R.string.msg_normal);
        } else if (bmi < 30.0) {
            category = getString(R.string.category_overweight);
            color = R.color.overweight;
            message = getString(R.string.msg_overweight);
        } else {
            category = getString(R.string.category_obese);
            color = R.color.obese;
            message = getString(R.string.msg_obese);
        }

        binding.tvCategory.setText(category);
        binding.llResultBackground.setBackgroundColor(ContextCompat.getColor(this, color));
        binding.tvMessage.setText(message);

        // Animate Progress (BMI range 0 to 40)
        int progress = (int) (bmi / 40.0 * 100);
        if (progress > 100) progress = 100;
        if (progress < 0) progress = 0;
        binding.indicatorBmi.setProgress(progress, true);

        binding.cardResult.setVisibility(View.VISIBLE);
        binding.cardResult.setAlpha(0f);
        binding.cardResult.animate().alpha(1f).setDuration(500).start();
    }

    private void resetFields() {
        if (binding.etHeight.getText() != null) binding.etHeight.getText().clear();
        if (binding.etWeight.getText() != null) binding.etWeight.getText().clear();
        binding.cardResult.setVisibility(View.GONE);
        clearFocus();
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    private void clearFocus() {
        binding.etHeight.clearFocus();
        binding.etWeight.clearFocus();
    }
}
