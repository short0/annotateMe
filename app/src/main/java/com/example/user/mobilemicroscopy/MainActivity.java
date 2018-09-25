package com.example.user.mobilemicroscopy;

import android.Manifest;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    /**
     * hold the username
     */
    String username = "";

    /**
     * variable to hold annotated image file
     */
    File mAnnotatedImageFile;

    /**
     * variable to hold original image file
     */
    File mOriginalImageFile;

    /**
     * variable to hold calibrate image file
     */
    File mCalibrateImageFile;

    /**
     * store original image file name
     */
    static String mOriginalImageFileName;

    /**
     * store annotated image file name
     */
    static String mAnnotatedImageFileName;

    /**
     * stor calibrate image file name
     */
    static String mCalibrateImageFileName;

    /**
     * store original image path
     */
    static String mOriginalImagePath;

    /**
     * store annotated image path
     */
    static String mAnnotatedImagePath;

    /**
     * store calibrate image path
     */
    static String mCalibrateImagePath;

    /**
     * take photo request
     */
    static final int REQUEST_TAKE_PHOTO = 1;

    /**
     * calibrate request
     */
    static final int REQUEST_CALIBRATE = 2;

    /**
     * Take photo button
     */
    FloatingActionButton fabTakePhoto;

    /**
     * Calibrate button
     */
    FloatingActionButton fabCalibrate;

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

    /**
     * onCreate method
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get the username from LoginActivity
        username = getIntent().getStringExtra("username");
        // set the username to empty string if null
        if (username == null)
        {
            username = "";
        }

        // check for CAMERA permission, if not granted, request it
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

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
//                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
//                startActivity(intent);
                sendImageCaptureIntent();
            }
        });

        fabCalibrate = (FloatingActionButton) findViewById(R.id.fab_calibrate);
        fabCalibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
//                startActivity(intent);
                sendImageCaptureIntent2();
            }
        });

        // Find list view
        ListView imageListView = findViewById(R.id.list_view);

        // find empty view
        View emptyView = findViewById(R.id.empty_view);
        // set empty view
        imageListView.setEmptyView(emptyView);

        // Create an Adapter
        adapter = new ImageAdapter(this, mImages);

        // Attach the adapter to list view
        imageListView.setAdapter(adapter);

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

                // declare what is passed in the intent
                detailsIntent.putExtra("passedType", "imageObject");

                // declare what is passed in the intent
                detailsIntent.putExtra("username", username);

                // send the intent
                startActivity(detailsIntent);
            }
        });
    }

    /**
     * onStart method
     */
    @Override
    protected void onStart() {
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
     * Hide menu item depends on logged in or not
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        // hide login item if logged in
        if (username != null && !username.equals(""))
        {
            MenuItem loginMenuItem = (MenuItem) menu.findItem(R.id.menu_login);
            loginMenuItem.setVisible(false);
        }
        else // hide logout item if not logged in
        {
            MenuItem logoutMenuItem = (MenuItem) menu.findItem(R.id.menu_log_out);
            logoutMenuItem.setVisible(false);
        }

        return true;
    }

    /**
     * Take action based on what menu item is selected
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_login:
                // go to LoginActivity
//                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//                startActivity(intent);

                // end activity
                finish();
                // Show text message
                Toast.makeText(this, "Login", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.menu_log_out:
                // end activity
                finish();

                // Show text message
                Toast.makeText(this, "Log out", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    /**
     * delete all rows method
     */
    private void deleteAll() {
        database.deleteAll();
        loadDatabase();
        adapter.notifyDataSetChanged();
    }

    /**
     * load all images to a list
     */
    public void loadDatabase() {
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

    /**
     * Use the existing camera app to take picture
     */
    private void sendImageCaptureIntent() {
        // create an intent to capture image using camera app
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // check if there is an camera app can handle the intent
        if (intent.resolveActivity(getPackageManager()) != null) {
            // create actual file
            mOriginalImageFile = null;
            mAnnotatedImageFile = null;
            try {
                mOriginalImageFile = createOriginalImageFile();
                mAnnotatedImageFile = createAnnotatedImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }

            // check if file is created an start the camera
            if (mOriginalImageFile != null) {
                // get the URI from the file created in fileprovider
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.android.fileprovider", mOriginalImageFile);
                // indicate a content resolver URI to store the capture image
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                // start the camera
                startActivityForResult(intent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    /**
     * Taking photo for calibrating
     */
    private void sendImageCaptureIntent2() {
        // create an intent to capture image using camera app
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // check if there is an camera app can handle the intent
        if (intent.resolveActivity(getPackageManager()) != null) {
            // create actual file
            mCalibrateImageFile = null;
            try {
                mCalibrateImageFile = createCalibrateImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }

            // check if file is created an start the camera
            if (mCalibrateImageFile != null) {
                // get the URI from the file created in fileprovider
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.android.fileprovider", mCalibrateImageFile);
                // indicate a content resolver URI to store the capture image
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                // start the camera
                startActivityForResult(intent, REQUEST_CALIBRATE);
            }
        }
    }

    /**
     * Method executed when the photo is taken
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Log.d("aaaaaaaaaaaaa", mOriginalImagePath);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {

            try {
                copy(mOriginalImagePath, mAnnotatedImagePath);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Image image = new Image();
            image.setOriginalImageLink(mOriginalImagePath);
            image.setAnnotatedImageLink(mAnnotatedImagePath);
            image.setOriginalFileName(mOriginalImageFileName);
            image.setAnnotatedFileName(mAnnotatedImageFileName);

            Intent intent = new Intent(MainActivity.this, DetailsActivity.class);

            // add the image to the intent to pass
            intent.putExtra("image", image);

            // declare what is passed in the intent
            intent.putExtra("passedType", "emptyObject");

            // declare what is passed in the intent
            intent.putExtra("username", username);

            startActivity(intent);
        }

        /////////////////////////////////////////////////////////////
        if (requestCode == REQUEST_CALIBRATE && resultCode == RESULT_OK) {

            Image image = new Image();

            image.setAnnotatedImageLink(mCalibrateImagePath);

            Intent intent = new Intent(MainActivity.this, CalibrateActivity.class);

            // add the image to the intent to pass
            intent.putExtra("image", image);

            startActivity(intent);
        }
    }

    /**
     * Method to create original image file
     *
     * @return
     * @throws IOException
     */
    private File createOriginalImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String originalImageFileName = timeStamp + "_original";
        File directory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        // make the file using the path
        File imageFile = new File(directory + "/" + originalImageFileName + ".jpg");
        // save the path to pass to other activity
        mOriginalImagePath = imageFile.getAbsolutePath();

        // get the file name
        mOriginalImageFileName = imageFile.getName();

        return imageFile;
    }

    /**
     * Method to create annotated image file
     *
     * @return
     * @throws IOException
     */
    private File createAnnotatedImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String annotatedImageFileName = timeStamp + "_annotated";
        File directory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        // make the file using the path
        File imageFile = new File(directory + "/" + annotatedImageFileName + ".jpg");
        // save the path to pass to other activity
        mAnnotatedImagePath = imageFile.getAbsolutePath();

        // get the file name
        mAnnotatedImageFileName = imageFile.getName();

        return imageFile;
    }

    /**
     * Method to create calibrate image file
     *
     * @return
     * @throws IOException
     */
    private File createCalibrateImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String calibrateImageFileName = timeStamp + "_calibrate";
        File directory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        // make the file using the path
        File imageFile = new File(directory + "/" + calibrateImageFileName + ".jpg");
        // save the path to pass to other activity
        mCalibrateImagePath = imageFile.getAbsolutePath();

        // get the file name
        mCalibrateImageFileName = imageFile.getName();

        return imageFile;
    }

    /**
     * Method to copy file
     *
     * @param srcName
     * @param dstName
     * @throws IOException
     */
    public void copy(String srcName, String dstName) throws IOException {
        File src = new File(srcName);
        File dst = new File(dstName);
        FileInputStream inStream = new FileInputStream(src);
        FileOutputStream outStream = new FileOutputStream(dst);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();
    }
}
