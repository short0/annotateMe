package com.example.user.mobilemicroscopy;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.example.user.mobilemicroscopy.aws.Constants;
import com.example.user.mobilemicroscopy.aws.Util;
import com.example.user.mobilemicroscopy.database.ImageDbHelper;
import com.example.user.mobilemicroscopy.help.HelpDetailsActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DetailsActivity extends AppCompatActivity {
    int x;
    int y;

//    private Uri mCropImagedUri;

    Context context = this;

    /**
     * The number for crop operation used in crop() and onActivityResult method
     */
    private final int CROP_IMAGE = 100;

    /**
     * Hold the URL to query the images
     */
    private static final String SIMPLE_IMAGE_WEB_API_URL = "http://ec2-13-210-117-22.ap-southeast-2.compute.amazonaws.com/api/images.php";

    /*
     * Hold the username
     */
    String username = "";

    /**
     * The TransferUtility is the primary class for managing transfer to S3
     */
    private TransferUtility transferUtility;

    /**
     * Reference to the utility class
     */
    private Util util;

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
     * Magnification input EditText
     */
    private EditText mMagnificationEditText;

    /**
     * GPS Position input EditText
     */
    private EditText mGPSPositionEditText;

    /**
     * Student Comment input EditText
     */
    private EditText mStudentCommentEditText;

    /**
     * store the image object passed by MainActivity
     */
     Image mImage;

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

    /**
     * onCreate method
     *
     * @param savedInstanceState
     */


    /**
     *   For web view - submission of work
     *
     */
//    private WebView webview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);


        // Initializes TransferUtility, always do this before using it.
        util = new Util();
        transferUtility = util.getTransferUtility(this);

        // initialize views
        mDateEditText = (EditText) findViewById(R.id.details_date_edit_text);
        mTimeEditText = (EditText) findViewById(R.id.details_time_edit_text);
        mSpecimenTypeEditText = (EditText) findViewById(R.id.details_specimen_type_edit_text);
        mMagnificationEditText = (EditText) findViewById(R.id.details_magnification_edit_text);
        mGPSPositionEditText = (EditText) findViewById(R.id.details_gps_position_edit_text);
        mStudentCommentEditText = (EditText) findViewById(R.id.details_student_comment_edit_text);


        // get the intent from MainActivity
        Intent intent = getIntent();

        // get the username from MainActivity
        username = getIntent().getStringExtra("username");
        // set the username to empty string if null
        if (username == null)
        {
            username = "";
        }
        Log.d(getClass().getName(), username);

        mPassedType = intent.getStringExtra("passedType");

        // extract the image object in the intent
        mImage = (Image) intent.getSerializableExtra("image");

        mImageView = (ImageView) findViewById(R.id.details_image_view);

        // add click action on image view
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent zoomIntent = new Intent(DetailsActivity.this, ZoomActivity.class);
                // add the image to the intent to pass
                zoomIntent.putExtra("image", mImage);
                startActivity(zoomIntent);

                // Show text message
                Toast.makeText(DetailsActivity.this, "Zoom", Toast.LENGTH_SHORT).show();

            }
        });

        // display the image if the link is found in the intent
        if (mPassedType.equals("emptyObject")) {
            try {
                // create exif interface object and extract useful data
                mExifInterface = new ExifInterface(mImage.getAnnotatedImageLink());

                // extract and format date and time
//                String dateTimeTag = mExifInterface.getAttribute(ExifInterface.TAG_DATETIME);
                /**
                 *     Convert date into Australian format dd/mm/yyyy
                 */
                //Gets DateTime and converts date to Australian format
                SimpleDateFormat dateParser = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
                SimpleDateFormat dateConverter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date d = null;
                try
                {
                    d = dateParser.parse(mExifInterface.getAttribute(ExifInterface.TAG_DATETIME));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String dateTimeTag = dateConverter.format(d);

                /**
                 *    END convert date code
                 */


                String dateTag = dateTimeTag.substring(0, 10).trim();
                String timeTag = dateTimeTag.substring(10).trim();

                // extract GPS Position
                float[] latLong = new float[2];
                String gpsPositionTag = "unknown";
                if (mExifInterface.getLatLong(latLong)) {
                    gpsPositionTag = latLong[0] + " " + latLong[1];
                }

                // set the data to the views
                mDateEditText.setText(dateTag);
                mTimeEditText.setText(timeTag);
                mGPSPositionEditText.setText(gpsPositionTag);
            } catch (IOException e) {
                e.printStackTrace();
            }

            displayImage();
        }


//        // extract the image object in the intent
//        mImage = (Image) intent.getSerializableExtra("image");

        // set text for all views if the image is not null
        if (mPassedType.equals("imageObject")) {
            id = mImage.getId();
            mDateEditText.setText(mImage.getDate());
            mTimeEditText.setText(mImage.getTime());
            mSpecimenTypeEditText.setText(mImage.getSpecimenType());
            mMagnificationEditText.setText(mImage.getMagnification());
            mGPSPositionEditText.setText(mImage.getGpsPosition());
            mStudentCommentEditText.setText(mImage.getStudentComment());

            // add image links
            mCurrentOriginalImagePath = mImage.getOriginalImageLink();
            mCurrentAnnotatedImagePath = mImage.getAnnotatedImageLink();
            mCurrentOriginalImageFileName = mImage.getOriginalFileName();
            mCurrentAnnotatedImageFileName = mImage.getAnnotatedFileName();

            try {
                mExifInterface = new ExifInterface(mCurrentAnnotatedImagePath);
            } catch (IOException e) {
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
     * Hide menu item depends on logged in or not
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        // hide items if logged in or a guest
        if (username == null || username.equals(""))
        {
            MenuItem uploadMenuItem = (MenuItem) menu.findItem(R.id.menu_upload);
            MenuItem uploadSubmitItem = (MenuItem) menu.findItem(R.id.menu_submit);
            uploadMenuItem.setVisible(false);
            uploadSubmitItem.setVisible(false);

        }
        else
        {
            MenuItem uploadMenuItem = (MenuItem) menu.findItem(R.id.menu_save);
            uploadMenuItem.setVisible(false);
        }

        return true;
    }

    /**
     * Take action based on what is selected
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {


            case R.id.menu_help:

                Intent intentHelp = new Intent(DetailsActivity.this, HelpDetailsActivity.class);
                startActivity(intentHelp);

                // Show text message
                Toast.makeText(this, "Details Page Help", Toast.LENGTH_SHORT).show();
                return true;


            case R.id.menu_annotate:

                Intent intent = new Intent(DetailsActivity.this, AnnotateActivity.class);

                // add the image to the intent to pass
                intent.putExtra("image", mImage);
                startActivity(intent);
                return true;


            case R.id.menu_crop:
                // crop the image
                crop();

                // Show text message
                Toast.makeText(this, "Crop Image", Toast.LENGTH_SHORT).show();
                return true;


            case R.id.menu_save:
                if (mSpecimenTypeEditText.getText().toString() == null || mSpecimenTypeEditText.getText().toString().equals(""))
                {
                    showEmptySpecimenMessage();
                }
                else {
                    // save the image to database whether insert new image or update a image
                    //hides soft keyboard before saving
                    hideSoftKeyboard(this);
                    saveImage();

                    // end activity
                    finish();

                    // Show text message
                    Toast.makeText(this, "Save", Toast.LENGTH_SHORT).show();
                }
                return true;

            case R.id.menu_delete:
                // delete the image in database
                deleteImage();
                return true;

            case R.id.menu_restore_original:

                restoreOriginal();

                return true;


            case R.id.menu_upload:
                if (mSpecimenTypeEditText.getText().toString() == null || mSpecimenTypeEditText.getText().toString().equals(""))
                {
                    showEmptySpecimenMessage();
                }
                else {

                    // upload to RDS
                    uploadToRDS();

                    Log.d("bbbbbbbbbbbbbbb", username);

                    // upload to S3 if the image is not null and username is valid
                    if (mImage != null && username != null && !username.equals("")) {
                        beginUpload(mImage.getOriginalImageLink(), Constants.BUCKET_NAME + "/" + username + "/original");
                        beginUpload(mImage.getAnnotatedImageLink(), Constants.BUCKET_NAME + "/" + username + "/annotated");
                    }

                    //hides soft keyboard before saving
                    hideSoftKeyboard(this);

                    // save the image to database whether insert new image or update a image
                    saveImage();

                    // End the activity
                    finish();

                    // Show text message
                    Toast.makeText(this, "Save and upload", Toast.LENGTH_SHORT).show();
                }
                return true;


            //to submit user must save and upload first
            case R.id.menu_submit:

                 // Goes to the Submit activity
                Intent submitWork = new Intent(DetailsActivity.this, SubmitActivity.class);
                submitWork.putExtra("originalFileName", mCurrentOriginalImageFileName);
                submitWork.putExtra("username", username);
                startActivity(submitWork);

                // End the activity
                finish();

                // Show text message
                // Toast.makeText(this, "Save and Submit", Toast.LENGTH_SHORT).show();

                return true;

        }
        return super.onOptionsItemSelected(item);

    }




     /**
     * Triggered when this activity received a value from other activity
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;

        switch (requestCode) {

            case CROP_IMAGE:

                if (data != null) {
                    // extract the crop image from the intent
                    Log.d("ddddddddddd", "data not null");
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        Bitmap bitmap = extras.getParcelable("data");
                        // save the bitmap to the annotated image
                        saveBitmap(bitmap, mImage.getAnnotatedImageLink());
                    }
                }

                break;
        }
    }


    /**
     * Save the bitmap to the filePath
     *
     * @param bitmap
     * @param filePath
     */
    public boolean saveBitmap(Bitmap bitmap, String filePath) {
        File file = new File(filePath);

        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Crop using installed apps
     */
    public void crop()
    {
        // Send an intent with action crop
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");

        // check if there are apps to handle this intent
        List<ResolveInfo> list = getPackageManager().queryIntentActivities( intent, 0 );

        int size = list.size();

        if (size == 0) // not found, exit
        {
            Toast.makeText(this, "Can not find image crop app", Toast.LENGTH_SHORT).show();

            return;
        } else { // this portion only work with Gallery app,
            Toast.makeText(this, "There are image crop app", Toast.LENGTH_SHORT).show();

            Uri u = getImageUri(this, rotateImage(BitmapFactory.decodeFile(mImage.getAnnotatedImageLink())));
            intent.setData(u);

            intent.putExtra("outputX", 4000);  //have increased this value for crop from 3000 to 4000
            intent.putExtra("outputY", 4000);  //have increased this value for crop from 3000 to 4000
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", true);

            startActivityForResult(intent, CROP_IMAGE);
            return;
        }
    }

    /**
     * Get the image URI of a bitmap
     *
     * @param inContext
     * @param inImage
     */
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    /**
     * method to Pinch Zoom an image
     */
    public void zoomImage()
    {
        ImageZoom img = new ImageZoom(this);
        Bitmap bitmap = BitmapFactory.decodeFile(mImage.getAnnotatedImageLink());
        img.setImageBitmap(rotateImage(bitmap));
        setContentView(img);
    }


    /**
     * methods to save an image to database
     */
    public void saveImage() {
        // create a new Image object if no image is passed from MainActivity
//        if (!mPassedType.equals("imageObject")) {
//            mImage = new Image();
//        }

        if (mImage == null) {
            return;
        }

        // set the values to the object from the views
        mImage.setDate(mDateEditText.getText().toString());
        mImage.setTime(mTimeEditText.getText().toString());
        mImage.setSpecimenType(mSpecimenTypeEditText.getText().toString());
        mImage.setMagnification(mMagnificationEditText.getText().toString());
        mImage.setGpsPosition(mGPSPositionEditText.getText().toString());
        mImage.setStudentComment(mStudentCommentEditText.getText().toString());

        // if the Image is null
        if (mPassedType.equals("emptyObject")) {
            // insert new image to the database
            database.addImage(mImage);
        } else {
            // update existing image in database
            database.updateImage(mImage);
        }
    }


    /**
     * method to delete an image in database
     */
    public void deleteImage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete record");
        builder.setMessage("Are you sure? Do you want to delete this record?");

        // Add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                // If the image is not null
                if (mPassedType.equals("imageObject")) {
                    // delete the image in database
                    database.deleteImage(mImage);
                }

                // Show text message
                Toast.makeText(context, "Delete", Toast.LENGTH_SHORT).show();

                // end the activity
                finish();

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog

            }
        });

        // Create the AlertDialog
        AlertDialog dialog = builder.create();

        dialog.show();
    }

    /**
     * Restore original image
     */
    public void restoreOriginal()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Restore original image");
        builder.setMessage("Are you sure? Do you want to restore the original image?");

        // Add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                // copy the original image to annotated image
                try {
                    copy(mImage.getOriginalImageLink(), mImage.getAnnotatedImageLink());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                displayImage();

                // Show text message
                Toast.makeText(context, "Restore original image", Toast.LENGTH_SHORT).show();

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog

            }
        });

        // Create the AlertDialog
        AlertDialog dialog = builder.create();

        dialog.show();
    }


    /**
     * method to display the main image
     */
    private void displayImage() {
        // Get the dimensions of the View
        int targetW = 300; //mImageView.getMeasuredWidth();
        int targetH = 300; //mImageView.getMeasuredHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mImage.getAnnotatedImageLink(), bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mImage.getAnnotatedImageLink(), bmOptions);
        mImageView.setImageBitmap(rotateImage(bitmap));
    }


    /**
     * rotate image using ExifInterface
     */
    public Bitmap rotateImage(Bitmap bitmap) {

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

        try {
            Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return rotatedBitmap;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * display the image when the activity is resumed
     * <p>
     * TODO: not working due to the path is null when restart
     */
    @Override
    protected void onResume() {
        super.onResume();
        displayImage();
        Log.d("USERNAME", username);
    }


    /******************************************************************/
    /**
     * Method to upload image details to AWS RDS using a simple web API
     */
    public void uploadToRDS() {
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {

            // start an async task to do in background
            RdsAsyncTask rdsAsyncTask = new RdsAsyncTask();
            rdsAsyncTask.execute(SIMPLE_IMAGE_WEB_API_URL);
        } else {
            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
            Log.d("AAAAAAAAAAAAAAAAAAAA", "bbbbbbbbbbbbbbbbbb");
        }
    }

    /**
     * An AsyncTask to add a record to RDS
     */
    private class RdsAsyncTask extends AsyncTask<String, Void, String> {
        /**
         * Execute the action
         *
         * @param strings
         * @return
         */
        @Override
        protected String doInBackground(String... strings) {
            String jsonResponse = "";
            InputStream inputStream = null;
            OutputStream outputStream = null;

            // get the details to upload
            mImage.setDate(mDateEditText.getText().toString());
            mImage.setTime(mTimeEditText.getText().toString());
            mImage.setSpecimenType(mSpecimenTypeEditText.getText().toString());
            mImage.setMagnification(mMagnificationEditText.getText().toString());
            mImage.setGpsPosition(mGPSPositionEditText.getText().toString());
            mImage.setStudentComment(mStudentCommentEditText.getText().toString());

            String date = mImage.getDate();
            String time = mImage.getTime();
            String specimenType = mImage.getSpecimenType();
            String originalFileName = mImage.getOriginalFileName();
            String annotatedFileName = mImage.getAnnotatedFileName();
            String GPSposition = mImage.getGpsPosition();
            String magnification = mImage.getMagnification();

            // assign the link to S3
            String originalImageLink = "https://s3-ap-southeast-2.amazonaws.com/kiotmicroscopy/" + username + "/original/" + mImage.getOriginalFileName();
            String annotattedImageLink = "https://s3-ap-southeast-2.amazonaws.com/kiotmicroscopy/" + username + "/annotated/" + mImage.getAnnotatedFileName();
            String studentComment = mImage.getStudentComment();
            String teacherComment = mImage.getTeacherComment();

            // parse the URL
            URL url = null;
            try {
                url = new URL(strings[0]);
                Log.d("AAAAAAAAAAAAAAAAa", strings[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            // make http connection
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("POST"); // use POST method
                connection.setDoInput(true);
                connection.setDoOutput(true);

                // add the parameters
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("date", date)
                        .appendQueryParameter("time", time)
                        .appendQueryParameter("specimenType", specimenType)
                        .appendQueryParameter("originalFileName", originalFileName)
                        .appendQueryParameter("annotatedFileName", annotatedFileName)
                        .appendQueryParameter("GPSposition", GPSposition)
                        .appendQueryParameter("magnification", magnification)
                        .appendQueryParameter("originalImageLink", originalImageLink)
                        .appendQueryParameter("annotatedImageLink", annotattedImageLink)
                        .appendQueryParameter("studentComment", studentComment)
                        .appendQueryParameter("teacherComment", teacherComment)
                        .appendQueryParameter("username", username);
                String query = builder.build().getEncodedQuery();

                // write parameters to output stream
                outputStream = connection.getOutputStream();

                BufferedWriter bufferedWriter = null;
                try {
                    bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                bufferedWriter.write(query);
                bufferedWriter.flush();
                bufferedWriter.close();

                outputStream.close();

                connection.connect(); // make the connection
                Log.d("AAAAAAAAAAAAAAAAAA", connection.getResponseCode() + "");

                // If the request was successful (response code 200),
                // then read the input stream and parse the response.
                if (connection.getResponseCode() == 200) {
                    inputStream = connection.getInputStream();
                    jsonResponse = readInputStream(inputStream); // read the response
                    Log.d("AAAAAAAAAAAAAAAAAAbbb", jsonResponse);
//                    extractFeatureFromJson(jsonResponse);
                } else {
                    Log.e("AAAAAAAAAAAAA", "Error response code: " + connection.getResponseCode());
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect(); // disconnect
                }
                if (inputStream != null) {
                    // function must handle java.io.IOException here
                    try {
                        inputStream.close(); // close the input stream
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return jsonResponse; // return a JSON response
        }

        /**
         * Execute after executing action
         *
         * @param jsonResponse
         */
        @Override
        protected void onPostExecute(String jsonResponse) {
            super.onPostExecute(jsonResponse);

            // get info from the response
            extractStatus(jsonResponse);
        }

        /**
         * Read the information from input stream
         *
         * @param inputStream
         * @return
         * @throws IOException
         */
        private String readInputStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }

        /**
         * Extract information from a JSON Response
         *
         * @param jsonResponse
         */
        private void extractStatus(String jsonResponse) {
            // If the JSON string is empty or null, then return early.
            if (TextUtils.isEmpty(jsonResponse)) {
                return;
            }

            try {
                JSONObject root = new JSONObject(jsonResponse);
                int status = root.getInt("status");
                String message = root.getString("message");

                // check the status and show result of operation
                if (status == 1) {
                    Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                } else if (status == 0) {
                    Toast.makeText(getApplicationContext(), "Failed. Please try again", Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                Log.e("AAAAAAAAAAAAAAAAAAA", "Problem parsing the earthquake JSON results", e);
            }
        }
    }

    /******************************************************************/
    /**
     * Begins to upload the file specified by the file path.
     */
    private void beginUpload(String filePath, String bucket) {
        if (filePath == null) {
            Toast.makeText(this, "Could not find the filepath of the selected file", Toast.LENGTH_LONG).show();
            return;
        }
        File file = new File(filePath);
        TransferObserver observer = transferUtility.upload(bucket, file.getName(), file);
    }
    /******************************************************************/

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

    /**
     * Method to hide soft keyboard
     */
    public static void hideSoftKeyboard(Activity activity) {

        try {

            InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


    /**
     *   DetailsActivity Messages
     */

    /**
     *    On Back Button Press
     */
    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Save record");
        builder.setMessage("Do you want to save changes?");
        // Add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                if (mSpecimenTypeEditText.getText().toString() == null || mSpecimenTypeEditText.getText().toString().equals(""))
                {
                    showEmptySpecimenMessage();
                }
                else {
                    saveImage();
                    finish();
                }
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                finish();
            }
        });

        // Create the AlertDialog
        AlertDialog dialog = builder.create();

        dialog.show();
    }

    /**
     * showEmptySpecimenMessage
     */
    public void showEmptySpecimenMessage() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Warning!");
        builder.setMessage("Please enter the specimen type");
        // Add the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });

        // Create the AlertDialog
        AlertDialog dialog = builder.create();

        dialog.show();
    }
}
