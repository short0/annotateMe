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

        // find the column indexes in cursor
        int dateColumnIndex = cursor.getColumnIndex(ImageEntry.COLUMN_NAME_DATE);
        int timeColumnIndex = cursor.getColumnIndex(ImageEntry.COLUMN_NAME_TIME);

        // extract the values
        String imageDate = cursor.getString(dateColumnIndex);
        String imageTime = cursor.getString(timeColumnIndex);

        // update the TextViews
        dateTextView.setText(imageDate);
        timeTextView.setText(imageTime);
    }
}
