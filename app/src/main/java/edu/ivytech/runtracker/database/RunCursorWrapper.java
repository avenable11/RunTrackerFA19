package edu.ivytech.runtracker.database;

import android.database.Cursor;

import android.database.CursorWrapper;
import android.location.Location;

import static edu.ivytech.runtracker.database.RunDBSchema.*;

public class RunCursorWrapper extends CursorWrapper {


        public RunCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        public Location getLocation() {
            Location loc = new Location("GPS");
            double latitude = getDouble(getColumnIndex(LocationTable.Cols.LOCATION_LATITUDE));
            double longitude = getDouble(getColumnIndex(LocationTable.Cols.LOCATION_LONGITUDE));
            long time = getLong(getColumnIndex(LocationTable.Cols.LOCATION_TIME));
            loc.setLatitude(latitude);
            loc.setLongitude(longitude);
            loc.setTime(time);

            return loc;
        }

}
