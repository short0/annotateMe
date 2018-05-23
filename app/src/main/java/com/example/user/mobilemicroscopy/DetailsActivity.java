package com.example.user.mobilemicroscopy;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.mobilemicroscopy.database.ImageContract.ImageEntry;
import com.example.user.mobilemicroscopy.database.ImageDbHelper;

import com.example.user.mobilemicroscopy.database.ImageContract;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DetailsActivity extends AppCompatActivity {
    int x;
    int y;

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
    private Image mImage;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // initialize views
        mDateEditText = (EditText) findViewById(R.id.details_date_edit_text);
        mTimeEditText = (EditText) findViewById(R.id.details_time_edit_text);
        mSpecimenTypeEditText = (EditText) findViewById(R.id.details_specimen_type_edit_text);
        mMagnificationEditText = (EditText) findViewById(R.id.details_magnification_edit_text);
        mGPSPositionEditText = (EditText) findViewById(R.id.details_gps_position_edit_text);
        mStudentCommentEditText = (EditText) findViewById(R.id.details_student_comment_edit_text);



        // get the intent from MainActivity
        Intent intent = getIntent();

        mPassedType = intent.getStringExtra("passedType");
//        mCurrentOriginalImagePath = intent.getStringExtra("originalImagePath");
//        mCurrentAnnotatedImagePath = intent.getStringExtra("annotatedImagePath");
//        mCurrentOriginalImageFileName = intent.getStringExtra("originalImageFileName");
//        mCurrentAnnotatedImageFileName = intent.getStringExtra("annotatedImageFileName");

        // extract the image object in the intent
        mImage = (Image) intent.getSerializableExtra("image");

        mImageView = (ImageView) findViewById(R.id.details_image_view);

        // add click action on image view
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailsActivity.this, AnnotateActivity.class);
                // add the image to the intent to pass
                intent.putExtra("image", mImage);
//                intent.putExtra("annotatedImagePath", mCurrentAnnotatedImagePath);
                startActivity(intent);
            }
        });

        // display the image if the link is found in the intent
        if (mPassedType.equals("emptyObject")) {
            try {
                // create exif interface object and extract useful data
                mExifInterface = new ExifInterface(mImage.getAnnotatedImageLink());

                // extract and format date and time
                String dateTimeTag = mExifInterface.getAttribute(ExifInterface.TAG_DATETIME);
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

            case R.id.menu_upload:


                uploadToRDS();

                // Show text message
                Toast.makeText(this, "Upload", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * methods to save an image to database
     */
    public void saveImage() {
        // create a new Image object if no image is passed from MainActivity
//        if (!mPassedType.equals("imageObject")) {
//            mImage = new Image();
//        }

        if (mImage == null)
        {
            return;
        }

        // set the values to the object from the views
        mImage.setDate(mDateEditText.getText().toString());
        mImage.setTime(mTimeEditText.getText().toString());
        mImage.setSpecimenType(mSpecimenTypeEditText.getText().toString());
        mImage.setMagnification(mMagnificationEditText.getText().toString());
        mImage.setGpsPosition(mGPSPositionEditText.getText().toString());
        mImage.setStudentComment(mStudentCommentEditText.getText().toString());

        // set the links and file names
//        mImage.setOriginalImageLink(mCurrentOriginalImagePath);
//        mImage.setAnnotatedImageLink(mCurrentAnnotatedImagePath);
//        mImage.setOriginalFileName(mCurrentOriginalImageFileName);
//        mImage.setAnnotatedFileName(mCurrentAnnotatedImageFileName);

        // if the Image is null
        if (mPassedType.equals("emptyObject")) {
            // insert new image to the database
            database.addImage(mImage);
        } else {
            // update existing image in database
            database.updateImage(mImage);
        }


/*
        // create a new Image object if no image is passed from MainActivity
        if (!mPassedType.equals("imageObject")) {
            mImage = new Image();
        }

        // set the values to the object from the views
        mImage.setDate(mDateEditText.getText().toString());
        mImage.setTime(mTimeEditText.getText().toString());
        mImage.setSpecimenType(mSpecimenTypeEditText.getText().toString());
        mImage.setMagnification(mMagnificationEditText.getText().toString());
        mImage.setGpsPosition(mGPSPositionEditText.getText().toString());
        mImage.setStudentComment(mStudentCommentEditText.getText().toString());

        // set the links and file names
        mImage.setOriginalImageLink(mCurrentOriginalImagePath);
        mImage.setAnnotatedImageLink(mCurrentAnnotatedImagePath);
        mImage.setOriginalFileName(mCurrentOriginalImageFileName);
        mImage.setAnnotatedFileName(mCurrentAnnotatedImageFileName);

        // if the Image is null
        if (!mPassedType.equals("imageObject")) {
            // insert new image to the database
            database.addImage(mImage);
        } else {
            // update existing image in database
            database.updateImage(mImage);
        }

        // end the activity
        finish();
*/
    }

    /**
     * method to delete an image in database
     */
    public void deleteImage() {
        // If the image is not null
        if (mPassedType.equals("imageObject")) {
            // delete the image in database
            database.deleteImage(mImage);
        }

        // end the activity
        finish();
    }

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
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

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
//        ExifInterface exifInterface = null;
//        try
//        {
//            exifInterface = new ExifInterface(mCurrentAnnotatedImagePath);
//            float[] latLong = new float[2];
//            exifInterface.getLatLong(latLong);
//            Log.d("aaaaaaaaaaaaaaaaaaaaaaa", exifInterface.getAttribute(ExifInterface.TAG_DATETIME));
//        }
//        catch (IOException e)
//        {
//            e.printStackTrace();
//        }
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
     *
     * TODO: not working due to the path is null when restart
     */
    @Override
    protected void onResume() {
        super.onResume();
        displayImage();
    }


    /******************************************************************/

    public void uploadToRDS()
    {
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {

            String webServerUrl = "http://ec2-13-210-117-22.ap-southeast-2.compute.amazonaws.com/api/images.php";

//                Uri imageUri = Uri.parse(webServerUrl);
//                Intent webIntent = new Intent(Intent.ACTION_VIEW, imageUri);
//
//                startActivity(webIntent);
            MyAsyncTask myAsyncTask = new MyAsyncTask();
            myAsyncTask.execute(webServerUrl);
        } else {
            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
            Log.d("AAAAAAAAAAAAAAAAAAAA", "bbbbbbbbbbbbbbbbbb");
        }
    }


    private class MyAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String jsonResponse = "";
            InputStream inputStream = null;
            OutputStream outputStream = null;

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
            String originalImageLink = mImage.getOriginalImageLink();
            String annotattedImageLink = mImage.getAnnotatedImageLink();
            String studentComment = mImage.getStudentComment();
            String teacherComment = mImage.getTeacherComment();
            String username = "n.zafra";

            URL url = null;
            try {
                url = new URL(strings[0]);
                Log.d("AAAAAAAAAAAAAAAAa", strings[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);

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

                connection.connect();
                Log.d("AAAAAAAAAAAAAAAAAA", connection.getResponseCode() + "");

                // If the request was successful (response code 200),
                // then read the input stream and parse the response.
                if (connection.getResponseCode() == 200) {
                    inputStream = connection.getInputStream();
                    jsonResponse = readInputStream(inputStream);
                    Log.d("AAAAAAAAAAAAAAAAAAbbb", jsonResponse);
//                    extractFeatureFromJson(jsonResponse);
                } else {
                    Log.e("AAAAAAAAAAAAA", "Error response code: " + connection.getResponseCode());
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                if (inputStream != null) {
                    // function must handle java.io.IOException here
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return jsonResponse;
        }

        @Override
        protected void onPostExecute(String jsonResponse) {
            super.onPostExecute(jsonResponse);

            extractStatus(jsonResponse);
        }

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


        private void extractStatus(String jsonResponse) {
            // If the JSON string is empty or null, then return early.
            if (TextUtils.isEmpty(jsonResponse)) {
                return;
            }

            try {
                JSONObject root = new JSONObject(jsonResponse);
                int status = root.getInt("status");
                String message = root.getString("message");

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
}
