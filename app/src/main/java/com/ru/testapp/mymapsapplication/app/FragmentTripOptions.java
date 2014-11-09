package com.ru.testapp.mymapsapplication.app;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import android.widget.ImageButton;

/**
 * Created by Sami on 01.11.14.
 *
 */
public class FragmentTripOptions extends Fragment {

    Button btnPauseTrip;
    Button btnEndTrip;
    ImageButton btnContinueTrip;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trip_options, container, false);

        onClickHandler handler = new onClickHandler();

        btnPauseTrip = (Button) view.findViewById(R.id.btn_pause_trip);
        btnEndTrip = (Button) view.findViewById(R.id.btn_end_trip);
        btnContinueTrip = (ImageButton) view.findViewById(R.id.imageButton);
        btnContinueTrip.setOnClickListener(handler);
        btnPauseTrip.setOnClickListener(handler);
        btnEndTrip.setOnClickListener(handler);


        return view;
    }



    public class onClickHandler implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            ((MainActivity)getActivity()).changeFragment(v);
        }
    }


}

