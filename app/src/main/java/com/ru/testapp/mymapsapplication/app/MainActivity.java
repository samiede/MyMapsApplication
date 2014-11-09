package com.ru.testapp.mymapsapplication.app;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;


/**
 * Created by Sami on 23.10.14.
 *
 */
public class MainActivity extends FragmentActivity implements ValueAnimator.AnimatorUpdateListener {

    LocationReceiver locationReceiver = null;
    protected boolean dontclose = false;
    protected boolean loadedmap = false;
    protected boolean onATrip = false;

    private Fragment fragmentStart = new FragmentStart();
    private FragmentManager fragmentManager = getFragmentManager();
    private FragmentMap mapsFragment = new FragmentMap();
    private FragmentNewTrip fragmentNewTrip;
    private FragmentRecent fragmentRecent = new FragmentRecent();
    private FragmentTripOptions fragmentTripOptions = new FragmentTripOptions();

    LinearLayout doubleFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        doubleFragment = (LinearLayout)findViewById(R.id.main_frame);
        Route.setUpDatabase(this);
        Route.instatiateInnerList();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.map, mapsFragment);
        fragmentTransaction.add(R.id.content_frame, fragmentStart);
        fragmentTransaction.commit();

        locationReceiver = new LocationReceiver();
        locationReceiver.setMainActivityHandler(this);
        IntentFilter filterLocationUpdated = new IntentFilter("com.ru.intent.action.LOCATION");
        registerReceiver(locationReceiver,filterLocationUpdated);
        Log.d("Main Activity", "Receiver created");

    }


    protected void changeFragment(View view) {

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        switch (view.getId()) {
            case R.id.btn_new_trip:

                fragmentNewTrip = new FragmentNewTrip();
                mapsFragment.clearDrawRoutes();
                mapsFragment.removeRoute();
                Intent stopGPS = new Intent(this, LocationService.class);
                stopService(stopGPS);
                fragmentTransaction.setCustomAnimations(R.animator.slide_in_right,
                        R.animator.slide_out_left, R.animator.slide_in_left, R.animator.slide_out_right);
                fragmentTransaction.replace(R.id.content_frame, fragmentNewTrip).addToBackStack(null);
                Log.i("MainActivity", "Adding to backstack");
                fragmentTransaction.commit();
                break;

            case R.id.btn_recent:

                fragmentTransaction.setCustomAnimations(R.animator.slide_in_left,
                        R.animator.slide_out_right, R.animator.slide_in_right, R.animator.slide_out_left);
                fragmentTransaction.hide(mapsFragment);
                fragmentTransaction.add(R.id.map, fragmentRecent).addToBackStack(null);
                fragmentTransaction.commit();
                setWeightSum(2.0f);
                break;

            case R.id.btn_start_trip:

                if(fragmentNewTrip.checkTripName()) {
                   setWeightSum(2.0f);
                   Intent startGPS = new Intent(this, LocationService.class);
                   startService(startGPS);
                   Route.currentName = fragmentNewTrip.getTripname();
                   Route.cont = false;
                   onATrip = true;
                }
                break;

            case R.id.btn_pause_trip:
                Route.saveRouteInDB(true);
                Toast.makeText(this, "Trip paused! Please come again!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_end_trip:
                Route.saveRouteInDB(false);
                Toast.makeText(this, "Route saved, I hope you had a nice trip!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.imageButton:
                setWeightSum(2.0f);
                break;
            case R.id.help:
                showInfo();
                break;
        }
    }

    @Override
    public void onBackPressed(){

        if(doubleFragment.getWeightSum() == 2.0f) {
            setWeightSum(3.0f);
        }
        if (fragmentManager.getBackStackEntryCount() > 0) {

            Log.i("MainActivity", "popping backstack");

            FragmentTripOptions checkTrip = (FragmentTripOptions)fragmentManager.findFragmentByTag("TripOptions");
            if(checkTrip != null) {
                if (checkTrip.isVisible()) {
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.setCustomAnimations(R.animator.slide_in_left,
                            R.animator.slide_out_right, R.animator.slide_in_right, R.animator.slide_out_left);
                    transaction.remove(checkTrip).commit();
                    fragmentManager.popBackStack();
                }
            }
            else {
                fragmentManager.popBackStack();
            }
        }
        else if(!dontclose){
            Log.i("MainActivity", "nothing on backstack, calling super");
            super.onBackPressed();
        }
        dontclose = false;
        onATrip = false;
        if(loadedmap){
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(R.animator.slide_in_left,
                    R.animator.slide_out_right, R.animator.slide_in_right, R.animator.slide_out_left);
            transaction.replace(R.id.content_frame, fragmentStart).commit();
            loadedmap = false;
        }


    }


    public void setWeightSum(float weightSum){
        float ws = doubleFragment.getWeightSum();
        ObjectAnimator animator = ObjectAnimator.ofFloat(doubleFragment, "weightSum", ws, weightSum);
        animator.setDuration(700);
        animator.addUpdateListener(this);
        animator.start();


    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        doubleFragment.requestLayout();
    }

    @Override
    public void onDestroy(){
        Intent stopGPS = new Intent(this, LocationService.class);
        stopService(stopGPS);
        unregisterReceiver(locationReceiver);
        super.onDestroy();
    }


    public void updateMap(){

        mapsFragment.drawNewTrip();

    }



    public void loadTrip(String name){

        mapsFragment.removeRoute();

        final String nameForTask = name;
        loadedmap = true;

        //If trip is not continuable, just draw the trip
        final boolean[] continuableTrip = new boolean[1];

        new AsyncTaskHelper(this){

            @Override
            protected void onFinished() {

                if(!continuableTrip[0]) {
                    setWeightSum(2.0f);
                    mapsFragment.drawTrip();
                    fragmentManager.popBackStack();
                    dontclose = true;
                }
                //Otherwise we draw the trip, start the service and continue tracking
                else{
                    dontclose = true;
                    fragmentManager.popBackStack();
                    mapsFragment.drawTrip();
                    setWeightSum(2.0f);
                    //Intent startGPS = new Intent(callingActivity, LocationService.class);
                    //startService(startGPS);
                }

            }

            @Override
            protected void doTask() {

                Log.d("DOING DATABASE STUFF", "DOING DATABASE STUFF");
                continuableTrip[0] = Route.loadClickedTrip(nameForTask);


            }
        }.execute();


    }


    public void showTripOptions() {

        if(doubleFragment.getWeightSum() == 2.0f) {
            setWeightSum(3.0f);
        }
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.animator.slide_in_right,
                R.animator.slide_out_left, R.animator.slide_in_left, R.animator.slide_out_right);
        if(onATrip) fragmentTransaction.replace(R.id.content_frame, fragmentTripOptions, "TripOptions");
        else fragmentTransaction.replace(R.id.content_frame, fragmentTripOptions, "TripOptions").addToBackStack(null);
        fragmentTransaction.commit();
    }



    public void showInfo(){

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
            alertDialogBuilder.setTitle("Info");
            alertDialogBuilder.setMessage(R.string.infotext);
            alertDialogBuilder.setNeutralButton("Got it!",new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int id) {

                }

            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

        }


}
