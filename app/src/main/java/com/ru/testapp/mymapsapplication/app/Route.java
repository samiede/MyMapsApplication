package com.ru.testapp.mymapsapplication.app;

import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.util.Log;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sami on 06.11.14.
 *
 */
public class Route {

    public static LocationDatabase dbHelper;


    public static List<ArrayList<LatLng>>loadRoute = new ArrayList<ArrayList<LatLng>>();
    public static boolean cont = false;
    public static final double MIN_DISTANCE_TO_BE_ONE_TRIP = 0.005;

    public static String currentName = null;

    public static void instatiateInnerList(){

        loadRoute.add(new ArrayList<LatLng>());

    }


    public static void addLocation(LatLng location){

        loadRoute.get(loadRoute.size()-1).add(location);


    }


    public static List<ArrayList<LatLng>> returnLoadedLocationList() {

        return loadRoute;

    }

    public static List<LatLng> returnCurrentRouteList(){

        return loadRoute.get(loadRoute.size()-1);

    }


    public static void setUpDatabase(Context context){

        Log.d("ROUTE", "SET UP DATABASE");
        dbHelper = new LocationDatabase(context);

    }



    public static void saveRouteInDB(boolean continuable){

        String continueToInsert;
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        if(continuable) continueToInsert = "true";
        else continueToInsert = "false";

        for(List<LatLng> partRoute : loadRoute) {
            for (LatLng position : partRoute) {

                ContentValues positionValues = new ContentValues();
                positionValues.put("name", currentName);
                positionValues.put("latitude", position.latitude);
                positionValues.put("longitude", position.longitude);
                positionValues.put("continuable", continueToInsert);

                db.insert(LocationDatabase.DBNAME, null, positionValues);

            }
        }

        Log.d("ROUTE", "SAVED ROUTE IN DB");

    }



    public static int checkIfNameIsInDatabaseAlready(String name){

        int numberOfTimesItsInThereAlreadyYouUncreativeBastard;

        String[] projection = {
                "name"
        };
        String[] selectionArgs = {
                name
        };


        String selection = "name = ?";

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor result = db.query(
                LocationDatabase.DBNAME,    //The table to query
                projection,                 //the colums to return
                selection,                  //the colums for WHERE clause
                selectionArgs,              //the values for WHERE clause
                null,
                null,
                null,
                null
                );

        numberOfTimesItsInThereAlreadyYouUncreativeBastard = result.getCount();

        return numberOfTimesItsInThereAlreadyYouUncreativeBastard;
    }



    public static Cursor getDestinctTripNamesAndContinuable(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();


        return db.rawQuery("SELECT DISTINCT _id, name, continuable FROM " + LocationDatabase.DBNAME + " GROUP BY name", null);
    }


    public static void clearRoute(){

        currentName = null;
        loadRoute.clear();
        instatiateInnerList();

    }


    public static boolean loadClickedTrip(String name) {

        currentName = name;
        loadRoute.clear();
        loadRoute.add(new ArrayList<LatLng>());
        boolean returnValue = false;

        LatLng nextLatLng;
        LatLng currentLatLng;
        int listNumber = 0;




        String[] projection = {
                "latitude", "longitude", "continuable"
        };
        String[] selectionArgs = {
                name
        };


        String selection = "name = ?";

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor result = db.query(
                LocationDatabase.DBNAME,    //The table to query
                projection,                 //the colums to return
                selection,                  //the colums for WHERE clause
                selectionArgs,              //the values for WHERE clause
                null,
                null,
                null,
                null
        );

        result.moveToFirst();

        boolean continueTrip = result.getString(result.getColumnIndex("continuable")).equals("true");

        if (continueTrip) {
            cont = true;
            returnValue = true;
        }


        result.moveToPosition(0);

        currentLatLng = new LatLng(Double.parseDouble(result.getString(result.getColumnIndex("latitude"))),
                Double.parseDouble(result.getString(result.getColumnIndex("longitude"))));



        while (result.moveToNext()) {

            loadRoute.get(listNumber).add(currentLatLng);


            nextLatLng = new LatLng(Double.parseDouble(result.getString(result.getColumnIndex("latitude"))),
                    Double.parseDouble(result.getString(result.getColumnIndex("longitude"))));

            boolean addthis = result.moveToNext();

            if(addthis) {
                if (Math.abs(currentLatLng.latitude - nextLatLng.latitude) > MIN_DISTANCE_TO_BE_ONE_TRIP
                        || Math.abs(currentLatLng.longitude - nextLatLng.longitude) > MIN_DISTANCE_TO_BE_ONE_TRIP) {
                    listNumber++;
                    loadRoute.add(new ArrayList<LatLng>());

                }
            }

            loadRoute.get(listNumber).add(nextLatLng);
            currentLatLng = nextLatLng;

        }

        result.close();
        return returnValue;


    }



    public static void updateRouteInDB(boolean continuable) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String continueToInsert;


        //delete old route, save new route
        // Define 'where' part of query.
        String selection = "name = ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { currentName };
        // Issue SQL statement.
        db.delete(LocationDatabase.DBNAME, selection, selectionArgs);

        if(continuable) continueToInsert = "true";
        else continueToInsert = "false";
        //save new route here
        for (List<LatLng> joinedList : loadRoute) {

            for (LatLng position : joinedList) {

                ContentValues positionValues = new ContentValues();
                positionValues.put("name", currentName);
                positionValues.put("latitude", position.latitude);
                positionValues.put("longitude", position.longitude);
                positionValues.put("continuable", continueToInsert);

                db.insert(LocationDatabase.DBNAME, null, positionValues);

            }
        }

        Log.d("ROUTE", "UPDATED ROUTE IN DB");

    }
}
