package com.example.user.mobilemicroscopy;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.user.mobilemicroscopy.database.ImageContract.ImageEntry;

import org.w3c.dom.Text;

public class ImageCursorAdapter extends CursorAdapter {

    /**
     * Constructor
     */
    public ImageCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    /**
     * Create a new blank list item view
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * Bind data with list item view
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // find views on list item
        TextView dateTextView = (TextView) view.findViewById(R.id.date);
        TextView timeTextView = (TextView) view.findViewById(R.id.time);
        TextView specimenTypeTextView = (TextView) view.findViewById(R.id.speciment_type);
        TextView gpsPositionTextView = (TextView) view.findViewById(R.id.gps_location);

        // find the column indexes in cursor
        int dateColumnIndex = cursor.getColumnIndex(ImageEntry.COLUMN_NAME_DATE);
        int timeColumnIndex = cursor.getColumnIndex(ImageEntry.COLUMN_NAME_TIME);
        int specimenTypeColumnIndex = cursor.getColumnIndex(ImageEntry.COLUMN_NAME_SPECIMEN_TYPE);
        int gpsPositionColumnIndex = cursor.getColumnIndex(ImageEntry.COLUMN_NAME_GPS_POSITION);

        // extract the values
        String imageDate = cursor.getString(dateColumnIndex);
        String imageTime = cursor.getString(timeColumnIndex);
        String imageSpecimenType = cursor.getString(specimenTypeColumnIndex);
        String imageGPSPosition = cursor.getString(gpsPositionColumnIndex);

        // update the TextViews
        dateTextView.setText(imageDate);
        timeTextView.setText(imageTime);
        specimenTypeTextView.setText(imageSpecimenType);
        gpsPositionTextView.setText(imageGPSPosition);
    }
}
