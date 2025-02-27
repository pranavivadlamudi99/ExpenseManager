package com.pranavi.expensemanager.model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.pranavi.expensemanager.database.ExpenseDatabase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExpenseViewModel extends AndroidViewModel {
    private final ExpenseDatabase db;
    private final LiveData<List<Expense>> allExpenses;

    public ExpenseViewModel(@NonNull Application application) {
        super(application);
        db = ExpenseDatabase.getInstance(application);
        allExpenses = db.expenseDao().getAllExpenses();
    }

    public LiveData<List<Expense>> getAllExpenses() {
        return allExpenses;
    }

    public void insertExpense(Expense expense) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> db.expenseDao().insertExpense(expense));
    }
}
