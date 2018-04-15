package com.example.user.mobilemicroscopy;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

        FloatingActionButton fabTakePhoto = (FloatingActionButton) findViewById(R.id.fab_take_photo);
        fabTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onStart() {
        displayDatabaseInfo();
        super.onStart();
    }

    /**
     * Method to display information in the onscreen
     */
    private void displayDatabaseInfo() {
        // Create an ImageDbHelper to access database
        ImageDbHelper databaseHelper = new ImageDbHelper(this);

        // Prepare to read from database
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        String[] projection = {
                ImageEntry._ID,
                ImageEntry.COLUMN_NAME_DATE,
        };

//        Cursor cursor = database.query(
//                ImageEntry.TABLE_NAME,
//                projection,
//                null,
//                null,
//                null,
//                null,
//                null
//        );

        Cursor cursor = getContentResolver().query(
                ImageEntry.CONTENT_URI,
                projection,
                null,
                null,
                null
        );

        TextView displayView = findViewById(R.id.displayView);


        try {
            displayView.setText("number of rows: " + cursor.getCount() + "\n\n");
            displayView.append(ImageEntry._ID + "-" + ImageEntry.COLUMN_NAME_DATE + "\n");

            int idColumnIndex = cursor.getColumnIndex(ImageEntry._ID);
            int dateColumIndex = cursor.getColumnIndex(ImageEntry.COLUMN_NAME_DATE);

            while (cursor.moveToNext()) {
                int currentID = cursor.getInt(idColumnIndex);
                String currentDate = cursor.getString(dateColumIndex);

                displayView.append(currentID + "-" + currentDate + "\n");
            }
        } finally {
            // Close the cursor when done to releases resources and makes it invalid.
            cursor.close();
        }
    }

    /**
     * Create menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Take action based on what is selected
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_insert:
                // Show text
                Toast.makeText(this, "Insert", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.menu_delete_all:
                // Show text
                Toast.makeText(this, "Delete", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
