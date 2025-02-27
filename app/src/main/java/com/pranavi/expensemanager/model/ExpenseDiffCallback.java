package com.pranavi.expensemanager.model;

import androidx.recyclerview.widget.DiffUtil;
import java.util.List;

public class ExpenseDiffCallback extends DiffUtil.Callback {
    private final List<Expense> oldList;
    private final List<Expense> newList;

    public ExpenseDiffCallback(List<Expense> oldList, List<Expense> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getId() == newList.get(newItemPosition).getId(); // Ensure ID uniqueness
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).equals(newList.get(newItemPosition)); // Use model's equals() method
    }
}
