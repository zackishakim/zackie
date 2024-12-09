package com.example.campusexpensemanager;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import java.util.ArrayList;
import java.util.List;

public class OverviewActivity extends AppCompatActivity {

    TextView tvTitle, tvCurrency, tvDescTop3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        tvCurrency = findViewById(R.id.tvCurrency);
        tvTitle = findViewById(R.id.tvTitle);
        tvDescTop3 = findViewById(R.id.tvDescTop3);

        // Dummy data for testing
        String totalIncome = "5000000";
        String totalExpense = "3000000";
        String recurring = "500000";

        // Calculate income vs expense percentage
        float incomePercentage = (float) ((Integer.parseInt(totalIncome) * 100f)
                / (Integer.parseInt(totalIncome) + Integer.parseInt(totalExpense)));
        float expensePercentage = (float) ((Integer.parseInt(totalExpense) * 100f)
                / (Integer.parseInt(totalIncome) + Integer.parseInt(totalExpense)));

        tvTitle.setText("Total: " + (Integer.parseInt(totalIncome) - Integer.parseInt(totalExpense)
                - Integer.parseInt(recurring)) + " VND");

        // Initialize the PieChart
        PieChart pieChart = findViewById(R.id.pieChart);

        // Data for the PieChart
        ArrayList<PieEntry> entriesPie = new ArrayList<>();
        entriesPie.add(new PieEntry(incomePercentage, "Income"));
        entriesPie.add(new PieEntry(expensePercentage, "Expense"));

        // Create PieDataSet
        PieDataSet dataSet = new PieDataSet(entriesPie, "Income/Expense");
        dataSet.setColors(Color.GREEN, Color.RED); // Example colors for "Income" and "Expense"
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(14f);

        // Create PieData
        PieData pieData = new PieData(dataSet);

        // Configure the PieChart
        pieChart.setData(pieData);
        pieChart.setCenterText("Income vs Expense");
        pieChart.setCenterTextSize(18f);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false); // Disable the description

        // Refresh the PieChart
        pieChart.invalidate();

        // Initialize the BarChart
        BarChart barChart = findViewById(R.id.barChart);

        // Dummy data for the BarChart
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        ArrayList<String> months = new ArrayList<>();
        months.add("Jan");
        months.add("Feb");
        months.add("Mar");

        // Adding some sample data for the BarChart
        barEntries.add(new BarEntry(0f, new float[]{4000000, 2000000}));  // Income and Expense for Jan
        barEntries.add(new BarEntry(1f, new float[]{3500000, 2500000}));  // Income and Expense for Feb
        barEntries.add(new BarEntry(2f, new float[]{4500000, 3000000}));  // Income and Expense for Mar

        // Create a BarDataSet for the stacked bars
        BarDataSet barDataSet = new BarDataSet(barEntries, "Monthly Totals (Income/Expense)");
        barDataSet.setColors(Color.GREEN, Color.RED); // Set colors for "Income" and "Expense"
        barDataSet.setStackLabels(new String[]{"Income", "Expense"}); // Set labels for the stacked bars

        // Create BarData
        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.7f); // Set bar width

        // Configure the BarChart
        barChart.setData(barData);
        barChart.setFitBars(true);
        barChart.getDescription().setEnabled(false); // Disable chart description
        barChart.animateY(1000); // Animate Y-axis

        // Configure the X-Axis
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(months)); // Set month labels
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f); // Ensure intervals match entries
        xAxis.setGranularityEnabled(true);

        // Refresh the BarChart
        barChart.invalidate();
    }
}
