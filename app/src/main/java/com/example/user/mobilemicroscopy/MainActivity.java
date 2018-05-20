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

    public static int COLOR_WHITE = 0;

    public static int COLOR_BLACK = 1;

    // variable to hold annotated image file
    File mAnnotatedImageFile;

    // variable to hold original image file
    File mOriginalImageFile;

    // store original image file name
    static String mOriginalImageFileName;

    // store annotated image file name
    static String mAnnotatedImageFileName;

    // store original image path
    static String mOriginalImagePath;

    // store annotated image path
    static String mAnnotatedImagePath;

    static final int REQUEST_TAKE_PHOTO = 1;

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

                // send the intent
                startActivity(detailsIntent);
            }
        });
    }

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
     * Take action based on what menu item is selected
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_login:
                // go to LoginActivity
//                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//                startActivity(intent);
                // Show text message
                Toast.makeText(this, "Login", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("aaaaaaaaaaaaa", mOriginalImagePath);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
//            mImageView.setImageBitmap(imageBitmap);

            try {
                copy(mOriginalImagePath, mAnnotatedImagePath);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
            // send the original image path with the intent
            intent.putExtra("originalImagePath", mOriginalImagePath);
            // send the annotated image path with the intent
            intent.putExtra("annotatedImagePath", mAnnotatedImagePath);
            // send the original image file name
            intent.putExtra("originalImageFileName", mOriginalImageFileName);
            // send the annotated image file name
            intent.putExtra("annotatedImageFileName", mAnnotatedImageFileName);
            // declare what is passed in the intent
            intent.putExtra("passedType", "annotatedImagePath");

            startActivity(intent);
        }
    }

    private File createOriginalImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String originalImageFileName = timeStamp + "_original";
        File directory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        File image = File.createTempFile(
//                originalImageFileName,  /* prefix */
//                ".jpg",         /* suffix */
//                directory      /* directory */
//        );

        // make the file using the path
        File imageFile = new File(directory + "/" + originalImageFileName + ".jpg");
        // save the path to pass to other activity
        mOriginalImagePath = imageFile.getAbsolutePath();

        // get the file name
        mOriginalImageFileName = imageFile.getName();

        return imageFile;
    }

    private File createAnnotatedImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String annotatedImageFileName = timeStamp + "_annotated";
        File directory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        File image = File.createTempFile(
//                originalImageFileName,  /* prefix */
//                ".jpg",         /* suffix */
//                directory      /* directory */
//        );

        // make the file using the path
        File imageFile = new File(directory + "/" + annotatedImageFileName + ".jpg");
        // save the path to pass to other activity
        mAnnotatedImagePath = imageFile.getAbsolutePath();

        // get the file name
        mAnnotatedImageFileName = imageFile.getName();

        return imageFile;
    }

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
