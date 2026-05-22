package com.example.powerpay;

import android.content.Intent;
import com.example.powerpay.R;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class CalculateActivity extends AppCompatActivity {

    private Spinner spinnerMonth;
    private EditText etUnit;
    private RadioGroup radioGroupRebate;
    private TextView tvTotalCharges, tvFinalCost;
    private Button btnCalculate, btnSave, btnViewHistory;

    private DBHelper dbHelper;
    private double totalCharges, finalCost;
    private String selectedMonth = "";
    private int units = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate);

        // Initialize views
        spinnerMonth = findViewById(R.id.spinnerMonth);
        etUnit = findViewById(R.id.etUnit);
        radioGroupRebate = findViewById(R.id.radioGroupRebate);
        tvTotalCharges = findViewById(R.id.tvTotalCharges);
        tvFinalCost = findViewById(R.id.tvFinalCost);
        btnCalculate = findViewById(R.id.btnCalculate);
        btnSave = findViewById(R.id.btnSave);
        btnViewHistory = findViewById(R.id.btnViewHistory);
        dbHelper = new DBHelper(this);

        // Populate spinner with month names
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.months)
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(adapter);

        // Calculate button logic
        btnCalculate.setOnClickListener(v -> calculateCharges());

        // Save button logic
        btnSave.setOnClickListener(v -> {
            if (finalCost > 0 && !selectedMonth.isEmpty()) {
                dbHelper.insertBill(selectedMonth, units, totalCharges, finalCost);
                Toast.makeText(this, "Bill saved successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please calculate the bill before saving.", Toast.LENGTH_SHORT).show();
            }
        });

        // View history button logic
        btnViewHistory.setOnClickListener(v -> {
            Intent intent = new Intent(CalculateActivity.this, HistoryActivity.class);
            startActivity(intent);
        });
    }

    private void calculateCharges() {
        String unitStr = etUnit.getText().toString().trim();

        if (unitStr.isEmpty()) {
            Toast.makeText(this, "Please enter electricity usage (units).", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedRebateId = radioGroupRebate.getCheckedRadioButtonId();
        if (selectedRebateId == -1) {
            Toast.makeText(this, "Please select a rebate percentage.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            units = Integer.parseInt(unitStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid unit input.", Toast.LENGTH_SHORT).show();
            return;
        }

        selectedMonth = spinnerMonth.getSelectedItem().toString();

        RadioButton selectedRadio = findViewById(selectedRebateId);
        String rebateText = selectedRadio.getText().toString().replace("%", "");
        double rebate = Double.parseDouble(rebateText);

        totalCharges = calculateElectricityBill(units);
        finalCost = totalCharges - (totalCharges * rebate / 100);

        tvTotalCharges.setText("Total Charges: RM " + String.format("%.2f", totalCharges));
        tvFinalCost.setText("Final Cost: RM " + String.format("%.2f", finalCost));
    }

    private double calculateElectricityBill(int units) {
        double total = 0;
        if (units <= 200) {
            total = units * 0.218;
        } else if (units <= 300) {
            total = 200 * 0.218 + (units - 200) * 0.334;
        } else if (units <= 600) {
            total = 200 * 0.218 + 100 * 0.334 + (units - 300) * 0.516;
        } else {
            total = 200 * 0.218 + 100 * 0.334 + 300 * 0.516 + (units - 600) * 0.546;
        }
        return total;
    }
}
