package com.example.user.mobilemicroscopy;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.mobilemicroscopy.database.ImageContract.ImageEntry;
import com.example.user.mobilemicroscopy.database.ImageDbHelper;

import com.example.user.mobilemicroscopy.database.ImageContract;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DetailsActivity extends AppCompatActivity /*implements LoaderManager.LoaderCallbacks<Cursor>*/ {

//    private static final int EXISTING_IMAGE_LOADER = 100;

    // content URI of existing image
//    private Uri mCurrentImageUri;

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

//    private String simpleDate;
//
//    private String simpleTime;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

//        // get the date and time
//        SimpleDateFormat dateFormater = new SimpleDateFormat("dd/MM/yyyy");
//        SimpleDateFormat timeFormater = new SimpleDateFormat("HH:mm:ss");
//        Date date = new Date();
//        simpleDate = dateFormater.format(date);
//        simpleTime = timeFormater.format(date);

        // initialize views
        mDateEditText = (EditText) findViewById(R.id.details_date_edit_text);
        mTimeEditText = (EditText) findViewById(R.id.details_time_edit_text);
        mSpecimenTypeEditText = (EditText) findViewById(R.id.details_specimen_type_edit_text);
        mGPSPositionEditText = (EditText) findViewById(R.id.details_gps_position_edit_text);

//        // get intent which is passed to
//        Intent intent = getIntent();
//        // get the URI in the intent
//        mCurrentImageUri = intent.getData();
//
//        // check the URI if it's null to set the title of the activity
//        if (mCurrentImageUri == null) {
//            setTitle("Details");
////            mDateEditText.setText(simpleDate);
////            mTimeEditText.setText(simpleTime);
//        } else {
//            setTitle("Details");
//            // start the Loader
//            getLoaderManager().initLoader(EXISTING_IMAGE_LOADER, null, this);
//        }

        // get the intent from MainActivity
        Intent intent = getIntent();

        // extract the image object in the intent
        mImage = (Image) intent.getSerializableExtra("image");

        // set text for all views if the image is not null
        if (mImage != null)
        {
            id = mImage.getId();
            mDateEditText.setText(mImage.getDate());
            mTimeEditText.setText(mImage.getTime());
            mSpecimenTypeEditText.setText(mImage.getSpecimenType());
            mGPSPositionEditText.setText(mImage.getGpsPosition());

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
        if (mImage == null)
        {
            mImage = new Image();
        }

        // set the values to the object from the views
        mImage.setDate(mDateEditText.getText().toString());
        mImage.setTime(mTimeEditText.getText().toString());
        mImage.setSpecimenType(mSpecimenTypeEditText.getText().toString());
        mImage.setGpsPosition(mGPSPositionEditText.getText().toString());

        // if the Image is null
        if (id == -100)
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
        if (mImage != null && id != -100)
        {
            // delete the image in database
            database.deleteImage(mImage);
        }

        // end the activity
        finish();
    }

    /**************************************************************************
    public void saveImage() {
        String dateString = mDateEditText.getText().toString().trim();
        String timeString = mTimeEditText.getText().toString().trim();
        String specimenTypeString = mSpecimenTypeEditText.getText().toString().trim();
        String gpsPositionString = mGPSPositionEditText.getText().toString().trim();

        // check if this is a new image
        if (mCurrentImageUri == null && TextUtils.isEmpty(dateString) && TextUtils.isEmpty(timeString)) {
            // if date and time are empty, do nothing
            return;
        }
//        // Create database helper
//        ImageDbHelper databaseHelper = new ImageDbHelper(this);
//
//        // Get object tp prepare to write to database
//        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        // Create a ContentValues object to insert values to record
        ContentValues values = new ContentValues();
        values.put(ImageEntry.COLUMN_NAME_DATE, dateString);
        values.put(ImageEntry.COLUMN_NAME_TIME, timeString);
        values.put(ImageEntry.COLUMN_NAME_SPECIMEN_TYPE, specimenTypeString);
        values.put(ImageEntry.COLUMN_NAME_GPS_POSITION, gpsPositionString);


//        // Insert the object, get the id
//        long newRowId = database.insert(ImageEntry.TABLE_NAME, null, values);


//        // Check if insertion is successful
//        if (newRowId == -1) {
//            // Encounter error
//            Toast.makeText(this, "Error saving", Toast.LENGTH_SHORT).show();
//        } else {
//            // successful
//            Toast.makeText(this, "Successfully, id: " + newRowId, Toast.LENGTH_SHORT).show();
//        }

        // Show a toast message depending on whether or not the insertion was successful
        // check if current URI is null
        if (mCurrentImageUri == null) {

            // Insert a new image into the provider
            Uri newUri = getContentResolver().insert(ImageEntry.CONTENT_URI, values);

            // error when inserting
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, "Fail to insert", Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, "Succeed to insert", Toast.LENGTH_SHORT).show();
            }
        }
        // otherwise, editing existing image
        else {
            // check how many rows are affected
            int rowsAffected = getContentResolver().update(mCurrentImageUri, values, null, null);

            if (rowsAffected == 0) {
                // If no rows were affected
                Toast.makeText(this, "Fail to update", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Succeed to update", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /*****************************************************************
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // projection
        String[] projection = {
                ImageEntry._ID,
                ImageEntry.COLUMN_NAME_DATE,
                ImageEntry.COLUMN_NAME_TIME,
                ImageEntry.COLUMN_NAME_SPECIMEN_TYPE,
                ImageEntry.COLUMN_NAME_ORIGINAL_FILE_NAME,
                ImageEntry.COLUMN_NAME_ANNOTATED_FILE_NAME,
                ImageEntry.COLUMN_NAME_GPS_POSITION,
                ImageEntry.COLUMN_NAME_MAGNIFICATION,
                ImageEntry.COLUMN_NAME_ORIGINAL_IMAGE_LINK,
                ImageEntry.COLUMN_NAME_ANNOTATED_IMAGE_LINK,
                ImageEntry.COLUMN_NAME_COMMENT
        };

        // The Loader will execute similar like the ContentProvider's query method
        return new CursorLoader(
                this,
                mCurrentImageUri,
                projection,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // exit if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            // Select columns to display
            int dateColumnIndex = cursor.getColumnIndex(ImageEntry.COLUMN_NAME_DATE);
            int timeColumnIndex = cursor.getColumnIndex(ImageEntry.COLUMN_NAME_TIME);
            int specimenTypeColumnIndex = cursor.getColumnIndex(ImageEntry.COLUMN_NAME_SPECIMEN_TYPE);
            int gpsPositionColumnIndex = cursor.getColumnIndex(ImageEntry.COLUMN_NAME_GPS_POSITION);

            // get the values to display
            String date = cursor.getString(dateColumnIndex);
            String time = cursor.getString(timeColumnIndex);
            String specimenType = cursor.getString(specimenTypeColumnIndex);
            String gpsPosition = cursor.getString(gpsPositionColumnIndex);

            // put data to the views
            mDateEditText.setText(date);
            mTimeEditText.setText(time);
            mSpecimenTypeEditText.setText(specimenType);
            mGPSPositionEditText.setText(gpsPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mDateEditText.setText("");
        mTimeEditText.setText("");
        mSpecimenTypeEditText.setText("");
        mGPSPositionEditText.setText("");
    }
    **********************************************************************/
}
