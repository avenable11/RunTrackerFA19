package edu.ivytech.runtracker.database;

public class RunDBSchema {
    public static final class LocationTable {
        public static final String NAME = "location";
        public static final class Cols {
            public static final String LOCATION_ID = "_id";
            public static final String LOCATION_LATITUDE = "latitude";
            public static final String LOCATION_LONGITUDE = "longitude";
            public static final String LOCATION_TIME = "time";
        }
    }
}
