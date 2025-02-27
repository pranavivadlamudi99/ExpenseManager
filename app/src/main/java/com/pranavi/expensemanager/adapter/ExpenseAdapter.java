package com.pranavi.expensemanager.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.pranavi.expensemanager.R;
import com.pranavi.expensemanager.model.Expense;
import com.pranavi.expensemanager.model.ExpenseDiffCallback;

import java.util.ArrayList;
import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {
    private List<Expense> expenseList;
    private Context context;
    private ExpenseActionsListener listener;

    public ExpenseAdapter() { // Empty constructor for initialization
        this.expenseList = new ArrayList<>();
    }

    public ExpenseAdapter(Context context, ExpenseActionsListener listener) {
        this.context = context;
        this.listener = listener;
        this.expenseList = new ArrayList<>();
    }

    public void setExpenseList(List<Expense> expenses) {
        this.expenseList = expenses;
        notifyDataSetChanged(); // Initial load
    }

    public void updateList(List<Expense> newList) {
        if (newList == null) return; // Ensure no null values

        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new ExpenseDiffCallback(expenseList, newList));
        expenseList.clear();
        expenseList.addAll(newList);
        diffResult.dispatchUpdatesTo(this); // Notify adapter of precise changes
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = expenseList.get(position);
        holder.textExpenseName.setText(expense.getExpenseName());
        holder.textAmount.setText("₹" + expense.getAmount());
        holder.textCategory.setText(expense.getCategory());
        holder.textDate.setText(expense.getDate());
        Log.d("Date", "Binding expense date: " + expense.getDate());

        // Implement Options Menu
        holder.imgOptions.setOnClickListener(view -> {
            PopupMenu popup = new PopupMenu(context, holder.imgOptions);
            popup.inflate(R.menu.expense_menu);
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.edit_expense) {
                    listener.editExpense(expense);
                    return true;
                } else if (item.getItemId() == R.id.delete_expense) {
                    listener.deleteExpense(expense);
                    return true;
                }
                return false;
            });
            popup.show();
        });

    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView textExpenseName, textAmount, textCategory, textDate;
        ImageView iconCategory, imgOptions;

        ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            textExpenseName = itemView.findViewById(R.id.textExpenseName);
            textAmount = itemView.findViewById(R.id.textAmount);
            textCategory = itemView.findViewById(R.id.textCategory);
            textDate = itemView.findViewById(R.id.textDate);
            iconCategory = itemView.findViewById(R.id.iconCategory);
            imgOptions = itemView.findViewById(R.id.imgOptions);
        }
    }

    // Interface for handling Edit & Delete actions
    public interface ExpenseActionsListener {
        void editExpense(Expense expense);
        void deleteExpense(Expense expense);
    }
}
