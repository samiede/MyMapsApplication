package com.ru.testapp.mymapsapplication.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Sami on 06.11.14.
 *
 */
public class LocationReceiver extends BroadcastReceiver {

    public double latitude, longitude;
    MainActivity main = null;

    public void setMainActivityHandler(MainActivity main){
        this.main = main;
    }

    @Override
    public void onReceive(final Context context, final Intent calledIntent)
    {

        latitude = calledIntent.getDoubleExtra("latitude", -1);
        longitude = calledIntent.getDoubleExtra("longitude", -1);

        Log.i("LOC_REVIEVER", "Lat:" + latitude + " Long:" + longitude);

        Route.addLocation(new LatLng(latitude, longitude));

        main.updateMap();


    }


}