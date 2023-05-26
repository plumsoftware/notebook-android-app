package ru.plumsoftware.notebook.databases;

import android.provider.BaseColumns;

public class DatabaseConstants implements BaseColumns {
    public static final String DATABASE_NAME = "Notes.db";
    public static final String _NOTES_TABLE_NAME = "notes";
    public static final String _GROUPS_TABLE_NAME = "groups";
    public static final String _NOTE_NAME = "_note_name";
    public static final String _GROUP_NAME = "_group_name";
    public static final String _ADD_NOTE_TIME = "_add_time";
    public static final String _ADD_GROUP_TIME = "_add_group_time";
    public static final String _NOTE_TEXT = "_note_text";
    public static final String _NOTE_PROMO = "_note_promo";
    public static final String _NOTE_COLOR = "_note_color";
    public static final String _IS_PINNED = "_is_pinned";
    public static final String _IS_LIKED = "_is_liked";

    public static final String CREATE_NOTES_TABLE =
            "CREATE TABLE IF NOT EXISTS " + DatabaseConstants._NOTES_TABLE_NAME +
                    " (" + DatabaseConstants._ID + " INTEGER PRIMARY KEY," +
                    DatabaseConstants._NOTE_NAME + " TEXT," +
                    DatabaseConstants._ADD_NOTE_TIME + " LONG," +
                    DatabaseConstants._NOTE_TEXT + " TEXT," +
                    DatabaseConstants._NOTE_PROMO + " INTEGER," +
                    DatabaseConstants._IS_PINNED + " INTEGER," +
                    DatabaseConstants._NOTE_COLOR + " INTEGER," +
                    DatabaseConstants._IS_LIKED + " INTEGER)";

    public static final String CREATE_GROUPS_TABLE =
            "CREATE TABLE IF NOT EXISTS " + DatabaseConstants._GROUPS_TABLE_NAME +
                    " (" + DatabaseConstants._ID + " INTEGER PRIMARY KEY," +
                    DatabaseConstants._ADD_NOTE_TIME + " LONG," +
                    DatabaseConstants._ADD_GROUP_TIME + " LONG," +
                    DatabaseConstants._NOTE_COLOR + " INTEGER," +
                    DatabaseConstants._GROUP_NAME + " TEXT)";

    public static final String DELETE_NOTES_TABLE =
            "DROP TABLE IF EXISTS " + DatabaseConstants._NOTES_TABLE_NAME;

    public static final String DELETE_GROUPS_TABLE =
            "DROP TABLE IF EXISTS " + DatabaseConstants._GROUPS_TABLE_NAME;
}
