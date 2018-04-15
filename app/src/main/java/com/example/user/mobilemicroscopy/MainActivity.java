package com.example.user.mobilemicroscopy;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.mobilemicroscopy.database.ImageContract;
import com.example.user.mobilemicroscopy.database.ImageDbHelper;

// import Contract class
import com.example.user.mobilemicroscopy.database.ImageContract.ImageEntry;

import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int IMAGE_LOADER = 0;

    ImageCursorAdapter mCursorAdapter;

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

        // Find list view
        ListView imageListView = findViewById(R.id.list);

        // Create an Adapter
        mCursorAdapter = new ImageCursorAdapter(this, null);

        // Attach the adapter to list view
        imageListView.setAdapter(mCursorAdapter);

        // Start the loader
        getLoaderManager().initLoader(IMAGE_LOADER, null, this);
    }

    @Override
    protected void onStart() {
//        displayDatabaseInfo();
        super.onStart();
    }

    /**
     * Method to display information in the onscreen
     */
//    private void displayDatabaseInfo() {
////        // Create an ImageDbHelper to access database
////        ImageDbHelper databaseHelper = new ImageDbHelper(this);
////
////        // Prepare to read from database
////        SQLiteDatabase database = databaseHelper.getReadableDatabase();
//
//        String[] projection = {
//                ImageEntry._ID,
//                ImageEntry.COLUMN_NAME_DATE,
//                ImageEntry.COLUMN_NAME_TIME
//        };
//
////        Cursor cursor = database.query(
////                ImageEntry.TABLE_NAME,
////                projection,
////                null,
////                null,
////                null,
////                null,
////                null
////        );
//
//        Cursor cursor = getContentResolver().query(
//                ImageEntry.CONTENT_URI,
//                projection,
//                null,
//                null,
//                null
//        );
//
//        // Find list view
//        ListView imageListView = findViewById(R.id.list);
//
//        // Create an Adapter
//        ImageCursorAdapter adapter = new ImageCursorAdapter(this, cursor);
//
//        // Attach the adapter to list view
//        imageListView.setAdapter(adapter);
//    }

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


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // projection
        String[] projection = {
                ImageEntry._ID,
                ImageEntry.COLUMN_NAME_DATE,
                ImageEntry.COLUMN_NAME_TIME
        };

        // The Loader will execute similar like the ContentProvider's query method
        return new CursorLoader(
                this,
                ImageEntry.CONTENT_URI,
                projection,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // update cursor
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // when reset loader, reset the cursor
        mCursorAdapter.swapCursor(null);
    }
}
