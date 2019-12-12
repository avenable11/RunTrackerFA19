package edu.ivytech.runtracker.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import static edu.ivytech.runtracker.database.RunDBSchema.*;

public class RunBaseHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "runtracker.db";
    public static final int DB_VERSION = 1;

    public RunBaseHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + LocationTable.NAME + "(" +
                LocationTable.Cols.LOCATION_ID + " integer primary key autoincrement, " +
                LocationTable.Cols.LOCATION_LATITUDE + "," +
                LocationTable.Cols.LOCATION_LONGITUDE + ", " +
                LocationTable.Cols.LOCATION_TIME + ");");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
