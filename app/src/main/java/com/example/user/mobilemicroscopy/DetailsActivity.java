package com.example.user.mobilemicroscopy;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.mobilemicroscopy.database.ImageContract.ImageEntry;
import com.example.user.mobilemicroscopy.database.ImageDbHelper;

import com.example.user.mobilemicroscopy.database.ImageContract;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DetailsActivity extends AppCompatActivity {
    /**
     * Image view to show the image
     */
    ImageView mImageView;
//    Bitmap myBitmap;

    /**
     * identify type of intent passed in
     */
    String mPassedType;

    /**
     * store current annotated image path
     */
    String mCurrentAnnotatedImagePath;

    /**
     * store current original image path
     */
    String mCurrentOriginalImagePath;

    /**
     * store the current original image file name
     */
    String mCurrentOriginalImageFileName;

    /**
     * store the annotated image file name;
     */
    String mCurrentAnnotatedImageFileName;

    /**
     * Date input EditText
     */
    private EditText mDateEditText;

    /**
     * Time input EditText
     */
    private EditText mTimeEditText;

    /**
     * Specimen Type input EditText
     */
    private EditText mSpecimenTypeEditText;

    /**
     * GPS Position input EditText
     */
    private EditText mGPSPositionEditText;

    /**
     * Comment input EditText
     */
    private EditText mCommentEditText;

    /**
     * store the image object passed by MainActivity
     */
    private Image mImage;

    /**
     * if the image object passed is null, the id will be default -100
     */
    private int id = -100;

    /**
     * hold the database
     */
    ImageDbHelper database;

    /**
     * Exif Interface to extract useful data
     */
    ExifInterface mExifInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // initialize views
        mDateEditText = (EditText) findViewById(R.id.details_date_edit_text);
        mTimeEditText = (EditText) findViewById(R.id.details_time_edit_text);
        mSpecimenTypeEditText = (EditText) findViewById(R.id.details_specimen_type_edit_text);
        mGPSPositionEditText = (EditText) findViewById(R.id.details_gps_position_edit_text);
        mCommentEditText = (EditText) findViewById(R.id.details_comment_edit_text);

        mImageView = (ImageView) findViewById(R.id.details_image_view);

        // add click action on image view
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DetailsActivity.this, AnnotateActivity.class);
                i.putExtra("annotatedImagePath", mCurrentAnnotatedImagePath);
                startActivity(i);
            }
        });

        // get the intent from MainActivity
        Intent intent = getIntent();

        mPassedType = intent.getStringExtra("passedType");
        mCurrentOriginalImagePath = intent.getStringExtra("originalImagePath");
        mCurrentAnnotatedImagePath = intent.getStringExtra("annotatedImagePath");
        mCurrentOriginalImageFileName = intent.getStringExtra("originalImageFileName");
        mCurrentAnnotatedImageFileName = intent.getStringExtra("annotatedImageFileName");

        // display the image if the link is found in the intent
        if (mPassedType.equals("annotatedImagePath")) {
            try
            {
                // create exif interface object and extract useful data
                mExifInterface = new ExifInterface(mCurrentAnnotatedImagePath);

                // extract and format date and time
                String dateTimeTag = mExifInterface.getAttribute(ExifInterface.TAG_DATETIME);
                String dateTag = dateTimeTag.substring(0, 10).trim();
                String timeTag = dateTimeTag.substring(10).trim();

                // extract GPS Position
                float[] latLong = new float[2];
                String gpsPositionTag = "unknown";
                if (mExifInterface.getLatLong(latLong))
                {
                    gpsPositionTag = latLong[0] + " " + latLong[1];
                }

                // set the data to the views
                mDateEditText.setText(dateTag);
                mTimeEditText.setText(timeTag);
                mGPSPositionEditText.setText(gpsPositionTag);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            displayImage();
        }

        // extract the image object in the intent
        mImage = (Image) intent.getSerializableExtra("image");

        // set text for all views if the image is not null
        if (mPassedType.equals("imageObject"))
        {
            id = mImage.getId();
            mDateEditText.setText(mImage.getDate());
            mTimeEditText.setText(mImage.getTime());
            mSpecimenTypeEditText.setText(mImage.getSpecimenType());
            mGPSPositionEditText.setText(mImage.getGpsPosition());
            mCommentEditText.setText(mImage.getComment());

            // add image links
            mCurrentOriginalImagePath = mImage.getOriginalImageLink();
            mCurrentAnnotatedImagePath = mImage.getAnnotatedImageLink();
            mCurrentOriginalImageFileName = mImage.getOriginalFileName();
            mCurrentAnnotatedImageFileName = mImage.getAnnotatedFileName();

            try
            {
                mExifInterface = new ExifInterface(mCurrentAnnotatedImagePath);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            displayImage();
        }

        // connect to the database
        database = new ImageDbHelper(getApplicationContext());
    }

    /**
     * Create menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Take action based on what is selected
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save:
                // save the image to database whether insert new image or update a image
                saveImage();

                // End the activity
                finish();

                // Show text message
                Toast.makeText(this, "Save", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.menu_delete:
                // delete the image in database
                deleteImage();

                // Show text message
                Toast.makeText(this, "Delete", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * methods to save an image to database
     */
    public void saveImage()
    {
        // create a new Image object if no image is passed from MainActivity
        if (!mPassedType.equals("imageObject"))
        {
            mImage = new Image();
        }

        // set the values to the object from the views
        mImage.setDate(mDateEditText.getText().toString());
        mImage.setTime(mTimeEditText.getText().toString());
        mImage.setSpecimenType(mSpecimenTypeEditText.getText().toString());
        mImage.setGpsPosition(mGPSPositionEditText.getText().toString());
        mImage.setComment(mCommentEditText.getText().toString());

        // set the links and file names
        mImage.setOriginalImageLink(mCurrentOriginalImagePath);
        mImage.setAnnotatedImageLink(mCurrentAnnotatedImagePath);
        mImage.setOriginalFileName(mCurrentOriginalImageFileName);
        mImage.setAnnotatedFileName(mCurrentAnnotatedImageFileName);

        // if the Image is null
        if (!mPassedType.equals("imageObject"))
        {
            // insert new image to the database
            database.addImage(mImage);
        }
        else
        {
            // update existing image in database
            database.updateImage(mImage);
        }

        // end the activity
        finish();
    }

    /**
     * method to delete an image in database
     */
    public void deleteImage()
    {
        // If the image is not null
        if (mPassedType.equals("imageObject"))
        {
            // delete the image in database
            database.deleteImage(mImage);
        }

        // end the activity
        finish();
    }

    private void displayImage() {
//        // Get the dimensions of the View
//        int targetW = mImageView.getWidth();
//        int targetH = mImageView.getHeight();
//
//        // Get the dimensions of the bitmap
//        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//        bmOptions.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
//        int photoW = bmOptions.outWidth;
//        int photoH = bmOptions.outHeight;
//
//        // Determine how much to scale down the image
//        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
//
//        // Decode the image file into a Bitmap sized to fill the View
//        bmOptions.inJustDecodeBounds = false;
//        bmOptions.inSampleSize = scaleFactor;
//        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentAnnotatedImagePath/*, bmOptions*/);
        mImageView.setImageBitmap(rotateImage(bitmap));
    }

    /**
     * rotate image using ExifInterface
     */
    public Bitmap rotateImage(Bitmap bitmap) {
//        ExifInterface exifInterface = null;
//        try
//        {
//            exifInterface = new ExifInterface(mCurrentAnnotatedImagePath);
//            float[] latLong = new float[2];
//            exifInterface.getLatLong(latLong);
//            Log.d("aaaaaaaaaaaaaaaaaaaaaaa", exifInterface.getAttribute(ExifInterface.TAG_DATETIME));
//        }
//        catch (IOException e)
//        {
//            e.printStackTrace();
//        }
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

        try
        {
            Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return rotatedBitmap;
        }
        catch (OutOfMemoryError e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * display the image when the activity is resumed
     *
     * TODO: not working due to the path is null when restart
     */
//    @Override
//    protected void onResume() {
//        super.onResume();
//        displayImage();
//    }
}
