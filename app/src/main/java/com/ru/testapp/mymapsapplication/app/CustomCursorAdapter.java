package com.ru.testapp.mymapsapplication.app;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

/**
 * Created by Sami on 07.11.14.
 *
 */
public class CustomCursorAdapter extends CursorAdapter {

    private LayoutInflater mInflater;
    TextView mTripName;
    ImageView mContinuable;
    ImageView mHeader;
    int[] pictures;



    public CustomCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);

        pictures = new int[c.getCount()];

        for(int i = 0; i < c.getCount(); i++){
            pictures[i] = randInt(0,5);
        }
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return mInflater.inflate(R.layout.trip_list_row, parent, false);

    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        mContinuable = (ImageView) view.findViewById(R.id.continuable);
        mTripName = (TextView) view.findViewById(R.id.textViewTripName);
        mHeader = (ImageView) view.findViewById(R.id.header);

        mTripName.setText(cursor.getString(cursor.getColumnIndex("name")));

        String cont = cursor.getString(cursor.getColumnIndex("continuable"));


        if(pictures[cursor.getPosition()] == 0 || pictures[cursor.getPosition()] == 1 ){
            mHeader.setImageResource(R.drawable.header_1);
        }
        else if(pictures[cursor.getPosition()] == 2 || pictures[cursor.getPosition()] == 3 ){
            mHeader.setImageResource(R.drawable.header_2);
        }
        else{
            mHeader.setImageResource(R.drawable.header_3);
        }

        if(cont.equals("true")){

            mContinuable.setImageResource(android.R.drawable.ic_media_pause);
        }
        else{
            mContinuable.setImageResource(android.R.drawable.ic_lock_idle_lock);

        }


    }

    public static int randInt(int min, int max) {

        Random rand = new Random();

        return rand.nextInt((max - min) + 1) + min;
    }
}
