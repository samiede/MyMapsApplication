package com.ru.testapp.mymapsapplication.app;

import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Created by Sami on 07.11.14.
 *
 */
public class FragmentRecent extends Fragment {

    MainActivity main;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recent, container, false);
        main = (MainActivity) getActivity();

        final ListView tripBook = (ListView) view.findViewById(R.id.listViewTrips);

        Cursor routeCursor = Route.getDestinctTripNamesAndContinuable();
        CustomCursorAdapter cursorAdapter = new CustomCursorAdapter(getActivity(), routeCursor, 0);

        tripBook.setAdapter(cursorAdapter);

        tripBook.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Cursor value = (Cursor) tripBook.getItemAtPosition(position);
                String tripToLoad = value.getString(value.getColumnIndex("name"));
                main.loadTrip(tripToLoad);


            }
        });

        return view;
    }




}