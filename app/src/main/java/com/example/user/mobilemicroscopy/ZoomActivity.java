package com.example.user.mobilemicroscopy;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

/**
 * Activity to handle the zoom function
 */
public class ZoomActivity extends AppCompatActivity {

    /**
     * Image view to hold the image
     */
    ImageView mImageView;

    /**
     * store the image object passed by MainActivity
     */
    Image mImage;

    /**
     * Exif Interface to extract useful data
     */
    ExifInterface mExifInterface;

    /**
     * onCreate method
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom);

        // get the intent from MainActivity
        Intent intent = getIntent();

        // extract the image object in the intent
        mImage = (Image) intent.getSerializableExtra("image");

        mImageView = (ImageView) findViewById(R.id.zoom_image_view);

        // create exif interface object and extract useful data
        try {
            mExifInterface = new ExifInterface(mImage.getAnnotatedImageLink());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // add click action on image view
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                zoomImage();

                // Show text message
//                Toast.makeText(getApplicationContext(), "Zoom", Toast.LENGTH_SHORT).show();

            }
        });

        displayImage();
    }

    /**
     * method to display the main image
     */
    private void displayImage() {
        // Get the dimensions of the View
        int targetW = 300; //mImageView.getMeasuredWidth();
        int targetH = 300; //mImageView.getMeasuredHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mImage.getAnnotatedImageLink(), bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mImage.getAnnotatedImageLink(), bmOptions);
        mImageView.setImageBitmap(rotateImage(bitmap));
    }

    /**
     * method to Pinch Zoom an image
     */
    public void zoomImage()
    {
        ImageZoom img = new ImageZoom(this);
        Bitmap bitmap = BitmapFactory.decodeFile(mImage.getAnnotatedImageLink());
//        img.setImageBitmap(rotateImage(bitmap));
        img.setImageBitmap(rotateImage(bitmap));
        setContentView(img);
    }

    /**
     * rotate image using ExifInterface
     */
    public Bitmap rotateImage(Bitmap bitmap) {

        int orientation = mExifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

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
}
