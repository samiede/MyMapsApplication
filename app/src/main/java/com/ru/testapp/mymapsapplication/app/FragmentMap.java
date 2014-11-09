package com.ru.testapp.mymapsapplication.app;

import android.app.Activity;
import android.app.Fragment;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sami on 23.10.14
 *
 */
public class FragmentMap extends Fragment implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        LocationListener {


    private MainActivity myContext;
    protected List<Polyline> routeList = new ArrayList<Polyline>();

    private boolean keepZoom = false;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    public LocationClient mLocationClient;
    public LocationRequest mLocationRequest;
    public LocationListener locationListener;

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    //Variables
    private static final int MILLISECONDS_PER_SECOND = 1000;
    // Update frequency in seconds
    public static final int UPDATE_INTERVAL_IN_SECONDS = 2;
    // Update frequency in milliseconds
    private static final long UPDATE_INTERVAL =
            MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    // The fastest update frequency, in seconds
    private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
    // A fast frequency ceiling in milliseconds
    private static final long FASTEST_INTERVAL =
            MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;

    //Override on Fragments
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        locationListener = this;
        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create();
        // Use high accuracy
        mLocationRequest.setPriority(
                LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Set the update interval to 5 seconds
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        // Set the fastest update interval to 1 second
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        setUpMapIfNeeded();

        if(mMap!=null) {

            mMap.setBuildingsEnabled(true);
            mMap.setMyLocationEnabled(true);

            mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    myContext.showTripOptions();
                }
            });

            mLocationClient = new LocationClient(getActivity(), this, this);

            // Connect the client.
            if(isGooglePlayServicesAvailable()){
                if(mLocationClient.isConnected()) mLocationClient.disconnect();
                mLocationClient.connect();
            }

        }
        return view;
    }


    @Override
    public void onAttach(Activity activity) {
        myContext = (MainActivity) activity;
        super.onAttach(activity);
    }


    @Override
    public void onPause(){
        mLocationClient.disconnect();
        super.onPause();
    }

    @Override
    public void onResume(){
        // Reconnect the client.
        if(isGooglePlayServicesAvailable()){
            if(mLocationClient.isConnected()) mLocationClient.disconnect();
            mLocationClient.connect();

        }
        super.onResume();
    }

    @Override
    public void onStop(){
        mLocationClient.disconnect();
        super.onStop();
    }

    @Override
    public void onDestroy(){
        mLocationClient.disconnect();
        super.onDestroy();
    }


    //Override on GooglePlayServicesClient
    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p>
     * If it isn't installed {@link com.google.android.gms.maps.SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) myContext.getSupportFragmentManager().findFragmentById(R.id.map_fragment_layout))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }


    private void setUpMap() {
    }


    private boolean isGooglePlayServicesAvailable() {
        // Check that Google Play services is available
        int resultCode =  GooglePlayServicesUtil.isGooglePlayServicesAvailable(myContext);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates", "Google Play services is available.");
            return true;
        } else {

            return false;
        }
    }



    @Override
    public void onConnected(Bundle bundle) {
        //Specify update rate here!
        mLocationClient.requestLocationUpdates(mLocationRequest, locationListener);

        Location location = mLocationClient.getLastLocation();

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
        mMap.animateCamera(cameraUpdate);
        keepZoom = true;

    }

    @Override
    public void onDisconnected() {
        // Display the connection status
        Toast.makeText(myContext, "Disconnected. Please reconnect.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    /*
     * Google Play services can resolve some errors it detects.
     * If the error has a resolution, try sending an Intent to
     * start a Google Play services activity that can resolve
     * error.
     */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        myContext,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
            /*
            * Thrown if Google Play services canceled the original
            * PendingIntent
            */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {

            Toast.makeText(myContext.getApplicationContext(), "Sorry. Location services are not available to you right now", Toast.LENGTH_LONG).show();
        }
    }



    //Override on LocationListener

    @Override
    public void onLocationChanged(Location location) {

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        if(keepZoom){
                CameraPosition position = CameraPosition.builder()
                        .bearing(location.getBearing())
                        .target(latLng)
                        .zoom(mMap.getCameraPosition().zoom)
                        .tilt(mMap.getCameraPosition().tilt)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
        }


    }



    //Custom Methods

    public void removeRoute(){

        if(routeList != null) {
            for (Polyline line : routeList) {
                line.remove();
            }
            routeList.clear();
        }
    }


    public void clearDrawRoutes(){
        Route.clearRoute();

    }


    public void drawTrip() {

        List<ArrayList<LatLng>> wholeList = Route.returnLoadedLocationList();

        for(ArrayList<LatLng> parttrip : wholeList){

            Polyline route  = mMap.addPolyline(new PolylineOptions()
                    .width(10)
                    .color(Color.BLUE)
                    .geodesic(true)
                    .zIndex(1));
            route.setPoints(parttrip);


            routeList.add(route);
        }
/*
        CameraPosition position = CameraPosition.builder()
                .target(wholeList.get(0).get(0))
                .zoom(15)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));*/


        Log.d("MAP", "DREW ROUTE");


    }


    public void drawNewTrip(){

        List<LatLng> drawThis = Route.returnCurrentRouteList();


            Polyline route  = mMap.addPolyline(new PolylineOptions()
                    .width(10)
                    .color(Color.BLUE)
                    .geodesic(true)
                    .zIndex(1));
            route.setPoints(drawThis);


    }





}
