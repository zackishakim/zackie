package com.example.campusexpensemanager;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.campusexpensemanager.Model.objExpences;

public class AddExpenseActivity extends AppCompatActivity {
    private EditText editTextDate, editTextAmount, editTextDescription;
    private Spinner spinnerCategory;
    private Button buttonSaveExpense,buttonGoback;
    private DatabaseHelper dbHelper;
    int idExpences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        dbHelper = new DatabaseHelper(this);

        editTextDate = findViewById(R.id.editTextDate);
        editTextAmount = findViewById(R.id.editTextAmount);
        editTextDescription = findViewById(R.id.editTextDescription);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        buttonSaveExpense = findViewById(R.id.buttonSaveExpense);
        buttonGoback = findViewById(R.id.buttonGoback);

        String[] transactionTypes = {"Income", "Outcome"};
        // Tạo ArrayAdapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item, // Layout hiển thị dữ liệu
                transactionTypes
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Gắn Adapter vào Spinner
        spinnerCategory.setAdapter(adapter);

        // Retrieve data from the Intent
        Intent intent = getIntent();
        if (intent != null) {
            idExpences=intent.getIntExtra("expenses",-1);
            if(idExpences!=-1) {
                String expenseDate = intent.getStringExtra("expense_date");
                int expenseAmount = intent.getIntExtra("expense_amount", 0);
                String expenseDescription = intent.getStringExtra("expense_description");
                String expenseCategory = intent.getStringExtra("expense_category");


                // Set data to fields
                editTextDate.setText(expenseDate);
                editTextAmount.setText(String.valueOf(expenseAmount));
                editTextDescription.setText(expenseDescription);

                // Set the selected category
                if (expenseCategory != null) {
                    int spinnerPosition = adapter.getPosition(expenseCategory);
                    spinnerCategory.setSelection(spinnerPosition);
                }
                buttonSaveExpense.setText("Update Expence");
            }
        }else{
            buttonSaveExpense.setText("Save Expence");
        }




        buttonSaveExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = editTextDate.getText().toString().trim();
                int amount = Integer.parseInt(editTextAmount.getText().toString().trim());
                String description = editTextDescription.getText().toString().trim();
                String category = spinnerCategory.getSelectedItem().toString();

                if (buttonSaveExpense.getText().equals("Update Expence") && idExpences != 0) {
                    objExpences obj=new objExpences();
                    obj.setId(idExpences);
                    obj.setDate(date);
                    obj.setAmount(amount);
                    obj.setDescription(description);
                    obj.setCategory(category);
                    if (dbHelper.updateExpense(obj)) {
                        Toast.makeText(AddExpenseActivity.this,
                                "Expense saved", Toast.LENGTH_SHORT).show();
                        finish(); // Close activity and go back
                    } else {
                        Toast.makeText(AddExpenseActivity.this,
                                "Failed to save expense", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (dbHelper.addExpense(date, (int) amount, description, category)) {
                        Toast.makeText(AddExpenseActivity.this,
                                "Expense saved", Toast.LENGTH_SHORT).show();
                        finish(); // Close activity and go back
                    } else {
                        Toast.makeText(AddExpenseActivity.this,
                                "Failed to save expense", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        buttonGoback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Close activity and go back
            }
        });
    }
}
