package com.example.campusexpensemanager;


import static com.example.campusexpensemanager.function.formatCurrencyVND;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.example.campusexpensemanager.Adapters.ExpencesAdapter;
import com.example.campusexpensemanager.Model.objExpences;


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper dbHelper;
    ListView listViewExpenses;
    Button buttonAddExpense,buttonOverview,buttonRecurringExprnse, buttonBudget;
    ArrayList<objExpences> _arrExpences;
    ExpencesAdapter adapterExpences;
    TextView tvTitleMain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        listViewExpenses = (ListView)findViewById(R.id.listViewExpenses);
        buttonAddExpense =(Button) findViewById(R.id.buttonAddExpense);
        buttonOverview =(Button) findViewById(R.id.buttonOverview);
        buttonRecurringExprnse=(Button) findViewById(R.id.buttonRecurring);
        tvTitleMain=(TextView)findViewById(R.id.tvTitleMain);
        SearchView searchView = (SearchView) findViewById(R.id.searchView);
        buttonBudget=(Button) findViewById(R.id.buttonBudget);


        buttonAddExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(MainActivity.this,AddExpenseActivity.class);
                startActivity(intent);
            }
        });
        buttonRecurringExprnse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(MainActivity.this,RecurringActivity.class);
                startActivity(intent);
            }
        });
        buttonOverview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OverviewActivity.class);
                startActivity(intent);
            }
        });

        buttonBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mở màn hình BudgetActivity
                Intent intent = new Intent(MainActivity.this, BudgetActivity.class);
                startActivity(intent);
            }
        });

        // get all expences
        loadExpenses();


        //create menuContext for Listview
        registerForContextMenu(listViewExpenses);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapterExpences.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapterExpences.getFilter().filter(newText);
                return true;
            }
        });
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        // Inflate menu resource
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.item_menu, menu);

        menu.setHeaderTitle("Choose an action");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position; // Get the position of the clicked item
        objExpences expense = _arrExpences.get(position); // Access the clicked item

        CharSequence title = item.getTitle();
        if (title.equals("Edit")) {

            Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);

            // Pass data to the new activity
            intent.putExtra("expenses", expense.getId());
            intent.putExtra("expense_date", expense.getDate());
            intent.putExtra("expense_amount", expense.getAmount());
            intent.putExtra("expense_description", expense.getDescription());
            intent.putExtra("expense_category", expense.getCategory());

            // Start AddExpenseActivity
            startActivity(intent);
            return true;
        } else if (title.equals("Delete")) {// Handle Delete action
            Toast.makeText(this, "Delete: " + expense.getDescription(), Toast.LENGTH_SHORT).show();
            dbHelper.deleteExpense(expense.getId());
            _arrExpences.remove(position);
            adapterExpences.notifyDataSetChanged();
            return true;
        }
        return super.onContextItemSelected(item);
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadExpenses(); // Refresh the list when returning to MainActivity
    }
    private void loadExpenses() {

        _arrExpences =dbHelper.getAllExpenses();
        if(_arrExpences.size()==0){
            dbHelper.createDATA();
            _arrExpences =dbHelper.getAllExpenses();
        }

        adapterExpences = new ExpencesAdapter(this, _arrExpences);
        listViewExpenses.setAdapter(adapterExpences);

        String total=dbHelper.getTotalExpensesAsJson();
        try {
            JSONObject jsonObject = new JSONObject(total);
            int totalIncome = jsonObject.optInt("totalIncome", 0);
            int totalExpense = jsonObject.optInt("totalExpense", 0);
            int recurring = jsonObject.optInt("recurring", 0);
            int totalBudget = jsonObject.optInt("totalBudget", 0);


            tvTitleMain.setText(formatCurrencyVND(totalIncome - totalExpense - recurring));

            if(totalBudget<(totalExpense - recurring)){

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }






}
