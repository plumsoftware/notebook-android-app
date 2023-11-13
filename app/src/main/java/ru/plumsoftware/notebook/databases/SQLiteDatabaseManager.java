package ru.plumsoftware.notebook.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteDatabaseManager extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 6;
    public static final String DATABASE_NAME = DatabaseConstants.DATABASE_NAME;

    public SQLiteDatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseConstants.CREATE_NOTES_TABLE);
        db.execSQL(DatabaseConstants.CREATE_GROUPS_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL(DatabaseConstants.DELETE_NOTES_TABLE);
//        db.execSQL(DatabaseConstants.DELETE_GROUPS_TABLE);
//        onCreate(db);
        // Добавьте новые столбцы в таблицу notes
        if (DATABASE_VERSION > 4) {
            db.execSQL("ALTER TABLE " + DatabaseConstants._NOTES_TABLE_NAME +
                    " ADD COLUMN " + DatabaseConstants._CHANNEL_ID + " TEXT");
            db.execSQL("ALTER TABLE " + DatabaseConstants._NOTES_TABLE_NAME +
                    " ADD COLUMN " + DatabaseConstants._IS_NOTIFY + " INTEGER");
        }
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
