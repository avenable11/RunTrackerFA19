package edu.ivytech.runtracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;

import java.util.ArrayList;

import static edu.ivytech.runtracker.database.RunDBSchema.*;

public class RunTrackerDB {
    private SQLiteDatabase mDatabase;
    private static RunTrackerDB runTrackerDB;
    private Context mContext;

    public static RunTrackerDB get(Context context) {
        if (runTrackerDB == null) {
            runTrackerDB = new RunTrackerDB(context);
        }
        return runTrackerDB;
    }

    private RunTrackerDB(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new RunBaseHelper(mContext).getWritableDatabase();
    }

    public void insertLocation(Location location) {
        ContentValues cv = getContentValues(location);
        mDatabase.insert(LocationTable.NAME,null, cv);
    }

    public ArrayList<Location> getLocations() {
        ArrayList<Location> list = new ArrayList<>();
        RunCursorWrapper cursor = queryLocations(null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                list.add(cursor.getLocation());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return list;
    }

    public void deleteLocations() {
        mDatabase.delete(LocationTable.NAME,null,null);
    }

    private RunCursorWrapper queryLocations(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(LocationTable.NAME,
                null,whereClause,whereArgs,null, null, null);
        return new RunCursorWrapper(cursor);
    }

    private static ContentValues getContentValues(Location location) {
        ContentValues cv = new ContentValues();
        cv.put(LocationTable.Cols.LOCATION_LATITUDE, location.getLatitude());
        cv.put(LocationTable.Cols.LOCATION_LONGITUDE, location.getLongitude());
        cv.put(LocationTable.Cols.LOCATION_TIME, location.getTime());
        return cv;
    }
}
