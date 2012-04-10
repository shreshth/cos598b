package com.cos598b;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "droidtn";

    // Contacts table name
    private static final String TABLE_POINTS = "points";

    // Contacts Table Columns names
    public static final String KEY_ID = "id";
    public static final String KEY_LAT = "lat";
    public static final String KEY_LNG = "lng";
    public static final String KEY_BEARING = "bearing";
    public static final String KEY_TIMESTAMP = "timestamp";
    public static final String KEY_TIME_TILL_WIFI = "time_till_wifi";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_POINTS_TABLE = "CREATE TABLE " + TABLE_POINTS + "("
                        + KEY_ID + " INTEGER PRIMARY KEY," + KEY_LAT + " REAL,"
                        + KEY_LNG + " REAL," + KEY_BEARING + " REAL,"
                        + KEY_TIMESTAMP + " INTEGER,"
                        + KEY_TIME_TILL_WIFI + " INTEGER" + ")";
        db.execSQL(CREATE_POINTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POINTS);

        // Create tables again
        onCreate(db);
    }

    // Adding new data point
    private void addPoint(DataPoint point) {
        if (point.isValid()) {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(KEY_LAT, point.getLat());
            values.put(KEY_LNG, point.getLng());
            values.put(KEY_BEARING, point.getBearing());
            values.put(KEY_TIMESTAMP, point.getTimestamp());
            values.put(KEY_TIME_TILL_WIFI, point.getTimeTillWifi());

            // Inserting Row
            db.insert(TABLE_POINTS, null, values);
            db.close(); // Closing database connection
        }
    }

    // Get the number of rows in the database
    private int getNumRows() {
        String sql = "SELECT COUNT(*) FROM " + TABLE_POINTS;
        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteStatement statement = db.compileStatement(sql);
        int count = (int) statement.simpleQueryForLong();
        db.close(); // Closing database connection
        return count;
    }

    // Retrieve a few data points and remove them from the database
    // Returns a comma separated string of fields
    private Map<String, String> popFew() {
        Map<String, String> data = new HashMap<String, String>();
        List<String> latList = new ArrayList<String>();
        List<String> lngList = new ArrayList<String>();
        List<String> bearingList = new ArrayList<String>();
        List<String> timestampList = new ArrayList<String>();
        List<String> timetillwifiList = new ArrayList<String>();

        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_POINTS + " ORDER BY " + KEY_TIMESTAMP + " ASC LIMIT " + Consts.http_batch_limit;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        long greatestTimeStamp = 0;
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                long timestamp = Long.parseLong(cursor.getString(4));
                if (timestamp > greatestTimeStamp) {
                    greatestTimeStamp = timestamp;
                }
                latList.add(cursor.getString(1));
                lngList.add(cursor.getString(2));
                bearingList.add(cursor.getString(3));
                timestampList.add(cursor.getString(4));
                timetillwifiList.add(cursor.getString(5));
            } while (cursor.moveToNext());
        }
        cursor.close();

        // Delete retrieved points
        db.delete(TABLE_POINTS, KEY_TIMESTAMP + " <= ?", new String[] {Long.toString(greatestTimeStamp)});
        db.close(); // Closing database connection

        data.put(KEY_LAT, Utils.implode(latList.toArray(new String[0]), ","));
        data.put(KEY_LNG, Utils.implode(lngList.toArray(new String[0]), ","));
        data.put(KEY_BEARING, Utils.implode(bearingList.toArray(new String[0]), ","));
        data.put(KEY_TIMESTAMP, Utils.implode(timestampList.toArray(new String[0]), ","));
        data.put(KEY_TIME_TILL_WIFI, Utils.implode(timetillwifiList.toArray(new String[0]), ","));

        return data;
    }

    // --------------- Synchronized access to whole class ----------------------------
    public synchronized static void addPoint(Context context, DataPoint point) {
        DatabaseHelper db = new DatabaseHelper(context);
        db.addPoint(point);
    }

    public synchronized static Map<String, String> popFew(Context context) {
        DatabaseHelper db = new DatabaseHelper(context);
        return db.popFew();
    }

    public synchronized static int getNumRows(Context context) {
        DatabaseHelper db = new DatabaseHelper(context);
        return db.getNumRows();
    }
}
