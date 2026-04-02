package com.example.travelcompanionapp;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Spinner spinnerCategory, spinnerFrom, spinnerTo;
    EditText editTextValue;
    Button buttonConvert;
    TextView textViewResult;

    String[] categories = {"Currency", "Fuel", "Temperature"};

    String[] currencyUnits = {"USD", "AUD", "EUR", "JPY", "GBP"};
    String[] fuelUnits = {"mpg", "km/L", "Gallon", "Liter", "Nautical Mile", "Kilometer"};
    String[] temperatureUnits = {"Celsius", "Fahrenheit", "Kelvin"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerFrom = findViewById(R.id.spinnerFrom);
        spinnerTo = findViewById(R.id.spinnerTo);
        editTextValue = findViewById(R.id.editTextValue);
        buttonConvert = findViewById(R.id.buttonConvert);
        textViewResult = findViewById(R.id.textViewResult);

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        updateUnitSpinners("Currency");

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = spinnerCategory.getSelectedItem().toString();
                updateUnitSpinners(selectedCategory);
                textViewResult.setText("Result will appear here");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        buttonConvert.setOnClickListener(v -> {
            String inputText = editTextValue.getText().toString().trim();

            if (inputText.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please enter a value", Toast.LENGTH_SHORT).show();
                return;
            }

            double inputValue;
            try {
                inputValue = Double.parseDouble(inputText);
            } catch (NumberFormatException e) {
                Toast.makeText(MainActivity.this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
                return;
            }

            String category = spinnerCategory.getSelectedItem().toString();
            String fromUnit = spinnerFrom.getSelectedItem().toString();
            String toUnit = spinnerTo.getSelectedItem().toString();

            if (fromUnit.equals(toUnit)) {
                textViewResult.setText("Converted value: " + formatResult(inputValue));
                return;
            }

            if (category.equals("Fuel") && inputValue < 0) {
                Toast.makeText(MainActivity.this,
                        "Negative values are not allowed for Fuel conversions",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (category.equals("Currency") && inputValue < 0) {
                Toast.makeText(MainActivity.this,
                        "Negative currency values are not allowed",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (category.equals("Fuel") && !isValidFuelConversion(fromUnit, toUnit)) {
                Toast.makeText(MainActivity.this,
                        "Invalid Fuel conversion pair",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            double result;

            if (category.equals("Currency")) {
                result = convertCurrency(fromUnit, toUnit, inputValue);
            } else if (category.equals("Fuel")) {
                result = convertFuel(fromUnit, toUnit, inputValue);
            } else {
                result = convertTemperature(fromUnit, toUnit, inputValue);
            }

            textViewResult.setText("Converted value: " + formatResult(result));
        });
    }

    private void updateUnitSpinners(String category) {
        String[] units;

        if (category.equals("Currency")) {
            units = currencyUnits;
        } else if (category.equals("Fuel")) {
            units = fuelUnits;
        } else {
            units = temperatureUnits;
        }

        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                units
        );
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerFrom.setAdapter(unitAdapter);
        spinnerTo.setAdapter(unitAdapter);
    }

    private double convertCurrency(String from, String to, double value) {
        double usdValue;

        if (from.equals("USD")) {
            usdValue = value;
        } else if (from.equals("AUD")) {
            usdValue = value / 1.55;
        } else if (from.equals("EUR")) {
            usdValue = value / 0.92;
        } else if (from.equals("JPY")) {
            usdValue = value / 148.50;
        } else {
            usdValue = value / 0.78;
        }

        if (to.equals("USD")) {
            return usdValue;
        } else if (to.equals("AUD")) {
            return usdValue * 1.55;
        } else if (to.equals("EUR")) {
            return usdValue * 0.92;
        } else if (to.equals("JPY")) {
            return usdValue * 148.50;
        } else {
            return usdValue * 0.78;
        }
    }

    private double convertFuel(String from, String to, double value) {
        if (from.equals("mpg") && to.equals("km/L")) {
            return value * 0.425;
        } else if (from.equals("km/L") && to.equals("mpg")) {
            return value / 0.425;
        } else if (from.equals("Gallon") && to.equals("Liter")) {
            return value * 3.785;
        } else if (from.equals("Liter") && to.equals("Gallon")) {
            return value / 3.785;
        } else if (from.equals("Nautical Mile") && to.equals("Kilometer")) {
            return value * 1.852;
        } else if (from.equals("Kilometer") && to.equals("Nautical Mile")) {
            return value / 1.852;
        }

        return value;
    }

    private double convertTemperature(String from, String to, double value) {
        if (from.equals("Celsius")) {
            if (to.equals("Fahrenheit")) {
                return (value * 1.8) + 32;
            } else if (to.equals("Kelvin")) {
                return value + 273.15;
            }
        }

        if (from.equals("Fahrenheit")) {
            if (to.equals("Celsius")) {
                return (value - 32) / 1.8;
            } else if (to.equals("Kelvin")) {
                return ((value - 32) / 1.8) + 273.15;
            }
        }

        if (from.equals("Kelvin")) {
            if (to.equals("Celsius")) {
                return value - 273.15;
            } else if (to.equals("Fahrenheit")) {
                return ((value - 273.15) * 1.8) + 32;
            }
        }

        return value;
    }

    private boolean isValidFuelConversion(String from, String to) {
        boolean fuelEfficiencyPair =
                (from.equals("mpg") && to.equals("km/L")) ||
                        (from.equals("km/L") && to.equals("mpg"));

        boolean volumePair =
                (from.equals("Gallon") && to.equals("Liter")) ||
                        (from.equals("Liter") && to.equals("Gallon"));

        boolean distancePair =
                (from.equals("Nautical Mile") && to.equals("Kilometer")) ||
                        (from.equals("Kilometer") && to.equals("Nautical Mile"));

        return fuelEfficiencyPair || volumePair || distancePair;
    }

    private String formatResult(double value) {
        return String.format(Locale.US, "%.2f", value);
    }
}