package com.example.user.mobilemicroscopy;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.mobilemicroscopy.database.ImageContract;
import com.example.user.mobilemicroscopy.database.ImageDbHelper;

// import Contract class
import com.example.user.mobilemicroscopy.database.ImageContract.ImageEntry;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity /*implements LoaderManager.LoaderCallbacks<Cursor>*/ {

//    private static final int IMAGE_LOADER = 100;

//    ImageCursorAdapter mImageCursorAdapter;

    FloatingActionButton fabTakePhoto;

    /**
     * hold the images to display
     */
    ArrayList<Image> mImages;

    /**
     * use to connect to the database
     */
    ImageDbHelper database;

    /**
     * the adapter adapts the data in the list to the list view
     */
    ImageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // connect to the database
        database = new ImageDbHelper(getApplicationContext());

        // initialize the list
        mImages = new ArrayList<Image>();

        // get images to the list from database
        loadDatabase();

        // specify action when the button is clicked
        fabTakePhoto = (FloatingActionButton) findViewById(R.id.fab_take_photo);
        fabTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                startActivity(intent);
            }
        });

        // Find list view
        ListView imageListView = findViewById(R.id.list_view);

        // find empty view
        View emptyView = findViewById(R.id.empty_view);
        // set empty view
        imageListView.setEmptyView(emptyView);

        // Create an Adapter
//        mImageCursorAdapter = new ImageCursorAdapter(this, null);
        adapter = new ImageAdapter(this, mImages);

        // Attach the adapter to list view
//        imageListView.setAdapter(mImageCursorAdapter);
        imageListView.setAdapter(adapter);

//        // set up on item click listener
//        imageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//                // create an intent to send
//                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
//
//                // get the current URI
//                Uri imageUri = ContentUris.withAppendedId(ImageEntry.CONTENT_URI, id);
//
//                // set the intent to data field of the intent
//                intent.setData(imageUri);
//
//                // Launch the intent
//                startActivity(intent);
//            }
//        });
//
//        // Start the loader
//        getLoaderManager().initLoader(IMAGE_LOADER, null, this);

        // specify action when an item is clicked
        imageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // create an intent to send
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);

                // get the clicked object
                Image image = mImages.get(position);

                Intent detailsIntent = new Intent(MainActivity.this, DetailsActivity.class);

                // add the image to the intent to pass
                detailsIntent.putExtra("image", image);

                // send the intent
                startActivity(detailsIntent);
            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();
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
     * Take action based on what menu item is selected
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_insert:
                // Show text message
                Toast.makeText(this, "Insert", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.menu_delete_all:
                // delete all images
                deleteAll();
                // Show text message
                Toast.makeText(this, "Delete all", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }


//    @Override
//    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
//        // projection
//        String[] projection = {
//                ImageEntry._ID,
//                ImageEntry.COLUMN_NAME_DATE,
//                ImageEntry.COLUMN_NAME_TIME,
//                ImageEntry.COLUMN_NAME_SPECIMEN_TYPE,
//                ImageEntry.COLUMN_NAME_ORIGINAL_FILE_NAME,
//                ImageEntry.COLUMN_NAME_ANNOTATED_FILE_NAME,
//                ImageEntry.COLUMN_NAME_GPS_POSITION,
//                ImageEntry.COLUMN_NAME_MAGNIFICATION,
//                ImageEntry.COLUMN_NAME_ORIGINAL_IMAGE_LINK,
//                ImageEntry.COLUMN_NAME_ANNOTATED_IMAGE_LINK,
//                ImageEntry.COLUMN_NAME_COMMENT
//        };
//
//        return new CursorLoader(
//                this,
//                ImageEntry.CONTENT_URI,
//                projection,
//                null,
//                null,
//                null
//        );
//    }

//    @Override
//    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
//        // update cursor
//        mImageCursorAdapter.swapCursor(cursor);
//    }
//
//    @Override
//    public void onLoaderReset(Loader<Cursor> loader) {
//        // when reset loader, reset the cursor
//        mImageCursorAdapter.swapCursor(null);
//    }

    /**
     * delete all rows method
     */
    private void deleteAll() {
//        int rowsDeleted = getContentResolver().delete(ImageEntry.CONTENT_URI, null, null);
        database.deleteAll();
        loadDatabase();
        adapter.notifyDataSetChanged();
    }

    public void loadDatabase()
    {
        mImages.clear();
        ArrayList<Image> temp = database.getAllImages();
        mImages.addAll(temp);
    }

    /**
     * Specify actions taken when the activity is resumed
     */
    @Override
    protected void onResume() {
        super.onResume();
        loadDatabase();
        adapter.notifyDataSetChanged();
    }
}
