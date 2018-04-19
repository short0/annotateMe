package com.example.user.mobilemicroscopy;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ImageAdapter extends ArrayAdapter<Image> {

    private List<Image> mList;

    /**
     * Constructor
     */
    public ImageAdapter(Context context, List<Image> list)
    {
        super(context, 0, list);
        mList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItemView = convertView;

        // Check if there is a recycled view availabe to reuse
        if (listItemView == null)
        {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        // Find the record at current position
        Image currentImage = getItem(position);

        // find the views in list item view
        TextView fileNameView = (TextView) listItemView.findViewById(R.id.file_name);
        TextView dateView = (TextView) listItemView.findViewById(R.id.date);
        TextView timeView = (TextView) listItemView.findViewById(R.id.time);
        TextView specimenTypeView = (TextView) listItemView.findViewById(R.id.speciment_type);
        TextView gpsPositionView = (TextView) listItemView.findViewById(R.id.gps_position);

        // set the text to the views using data in currentImage
        fileNameView.setText(currentImage.getAnnotatedFileName());
        dateView.setText(currentImage.getDate());
        timeView.setText(currentImage.getTime());
        specimenTypeView.setText(currentImage.getSpecimenType());
        gpsPositionView.setText(currentImage.getGpsPosition());

        // return the view to display
        return listItemView;
    }


}
