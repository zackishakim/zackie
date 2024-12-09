package com.example.campusexpensemanager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.campusexpensemanager.Model.RecurringExpense;

public class RecurringActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recurring);

        // Đảm bảo tên ID trong Java khớp với XML
        EditText editDesc = findViewById(R.id.etDescription);  // Đây phải khớp với tên trong XML
        EditText editAmount = findViewById(R.id.etAmount);  // Khớp với XML
        EditText editStart = findViewById(R.id.editStartDate);  // Khớp với XML
        EditText editEnd = findViewById(R.id.editEndDate);  // Khớp với XML
        Button btnAddRecurringExpense = findViewById(R.id.btnAddRecurringExpense);

        btnAddRecurringExpense.setOnClickListener(v -> {
            RecurringExpense expense = new RecurringExpense();
            if (!editAmount.getText().toString().isEmpty()) {
                double amount = Double.parseDouble(editAmount.getText().toString());
                expense.setAmount(amount);
            } else {
                Toast.makeText(this, "Amount is required", Toast.LENGTH_SHORT).show();
                return;
            }

            expense.setDescription(editDesc.getText().toString());
            expense.setStartDate(editStart.getText().toString());
            expense.setEndDate(editEnd.getText().toString());
            expense.setCategory("");  // Nếu có, bạn có thể thêm phần Category

            // Thêm vào cơ sở dữ liệu
            DatabaseHelper db = new DatabaseHelper(this);
            if (db.addRecurringExpense(expense)) {
                Toast.makeText(this, "Recurring Expense Added", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error Adding Expense", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
