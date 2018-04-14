package com.example.user.mobilemicroscopy;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.user.mobilemicroscopy.database.ImageContract.ImageEntry;
import com.example.user.mobilemicroscopy.database.ImageDbHelper;

import com.example.user.mobilemicroscopy.database.ImageContract;

public class DetailsActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // initialize views
        mDateEditText = (EditText) findViewById(R.id.details_date_edit_text);
        mTimeEditText = (EditText) findViewById(R.id.details_time_edit_text);
        mSpecimenTypeEditText = (EditText) findViewById(R.id.details_specimen_type_edit_text);
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

                insertImage();

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

    public void insertImage() {
        String dateString = mDateEditText.getText().toString().trim();
        String timeString = mTimeEditText.getText().toString().trim();
        String specimenTypeString = mSpecimenTypeEditText.getText().toString().trim();

        // Create database helper
        ImageDbHelper databaseHelper = new ImageDbHelper(this);

        // Get object tp prepare to write to database
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        // Create a ContentValues object to insert values to record
        ContentValues values = new ContentValues();
        values.put(ImageEntry.COLUMN_NAME_DATE, dateString);
        values.put(ImageEntry.COLUMN_NAME_TIME, timeString);
        values.put(ImageEntry.COLUMN_NAME_SPECIMEN_TYPE, specimenTypeString);


        // Insert the object, get the id
        long newRowId = database.insert(ImageEntry.TABLE_NAME, null, values);

        // Check if insertion is successful
        if (newRowId == -1) {
            // Encounter error
            Toast.makeText(this, "Error saving", Toast.LENGTH_SHORT).show();
        } else {
            // successful
            Toast.makeText(this, "Successfully, id: " + newRowId, Toast.LENGTH_SHORT).show();
        }
    }
}
