package com.pranavi.expensemanager.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "expenses")
public class Expense {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private double amount;
    private String category;
//    @NonNull
    private String date;

    private String expenseName;


    public Expense(double amount, String category, String expenseName, String date) {
        this.amount = amount;
        this.category = category;
        this.expenseName = expenseName;
        this.date = date;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }


    public String getExpenseName() {
        return expenseName;
    }

    public void setExpenseName(String expenseName) {
        this.expenseName = expenseName;
    }


    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}
