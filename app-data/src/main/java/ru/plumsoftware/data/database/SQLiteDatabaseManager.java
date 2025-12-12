package ru.plumsoftware.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ru.plumsoftware.data.model.database.DatabaseConstants;

public class SQLiteDatabaseManager extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 5;
    private static final String DATABASE_NAME = DatabaseConstants.DATABASE_NAME;

    public SQLiteDatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseConstants.CREATE_NOTES_TABLE);
        db.execSQL(DatabaseConstants.CREATE_GROUPS_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            String tempTable = "temp_table";
            db.execSQL("CREATE TABLE " + tempTable + " AS SELECT * FROM " + DatabaseConstants._NOTES_TABLE_NAME);
            db.execSQL(DatabaseConstants.DELETE_NOTES_TABLE);
            db.execSQL(DatabaseConstants.CREATE_NOTES_TABLE);

            db.execSQL("ALTER TABLE " + tempTable + " ADD " + DatabaseConstants._CHANNEL_ID + " TEXT;");
            db.execSQL("ALTER TABLE " + tempTable + " ADD " + DatabaseConstants._IS_NOTIFY + " INTEGER;");

            db.execSQL("INSERT INTO " + DatabaseConstants._NOTES_TABLE_NAME + " SELECT * FROM " + tempTable);

            db.execSQL("DROP TABLE IF EXISTS " + tempTable);
        }
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
