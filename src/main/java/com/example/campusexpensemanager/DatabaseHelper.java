package com.example.campusexpensemanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.campusexpensemanager.Adapters.ExpencesAdapter;
import com.example.campusexpensemanager.Model.RecurringExpense;
import com.example.campusexpensemanager.Model.objBudgetActivity;
import com.example.campusexpensemanager.Model.objExpences;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {



    // Database Name and Version
    private static final String DATABASE_NAME = "CampusExpenseManager.db";
    private static final int DATABASE_VERSION = 8;

    // User Table
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";

    // Expense Table
    public static final String TABLE_EXPENSES = "expenses";
    public static final String COLUMN_EXPENSE_ID = "id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_CATEGORY = "category";

    // Budget Activity Table
    public static final String TABLE_BUDGET_ACTIVITY = "budget_activity";
    public static final String COLUMN_BUDGET_ID = "id";
    public static final String COLUMN_BUDGET_NAME = "name";
    public static final String COLUMN_BUDGET_AMOUNT = "budget_amount";
    public static final String COLUMN_BUDGET_START_DATE = "start_date";
    public static final String COLUMN_BUDGET_END_DATE = "end_date";


    // Constructor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Users Table
        String createUserTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT NOT NULL, " +
                COLUMN_PASSWORD + " TEXT NOT NULL);";
        db.execSQL(createUserTable);

        // Create Expenses Table
        String createExpenseTable = "CREATE TABLE " + TABLE_EXPENSES + " (" +
                COLUMN_EXPENSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DATE + " TEXT NOT NULL, " +
                COLUMN_AMOUNT + " INTEGER NOT NULL, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_CATEGORY + " TEXT);";
        db.execSQL(createExpenseTable);


        db.execSQL("CREATE TABLE recurring_expenses (" +
                "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "    description TEXT NOT NULL," +
                "    amount REAL NOT NULL," +
                "    category TEXT NOT NULL," +
                "    start_date TEXT NOT NULL," +
                "    end_date TEXT" +
                ");");

        // Create Budget Activity Table
        String createBudgetActivityTable = "CREATE TABLE " + TABLE_BUDGET_ACTIVITY + " (" +
                COLUMN_BUDGET_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_BUDGET_NAME + " TEXT NOT NULL, " +
                COLUMN_BUDGET_AMOUNT + " REAL NOT NULL, " +
                COLUMN_BUDGET_START_DATE + " TEXT NOT NULL, " +
                COLUMN_BUDGET_END_DATE + " TEXT);";
        db.execSQL(createBudgetActivityTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSES);
        db.execSQL("DROP TABLE IF EXISTS recurring_expenses;");
        db.execSQL("DROP TABLE IF EXISTS  "+TABLE_BUDGET_ACTIVITY);
        onCreate(db);
    }

    // Add Budget Activity
    public boolean addBudgetActivity(String name, double amount, String startDate, String endDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_BUDGET_NAME, name);
        values.put(COLUMN_BUDGET_AMOUNT, amount);
        values.put(COLUMN_BUDGET_START_DATE, startDate);
        values.put(COLUMN_BUDGET_END_DATE, endDate);

        long result = db.insert(TABLE_BUDGET_ACTIVITY, null, values);
        db.close();
        return result != -1;
    }

    // Get All Budget Activities
    public ArrayList<objBudgetActivity> getAllBudgetActivities() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<objBudgetActivity> budgetActivities = new ArrayList<>();
        Cursor cursor = db.query(TABLE_BUDGET_ACTIVITY, null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                objBudgetActivity activity = new objBudgetActivity();
                activity.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BUDGET_ID)));
                activity.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BUDGET_NAME)));
                activity.setAmount(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_BUDGET_AMOUNT)));
                activity.setStartDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BUDGET_START_DATE)));
                activity.setEndDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BUDGET_END_DATE)));
                budgetActivities.add(activity);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return budgetActivities;
    }

    // Update Budget Activity
    public boolean updateBudgetActivity(objBudgetActivity activity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_BUDGET_NAME, activity.getName());
        values.put(COLUMN_BUDGET_AMOUNT, activity.getAmount());
        values.put(COLUMN_BUDGET_START_DATE, activity.getStartDate());
        values.put(COLUMN_BUDGET_END_DATE, activity.getEndDate());

        int result = db.update(TABLE_BUDGET_ACTIVITY, values,
                COLUMN_BUDGET_ID + "=?", new String[]{String.valueOf(activity.getId())});
        db.close();
        return result > 0;
    }

    // Delete Budget Activity
    public boolean deleteBudgetActivity(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_BUDGET_ACTIVITY,
                COLUMN_BUDGET_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }




    public boolean registerUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password); // Ideally, hash the password here

        long result = db.insert(TABLE_USERS, null, values);
        return result != -1; // Returns true if insert is successful
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username, password});

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists; // Returns true if user exists with the provided username and password
    }

    public boolean addExpense(String date, int amount, String description, String category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_AMOUNT, amount);
        values.put(COLUMN_DESCRIPTION, description);
        values.put(COLUMN_CATEGORY, category);

        long result = db.insert(TABLE_EXPENSES, null, values);
        return result != -1;
    }

    public boolean updateExpense(objExpences obj) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, obj.getDate());
        values.put(COLUMN_AMOUNT, obj.getAmount());
        values.put(COLUMN_DESCRIPTION, obj.getDescription());
        values.put(COLUMN_CATEGORY, obj.getCategory());

        int result = db.update(TABLE_EXPENSES, values,
                COLUMN_EXPENSE_ID + "=?", new String[]{String.valueOf(obj.getId())});
        return result > 0;
    }

    public boolean deleteExpense(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_EXPENSES,
                COLUMN_EXPENSE_ID + "=?", new String[]{String.valueOf(id)});
        return result > 0;
    }

    public ArrayList<objExpences> getAllExpenses() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<objExpences> arr = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = db.query(TABLE_EXPENSES,
                    null, null, null, null, null,
                    COLUMN_EXPENSE_ID + " DESC");

            if (cursor != null && cursor.moveToFirst()) { // Ensure cursor is not null and has data
                do {
                    objExpences obj = new objExpences();
                    obj.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_EXPENSE_ID)));
                    obj.setAmount(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT)));
                    obj.setDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)));
                    obj.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)));
                    obj.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)));
                    arr.add(obj);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close(); // Always close the cursor to avoid memory leaks
            }
            db.close(); // Close database connection
        }

        return arr;
    }
    public ArrayList<objExpences> getBy_like(String desc) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<objExpences> arr = new ArrayList<>();
        Cursor cursor = null;

        try {
            String selection = "desc LIKE ?";
            String[] selectionArgs = new String[]{"%" + desc + "%"};
            cursor = db.query(TABLE_EXPENSES,
                    null, selection, selectionArgs, null, null,
                    COLUMN_EXPENSE_ID + " DESC");

            if (cursor != null && cursor.moveToFirst()) { // Ensure cursor is not null and has data
                do {
                    objExpences obj = new objExpences();
                    obj.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_EXPENSE_ID)));
                    obj.setAmount(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT)));
                    obj.setDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)));
                    obj.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)));
                    obj.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)));
                    arr.add(obj);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close(); // Always close the cursor to avoid memory leaks
            }
            db.close(); // Close database connection
        }

        return arr;
    }

    public ArrayList<objExpences> getBy_Date(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<objExpences> arr = new ArrayList<>();
        Cursor cursor = null;

        try {
            String selection = "date LIKE ?";
            String[] selectionArgs = new String[]{"" + date + ""};
            cursor = db.query(TABLE_EXPENSES,
                    null, selection, selectionArgs, null, null,
                    COLUMN_EXPENSE_ID + " DESC");

            if (cursor != null && cursor.moveToFirst()) { // Ensure cursor is not null and has data
                do {
                    objExpences obj = new objExpences();
                    obj.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_EXPENSE_ID)));
                    obj.setAmount(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT)));
                    obj.setDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)));
                    obj.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)));
                    obj.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)));
                    arr.add(obj);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close(); // Always close the cursor to avoid memory leaks
            }
            db.close(); // Close database connection
        }

        return arr;
    }

    public String getTotalExpensesAsJson() {
        SQLiteDatabase db = this.getReadableDatabase();
        JSONObject jsonResult = new JSONObject();
        Cursor cursor = null;

        String sql = "SELECT " +
                "(SELECT SUM(amount) FROM recurring_expenses  " +
                "WHERE (start_date <= '2024-11-01' AND (end_date IS NULL OR end_date >= '2024-11-30'))) AS recurring, " +
                "(SELECT SUM("+COLUMN_BUDGET_AMOUNT+") FROM "+TABLE_BUDGET_ACTIVITY+") AS totalBudget,"+
                "(SELECT SUM(amount) FROM expenses WHERE category = 'Income') AS totalIncome, " +
                "(SELECT SUM(amount) FROM expenses WHERE category = 'Outcome') AS totalExpense";

        try {
            cursor = db.rawQuery(sql, null);

            if (cursor != null && cursor.moveToFirst()) {
                int totalIncome = cursor.getInt(cursor.getColumnIndexOrThrow("totalIncome"));
                int totalExpense = cursor.getInt(cursor.getColumnIndexOrThrow("totalExpense"));
                int recurring = cursor.getInt(cursor.getColumnIndexOrThrow("recurring"));
                int totalBudget = cursor.getInt(cursor.getColumnIndexOrThrow("totalBudget"));
                // Add data to JSON object
                jsonResult.put("totalIncome", totalIncome);
                jsonResult.put("totalExpense", totalExpense);
                jsonResult.put("recurring", recurring);
                jsonResult.put("totalBudget", totalBudget);
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                jsonResult.put("error", e.getMessage());
            } catch (Exception jsonException) {
                jsonException.printStackTrace();
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return jsonResult.toString();
    }

    public JSONArray getMonthlyTotalsAsJson() {
        SQLiteDatabase db = this.getReadableDatabase();
        JSONArray jsonArray = new JSONArray();

        String sql = "SELECT " +
                "strftime('%m', date) AS month, " +
                "SUM(CASE WHEN category = 'Income' THEN amount ELSE 0 END) AS totalThu, " +
                "SUM(CASE WHEN category = 'Outcome' THEN amount ELSE 0 END) AS totalChi," +
                "SUM(amount) AS total " +
                "FROM expenses " +
                "GROUP BY month " +
                "ORDER BY month";

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    // Create a JSON object for each row
                    JSONObject jsonObject = new JSONObject();
                    String month = cursor.getString(cursor.getColumnIndexOrThrow("month"));
                    double totalThu = cursor.getDouble(cursor.getColumnIndexOrThrow("totalThu"));
                    double totalChi = cursor.getDouble(cursor.getColumnIndexOrThrow("totalChi"));
                    double total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"));
                    jsonObject.put("month", month);
                    jsonObject.put("totalThu", totalThu);
                    jsonObject.put("totalChi", totalChi);
                    jsonObject.put("total", total);

                    // Add the JSON object to the JSON array
                    jsonArray.put(jsonObject);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return jsonArray;
    }
    public JSONArray getTop3Chi_byMonth(String yyyymm) {
        SQLiteDatabase db = this.getReadableDatabase();
        JSONArray jsonArray = new JSONArray();



        Cursor cursor = null;
        String selection = "strftime('%Y-%m',date)=? and category=?";
        String[] selectionArgs = new String[]{yyyymm,"Chi"};
        try {
            cursor = db.query("expenses", new String[]{"strftime('%Y-%m', date) AS month", "description", "SUM(amount) AS total"},
                    selection,selectionArgs,
                    "description,strftime('%Y-%m', date)",
                    null,"total desc","3");
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    // Create a JSON object for each row
                    JSONObject jsonObject = new JSONObject();
                    String month = cursor.getString(cursor.getColumnIndexOrThrow("month"));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                    double total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"));
                    jsonObject.put("month", month);
                    jsonObject.put("description", description);
                    jsonObject.put("total", total);
                    // Add the JSON object to the JSON array
                    jsonArray.put(jsonObject);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return jsonArray;
    }
    public void createDATA() {
        String sql = "-- Insert 50 sample records\n" +
                "INSERT INTO expenses (date, amount, description, category)\n" +
                "VALUES\n" +
                "    -- January\n" +
                "    ('2024-01-03', 150.00, 'Groceries', 'Outcome'),\n" +
                "    ('2024-01-15', 2000.00, 'Salary', 'Income'),\n" +
                "    ('2024-01-20', 500.00, 'Electricity Bill', 'Outcome'),\n" +
                "    ('2024-01-25', 700.00, 'Freelance Payment', 'Income'),\n" +
                "    ('2024-01-28', 300.00, 'Transport', 'Outcome'),\n" +
                "\n" +
                "    -- February\n" +
                "    ('2024-02-05', 180.00, 'Groceries', 'Outcome'),\n" +
                "    ('2024-02-12', 1000.00, 'Freelance Payment', 'Income'),\n" +
                "    ('2024-02-18', 250.00, 'Dining Out', 'Outcome'),\n" +
                "    ('2024-02-23', 450.00, 'Gift from Friend', 'Income'),\n" +
                "    ('2024-02-28', 150.00, 'Phone Bill', 'Outcome'),\n" +
                "\n" +
                "    -- March\n" +
                "    ('2024-03-02', 300.00, 'Shopping', 'Outcome'),\n" +
                "    ('2024-03-10', 2500.00, 'Salary', 'Income'),\n" +
                "    ('2024-03-15', 700.00, 'Car Maintenance', 'Outcome'),\n" +
                "    ('2024-03-22', 600.00, 'Freelance Payment', 'Income'),\n" +
                "    ('2024-03-30', 400.00, 'Transport', 'Outcome'),\n" +
                "\n" +
                "    -- April\n" +
                "    ('2024-04-01', 200.00, 'Groceries', 'Outcome'),\n" +
                "    ('2024-04-12', 1000.00, 'Freelance Payment', 'Income'),\n" +
                "    ('2024-04-18', 180.00, 'Coffee', 'Outcome'),\n" +
                "    ('2024-04-24', 500.00, 'Gift', 'Income'),\n" +
                "    ('2024-04-29', 150.00, 'Phone Bill', 'Outcome'),\n" +
                "\n" +
                "    -- May\n" +
                "    ('2024-05-06', 400.00, 'Groceries', 'Outcome'),\n" +
                "    ('2024-05-14', 3000.00, 'Bonus', 'Income'),\n" +
                "    ('2024-05-19', 500.00, 'Dining Out', 'Outcome'),\n" +
                "    ('2024-05-25', 750.00, 'Freelance Payment', 'Income'),\n" +
                "    ('2024-05-31', 180.00, 'Transport', 'Outcome'),\n" +
                "\n" +
                "    -- June\n" +
                "    ('2024-06-03', 250.00, 'Groceries', 'Outcome'),\n" +
                "    ('2024-06-15', 2000.00, 'Salary', 'Income'),\n" +
                "    ('2024-06-20', 300.00, 'Shopping', 'Outcome'),\n" +
                "    ('2024-06-27', 500.00, 'Freelance Payment', 'Income'),\n" +
                "    ('2024-06-30', 200.00, 'Transport', 'Outcome'),\n" +
                "\n" +
                "    -- July\n" +
                "    ('2024-07-04', 300.00, 'Groceries', 'Outcome'),\n" +
                "    ('2024-07-12', 2500.00, 'Salary', 'Income'),\n" +
                "    ('2024-07-18', 500.00, 'Car Maintenance', 'Outcome'),\n" +
                "    ('2024-07-26', 600.00, 'Freelance Payment', 'Income'),\n" +
                "    ('2024-07-30', 400.00, 'Phone Bill', 'Outcome'),\n" +
                "\n" +
                "    -- August\n" +
                "    ('2024-08-02', 200.00, 'Groceries', 'Outcome'),\n" +
                "    ('2024-08-10', 3000.00, 'Salary', 'Income'),\n" +
                "    ('2024-08-15', 400.00, 'Dining Out', 'Outcome'),\n" +
                "    ('2024-08-20', 800.00, 'Freelance Payment', 'Income'),\n" +
                "    ('2024-08-27', 250.00, 'Coffee', 'Outcome'),\n" +
                "\n" +
                "    -- September\n" +
                "    ('2024-09-01', 350.00, 'Groceries', 'Outcome'),\n" +
                "    ('2024-09-12', 2500.00, 'Bonus', 'Income'),\n" +
                "    ('2024-09-18', 200.00, 'Shopping', 'Outcome'),\n" +
                "    ('2024-09-25', 750.00, 'Gift', 'Income'),\n" +
                "    ('2024-09-30', 150.00, 'Phone Bill', 'Outcome'),\n" +
                "\n" +
                "    -- October\n" +
                "    ('2024-10-04', 180.00, 'Groceries', 'Outcome'),\n" +
                "    ('2024-10-14', 3000.00, 'Freelance Payment', 'Income'),\n" +
                "    ('2024-10-21', 250.00, 'Coffee', 'Outcome'),\n" +
                "    ('2024-10-28', 500.00, 'Gift', 'Income'),\n" +
                "    ('2024-10-31', 300.00, 'Transport', 'Outcome'),\n" +
                "\n" +
                "    -- November\n" +
                "    ('2024-11-06', 400.00, 'Groceries', 'Outcome'),\n" +
                "    ('2024-11-12', 2500.00, 'Salary', 'Income'),\n" +
                "    ('2024-11-18', 300.00, 'Dining Out', 'Outcome'),\n" +
                "    ('2024-11-24', 600.00, 'Freelance Payment', 'Income'),\n" +
                "    ('2024-11-30', 200.00, 'Transport', 'Outcome'),\n" +
                "\n" +
                "    -- December\n" +
                "    ('2024-12-02', 350.00, 'Groceries', 'Outcome'),\n" +
                "    ('2024-12-15', 3000.00, 'Bonus', 'Income'),\n" +
                "    ('2024-12-21', 250.00, 'Coffee', 'Outcome'),\n" +
                "    ('2024-12-27', 800.00, 'Freelance Payment', 'Income'),\n" +
                "    ('2024-12-30', 400.00, 'Phone Bill', 'Outcome');";
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL(sql);
    }


    public boolean addRecurringExpense(RecurringExpense expense ) {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("description", expense.getDescription());
        values.put("amount", expense.getAmount());
        values.put("category", expense.getCategory());
        values.put("start_date", expense.getStartDate());
        values.put("end_date", expense.getEndDate());
        long result = db.insert("recurring_expenses", null, values);
        db.close();
        return result != -1; // Trả về true nếu thêm thành công
    }
}
