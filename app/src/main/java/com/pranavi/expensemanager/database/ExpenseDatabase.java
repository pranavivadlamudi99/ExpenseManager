package com.pranavi.expensemanager.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.pranavi.expensemanager.database.ExpenseDao;
import com.pranavi.expensemanager.model.Expense;

@Database(entities = {Expense.class}, version = 2, exportSchema = false)
public abstract class ExpenseDatabase extends RoomDatabase {
    private static volatile ExpenseDatabase instance;

    public abstract ExpenseDao expenseDao();

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Add the new column 'date' to the existing table
            database.execSQL("ALTER TABLE expenses ADD COLUMN date TEXT NOT NULL DEFAULT ''");
        }
    };

    public static synchronized ExpenseDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (ExpenseDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                                    ExpenseDatabase.class, "expense_database")
                            .addMigrations(MIGRATION_1_2)
                            .build();
                }
            }
        }
        return instance;
    }
}
