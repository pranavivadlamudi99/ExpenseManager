package com.pranavi.expensemanager;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.pranavi.expensemanager.adapter.ExpenseAdapter;
import com.pranavi.expensemanager.database.ExpenseDatabase;
import com.pranavi.expensemanager.model.Expense;
import com.pranavi.expensemanager.model.ExpenseViewModel;
import com.github.mikephil.charting.charts.PieChart;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements ExpenseAdapter.ExpenseActionsListener {
    private ExpenseDatabase db;
    private ExpenseAdapter adapter;
    private List<Expense> expenseList;
    private RecyclerView recyclerView;
    private Button buttonAdd, buttonSelectDate;
    private EditText editTextAmount, editTextExpenseName;
    private TextView textSelectedDate;
    private PieChart pieChart;
    private Spinner spinnerCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }

        ExpenseViewModel expenseViewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);

        recyclerView = findViewById(R.id.recyclerView);
        buttonAdd = findViewById(R.id.buttonAddExpense);
        editTextAmount = findViewById(R.id.editTextAmount);
        editTextExpenseName = findViewById(R.id.editTextExpenseName);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        buttonSelectDate = findViewById(R.id.buttonSelectDate);
        textSelectedDate = findViewById(R.id.textSelectedDate);
        pieChart = findViewById(R.id.pieChart);

        adapter = new ExpenseAdapter(this, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(true);

        db = ExpenseDatabase.getInstance(this);

        if (db == null) {
            Log.e("MainActivity", "Database is NULL!");
        } else {
            Log.d("MainActivity", "Database initialized successfully.");
        }

        String[] categories = {"Select Category", "Food", "Transport", "Entertainment", "Shopping", "Bills",
                "Healthcare", "Education", "Groceries", "Rent", "Others"};

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categories);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(spinnerAdapter);

        // Observe LiveData and update RecyclerView when data changes
        expenseViewModel.getAllExpenses().observe(this, expenses -> {
            if (expenses != null) {
                adapter.updateList(expenses);
            }
        });

        editTextAmount.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editTextAmount, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        editTextExpenseName.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editTextExpenseName, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        buttonSelectDate.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (datePicker, selectedYear, selectedMonth, selectedDay) -> {
                String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                Log.d("Date", "MainActivity Selected Date: " + selectedDate);
                textSelectedDate.setText(selectedDate);
                Log.d("Date", "MainActivity Text Selected Date: " + textSelectedDate.getText().toString());
            }, year, month, day);

            datePickerDialog.show();
        });

        Log.d("Date", "MainActivity Text outside Selected Date: " + textSelectedDate.getText().toString());
        buttonAdd.setOnClickListener(view -> {
            Log.d("Date", "MainActivity Text inside add Selected Date: " + textSelectedDate.getText().toString());
            double amount = Double.parseDouble(editTextAmount.getText().toString());
            String category = spinnerCategory.getSelectedItem().toString();
            String date = textSelectedDate.getText().toString();
            String expenseName = editTextExpenseName.getText().toString();

            Expense newExpense = new Expense(amount, category, expenseName, date);
            expenseViewModel.insertExpense(newExpense);
        });

        db.expenseDao().getAllExpenses().observe(this, expenses -> {
            if (expenses != null) {
                updatePieChart(expenses);
            }
        });
    }


    // Implementing Edit & Delete functions
    @Override
    public void editExpense(Expense expense) {
        // Example: Show a dialog for editing (You can implement a proper UI for this)
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Expense");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText inputAmount = new EditText(this);
        inputAmount.setText(String.valueOf(expense.getAmount()));
        layout.addView(inputAmount);

        final EditText inputCategory = new EditText(this);
        inputCategory.setText(expense.getCategory());
        layout.addView(inputCategory);

        builder.setView(layout);

        builder.setPositiveButton("Update", (dialog, which) -> {
            double newAmount = Double.parseDouble(inputAmount.getText().toString());
            String newCategory = inputCategory.getText().toString();

            expense.setAmount(newAmount);
            expense.setCategory(newCategory);
            Executors.newSingleThreadExecutor().execute(() -> {
                        db.expenseDao().updateExpense(expense);
                    });

            db.expenseDao().getAllExpenses().observe(this, expenses -> {
                if (expenses != null) {
                    adapter.updateList(expenses); // Extract the List<Expense> from LiveData
                }
            });

        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    @Override
    public void deleteExpense(Expense expense) {
        Executors.newSingleThreadExecutor().execute(() -> {
            db.expenseDao().deleteExpense(expense);
        });
        db.expenseDao().getAllExpenses().observe(this, expenses -> {
            if (expenses != null) {
                adapter.updateList(expenses); // Extract the List<Expense> from LiveData
            }
        });

    }

    private void updatePieChart(List<Expense> expenses) {
        Map<String, Double> categoryTotals = new HashMap<>();

        for (Expense expense : expenses) {
            double amount = expense.getAmount();
            String category = expense.getCategory();
            categoryTotals.put(category, categoryTotals.getOrDefault(category, 0.0) + amount);
        }

        List<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            entries.add(new PieEntry(entry.getValue().floatValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Expense Categories");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(14f);

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.invalidate(); // Refresh chart
    }
}
