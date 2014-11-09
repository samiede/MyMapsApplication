package com.ru.testapp.mymapsapplication.app;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Sami on 03.11.14.
 *
 */
public class FragmentNewTrip extends Fragment {

    protected Button startTrip;
    public EditText tripName;
    protected onClickHandler clickListener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_newtrip, container, false);

        //instatiate
        startTrip = (Button) view.findViewById(R.id.btn_start_trip);
        tripName = (EditText) view.findViewById(R.id.trip_title);

        //initiate
        clickListener = new onClickHandler();
        startTrip.setOnClickListener(clickListener);
        tripName.setText("");


        return view;
    }


    public class onClickHandler implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            ((MainActivity)getActivity()).changeFragment(v);
        }
    }

    public boolean checkTripName(){

        if(tripName.getText().toString().equals("")){
            tripName.setError("Please enter a name!");
            return false;
        }
        else{
            int number = Route.checkIfNameIsInDatabaseAlready(tripName.getText().toString());
            if (number > 0) {
                Toast.makeText(getActivity(), "Name already chosen before!", Toast.LENGTH_SHORT).show();
                return false;
            }
            else return true;
        }

    }

    public String getTripname(){

        return tripName.getText().toString().trim();
    }

}
