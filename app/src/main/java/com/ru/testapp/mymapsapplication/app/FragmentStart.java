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
public class FragmentStart extends Fragment {

    Button btnNewTrip;
    Button btnRecent;
    ImageButton btnHelp;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_start, container, false);

        onClickHandler handler = new onClickHandler();

        btnNewTrip = (Button) view.findViewById(R.id.btn_new_trip);
        btnRecent = (Button) view.findViewById(R.id.btn_recent);
        btnHelp = (ImageButton) view.findViewById(R.id.help);
        btnNewTrip.setOnClickListener(handler);
        btnRecent.setOnClickListener(handler);
        btnHelp.setOnClickListener(handler);



        return view;
    }



    public class onClickHandler implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            ((MainActivity)getActivity()).changeFragment(v);
        }
    }


}

