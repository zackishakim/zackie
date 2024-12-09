package com.example.campusexpensemanager;

import static com.example.campusexpensemanager.function.formatCurrencyVND;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.campusexpensemanager.Model.objExpences;

import org.json.JSONObject;

import java.util.ArrayList;

public class BudgetActivity extends AppCompatActivity {



    private TextView tvTotalIncome, tvTotalExpenses, tvRemainingBudget;
    private EditText etBudgetAmount;
    private Button btnSetBudget, btnViewBudgetDetails;
    private DatabaseHelper databaseHelper;
    private int totalIncome = 0, totalExpenses = 0;
    private int budgetAmount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        // Initialize views
        tvTotalIncome = findViewById(R.id.tvTotalIncome);
        tvTotalExpenses = findViewById(R.id.tvTotalExpenses);
        tvRemainingBudget = findViewById(R.id.tvRemainingBudget);
        etBudgetAmount = findViewById(R.id.etBudgetAmount);
        btnSetBudget = findViewById(R.id.btnSetBudget);
        btnViewBudgetDetails = findViewById(R.id.btnViewBudgetDetails);

        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Calculate and display total income and expenses
        updateBudgetInfo();

        String total=databaseHelper.getTotalExpensesAsJson();
        try {
            JSONObject jsonObject = new JSONObject(total);
            int totalIncome = jsonObject.optInt("totalIncome", 0);
            int totalExpense = jsonObject.optInt("totalExpense", 0);
            int recurring = jsonObject.optInt("recurring", 0);
            int totalBudget = jsonObject.optInt("totalBudget", 0);

            tvTotalIncome.setText(totalIncome+"");
            tvTotalExpenses.setText(totalExpense+"");
            tvRemainingBudget.setText(totalBudget+"");


        } catch (Exception e) {
            e.printStackTrace();
        }


        // Set Budget Button
        btnSetBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String budgetInput = etBudgetAmount.getText().toString();
                if (budgetInput.isEmpty()) {
                    Toast.makeText(BudgetActivity.this,
                            "Please enter a valid budget amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                budgetAmount = Integer.parseInt(budgetInput);
                showConfirmationDialog(budgetAmount);
            }
        });

        // View Budget Details Button
        btnViewBudgetDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBudgetDetailsDialog();
            }
        });
    }

    // Method to update total income, total expenses, and remaining budget
    private void updateBudgetInfo() {
        ArrayList<objExpences> allExpenses = databaseHelper.getAllExpenses();
        totalIncome = 0;
        totalExpenses = 0;

        // Calculate total income and expenses
        for (objExpences expense : allExpenses) {
            if ("income".equals(expense.getCategory())) {
                totalIncome += expense.getAmount();
            } else if ("expense".equals(expense.getCategory())) {
                totalExpenses += expense.getAmount();
            }
        }

        // Update UI
        tvTotalIncome.setText("Total Income: " + totalIncome);
        tvTotalExpenses.setText("Total Expenses: " + totalExpenses);
        int remainingBudget = budgetAmount - totalExpenses;
        tvRemainingBudget.setText("Remaining Budget: " + remainingBudget);
    }

    // Show confirmation dialog when setting a new budget
    private void showConfirmationDialog(int budgetAmount) {
        new AlertDialog.Builder(this)
                .setTitle("Set Budget")
                .setMessage("Are you sure you want to set the budget to " + budgetAmount + "?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(BudgetActivity.this, "Budget set to: " + budgetAmount, Toast.LENGTH_SHORT).show();
                        databaseHelper.addBudgetActivity("",Integer.parseInt(etBudgetAmount.getText().toString()),
                                "","");
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    // Show budget details in a dialog
    private void showBudgetDetailsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Budget Details");

        StringBuilder details = new StringBuilder();
        details.append("Total Income: ").append(totalIncome).append("\n");
        details.append("Total Expenses: ").append(totalExpenses).append("\n");
        details.append("Remaining Budget: ").append(budgetAmount - totalExpenses).append("\n");

        builder.setMessage(details.toString());
        builder.setPositiveButton("OK", null);

        builder.show();
    }
}
