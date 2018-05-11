package com.example.user.mobilemicroscopy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

public class ImageAdapter extends ArrayAdapter<Image> {

    private List<Image> mList;

    /**
     * Constructor
     */
    public ImageAdapter(Context context, List<Image> list) {
        super(context, 0, list);
        mList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItemView = convertView;

        // Check if there is a recycled view availabe to reuse
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        // Find the record at current position
        Image currentImage = getItem(position);

        // find the views in list item view
        ImageView imageView = (ImageView) listItemView.findViewById(R.id.image_view);
        TextView fileNameView = (TextView) listItemView.findViewById(R.id.file_name);
        TextView dateView = (TextView) listItemView.findViewById(R.id.date);
        TextView timeView = (TextView) listItemView.findViewById(R.id.time);
        TextView specimenTypeView = (TextView) listItemView.findViewById(R.id.speciment_type);
        TextView gpsPositionView = (TextView) listItemView.findViewById(R.id.gps_position);

        // set the text to the views using data in currentImage
        imageView.setImageBitmap(rotateImage(BitmapFactory.decodeFile(currentImage.getAnnotatedImageLink()), currentImage.getAnnotatedImageLink()));
        displayImage(imageView, currentImage.getAnnotatedImageLink());
        fileNameView.setText(currentImage.getAnnotatedFileName());
        dateView.setText(currentImage.getDate());
        timeView.setText(currentImage.getTime());
        specimenTypeView.setText(currentImage.getSpecimenType());
        gpsPositionView.setText(currentImage.getGpsPosition());

        // return the view to display
        return listItemView;
    }

    /**
     * rotate image using ExifInterface
     */
    public Bitmap rotateImage(Bitmap bitmap, String path) {
        ExifInterface exifInterface = null;
        try {
            exifInterface = new ExifInterface(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }

        try {
            Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return rotatedBitmap;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    private void displayImage(ImageView imageView, String path) {
        // Get the dimensions of the View
        int targetW = 60; //imageView.getWidth();
        int targetH = 60; //imageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);
        Bitmap rotatedBitmap = rotateImage(bitmap, path);
        imageView.setImageBitmap(rotatedBitmap);

    }

}
