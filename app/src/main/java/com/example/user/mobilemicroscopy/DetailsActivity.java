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

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_IMAGE_LOADER = 100;

    // content URI of existing image
    private Uri mCurrentImageUri;

    /**
     * Date input EditText
     */
    private TextView mDateTextView;

    /**
     * Time input EditText
     */
    private TextView mTimeTextView;

    /**
     * Specimen Type input EditText
     */
    private EditText mSpecimenTypeEditText;

    /**
     * GPS Position input EditText
     */
    private EditText mGPSPositionEditText;

    private String simpleDate;

    private String simpleTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // get the date and time
        SimpleDateFormat dateFormater = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormater = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        simpleDate = dateFormater.format(date);
        simpleTime = timeFormater.format(date);

        // initialize views
        mDateTextView = (TextView) findViewById(R.id.details_date_text_view);
        mTimeTextView = (TextView) findViewById(R.id.details_time_text_view);
        mSpecimenTypeEditText = (EditText) findViewById(R.id.details_specimen_type_edit_text);
        mGPSPositionEditText = (EditText) findViewById(R.id.details_gps_position_edit_text);

        // get intent which is passed to
        Intent intent = getIntent();
        // get the URI in the intent
        mCurrentImageUri = intent.getData();

        // check the URI if it's null to set the title of the activity
        if (mCurrentImageUri == null) {
            setTitle("Details");
            mDateTextView.setText(simpleDate);
            mTimeTextView.setText(simpleTime);
        } else {
            setTitle("Details");
            // start the Loader
            getLoaderManager().initLoader(EXISTING_IMAGE_LOADER, null, this);
        }


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

                saveImage();

                // End the activity
                finish();

                // Show text
                Toast.makeText(this, "Save", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.menu_delete:
                // Show text
                Toast.makeText(this, "Delete", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void saveImage() {
        String dateString = mDateTextView.getText().toString().trim();
        String timeString = mTimeTextView.getText().toString().trim();
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
            mDateTextView.setText(date);
            mTimeTextView.setText(time);
            mSpecimenTypeEditText.setText(specimenType);
            mGPSPositionEditText.setText(gpsPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mDateTextView.setText("");
        mTimeTextView.setText("");
        mSpecimenTypeEditText.setText("");
        mGPSPositionEditText.setText("");
    }
}
