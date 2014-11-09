package com.ru.testapp.mymapsapplication.app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Sami on 07.11.14.
 *
 */
public class LocationDatabase extends SQLiteOpenHelper {

    public static String DBNAME = "routesdb";
    private static int DB_VERSION = 1;
    private static final String COLUMN_ID = "_id";
    public static final String LAT = "latitude";
    public static final String LONGI = "longitude";
    public static final String CONTINUABLE = "continuable";
    public static final String TRIPNAME = "name";


    private static final String DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS "
            + DBNAME + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + TRIPNAME
            + " text not null, " + LAT
            + " text not null, " + LONGI
            + " text not null, " + CONTINUABLE
            + " text not null);";

    public LocationDatabase(Context context) {

        super(context, DBNAME, null, DB_VERSION);

    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(LocationDatabase.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + DBNAME);
        onCreate(db);
    }
}
