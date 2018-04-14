package com.example.user.mobilemicroscopy;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.mobilemicroscopy.database.ImageContract;
import com.example.user.mobilemicroscopy.database.ImageDbHelper;

// import Contract class
import com.example.user.mobilemicroscopy.database.ImageContract.ImageEntry;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        displayDatabaseInfo();
    }

    /**
     * Method to display information in the onscreen
     */
    private void displayDatabaseInfo() {
        // Create an ImageDbHelper to access database
        ImageDbHelper mDbHelper = new ImageDbHelper(this);

        // Prepare to read from database
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Perform raw query
        Cursor cursor = db.rawQuery("SELECT * FROM " + ImageEntry.TABLE_NAME, null);
        try
        {
            Toast.makeText(this, "Number of rows in image table in database: " + cursor.getCount(), Toast.LENGTH_SHORT).show();
        }
        finally
        {
            // Close the cursor when done to releases resources and makes it invalid.
            cursor.close();
        }
    }
}
