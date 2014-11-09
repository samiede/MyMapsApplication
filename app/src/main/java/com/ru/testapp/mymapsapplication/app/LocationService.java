package com.ru.testapp.mymapsapplication.app;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Sami on 06.11.14.
 *
 */
public class LocationService extends Service {

    private static final int TWO_MINUTES = 1000 * 60 * 2;
    private static final int TEN_SECONDS = 1000 * 10;
    private static final int MIN_DISTANCE_TO_UPDATE_IN_METERS = 0;
    private static final double MIN_LOCATION_DIFFERENCE_TO_GET_NEW_LOCATION = 0.00001;
    private static final double MAX_LOCATION_DIFFERENCE_TO_GET_NEW_LCATION = 10;
    public LocationManager locationManager;
    public MyLocationListener listener;
    public Location previousBestLocation = null;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = new MyLocationListener();

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, TEN_SECONDS, MIN_DISTANCE_TO_UPDATE_IN_METERS, listener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, TEN_SECONDS, MIN_DISTANCE_TO_UPDATE_IN_METERS, listener);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }


    //Are the two locations sufficiently far apart to draw a new line?
    protected boolean isSufficientlyFarApart(Location location, Location currentBestLocation){
        //if we don't have a location, then this is "far apart" from the last one, indefinitely far
        if (currentBestLocation != null) {
            double deltaLat = Math.abs(((location.getLatitude() - currentBestLocation.getLatitude())));
            double deltaLong = Math.abs((location.getLongitude() - currentBestLocation.getLongitude()));
            return !((deltaLat < MIN_LOCATION_DIFFERENCE_TO_GET_NEW_LOCATION) || deltaLong < MIN_LOCATION_DIFFERENCE_TO_GET_NEW_LOCATION);
        } else {
            return true;
        }
    }

    //Are the locations close enough to be sure it's not just a glitch in positioning?
    protected boolean isNotTooFarApart(Location location, Location currentBestLocation){
        //if we don't have a location, then this is "far apart" from the last one, indefinitely far
        if (currentBestLocation != null) {
            double deltaLat = Math.abs(((location.getLatitude() - currentBestLocation.getLatitude())));
            double deltaLong = Math.abs((location.getLongitude() - currentBestLocation.getLongitude()));
            return !((deltaLat > MAX_LOCATION_DIFFERENCE_TO_GET_NEW_LCATION) || deltaLong > MAX_LOCATION_DIFFERENCE_TO_GET_NEW_LCATION);
        } else {
            return true;
        }
    }


    //Timing issues
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }


    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }



    @Override
    public void onDestroy() {
        // handler.removeCallbacks(sendUpdatesToUI);
        Log.v("STOP_SERVICE", "DONE");
        locationManager.removeUpdates(listener);
        if(!Route.cont) Route.saveRouteInDB(true);
        else Route.updateRouteInDB(true);
        super.onDestroy();

    }




    public class MyLocationListener implements LocationListener
    {
        double latitude, longitude;


        public void onLocationChanged(final Location loc) {

            Log.i("LOC_SERVICE", "Location changed");
            Log.i("LOC_SERVICE", "Accuracy: " + loc.getAccuracy());
                if (isBetterLocation(loc, previousBestLocation)) {
                    if(isNotTooFarApart(loc, previousBestLocation)) {
                        if (isSufficientlyFarApart(loc, previousBestLocation)) {

                            previousBestLocation = loc;
                            latitude = loc.getLatitude();
                            longitude = loc.getLongitude();

                            Intent filterRes = new Intent();
                            filterRes.setAction("com.ru.intent.action.LOCATION");
                            filterRes.putExtra("latitude", latitude);
                            filterRes.putExtra("longitude", longitude);
                            getApplicationContext().sendBroadcast(filterRes);

                        }
                    }
            }
}




        public void onProviderDisabled(String provider)
        {
            Toast.makeText(getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT).show();
        }


        public void onProviderEnabled(String provider)
        {
            Toast.makeText(getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
        }


        public void onStatusChanged(String provider, int status, Bundle extras)
        {

        }

    }
}