package com.example.user.mobilemicroscopy;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.mobilemicroscopy.drawing.DrawingItem;
import com.example.user.mobilemicroscopy.drawing.DrawingView;

import java.io.IOException;
import java.util.ArrayList;

public class CalibrateActivity extends AppCompatActivity {

    final Context context = this;

    /**
     * unit selection used to convert to micron
     */
    Integer toMicron = 1;


    /**
     * Hold drawing board
     */
    DrawingView mDrawingView;

    /**
     * Image view to hold the image
     */
    ImageView mImageView;

    /**
     * store the image object passed by MainActivity
     */
    Image mImage;

    /**
     * variable to store the current annotated image path
     */
    String mCurrentAnnotatedImagePath;

    /**
     * Hold the main bitmap
     */
    Bitmap mBitmap;

    /**
     * Hold the canvas to draw
     */
    Canvas mCanvas;

    /**
     * Add a point button
     */
    TextView textViewAddAPoint;

    /**
     * Enter real size button
     */
    TextView textViewEnterRealSize;

    /**
     * onCreate method
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibrate);

        // instruction message
        onCalibrationMsg();

        // get drawing view and image view
        mDrawingView = (DrawingView) findViewById(R.id.crop_drawing_view);
        mImageView = (ImageView) findViewById(R.id.crop_image_view);

        // get the buttons
        textViewAddAPoint = (TextView) findViewById(R.id.text_view_add_a_point);
        textViewEnterRealSize = (TextView) findViewById(R.id.text_view_enter_real_size);

        textViewAddAPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<DrawingItem> drawingItemList = mDrawingView.getDrawingItemList();

                if (drawingItemList.size() < 2) {

                    mDrawingView.addCropBox();

                    mDrawingView.invalidate();

                    // Show text message
                    Toast.makeText(getApplicationContext(), "Add point", Toast.LENGTH_SHORT).show();
                }

                if (drawingItemList.size() == 2)
                {
                    // Show text message
                    Toast.makeText(getApplicationContext(), "2 points have been added, please enter the length between the points", Toast.LENGTH_SHORT).show();
                }
            }
        });

        textViewEnterRealSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<DrawingItem> drawingItemList = mDrawingView.getDrawingItemList();

                if (drawingItemList.size() == 2) {
                    enterRealSize();

                    // Show text message
//                    onAddRealSizeMsg();
                    Toast.makeText(getApplicationContext(), "Enter real size", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    onAddPointsMsg();
//                    Toast.makeText(getApplicationContext(), "Please add 2 points on the screen", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // get the intent passed in
        Intent intent = getIntent();

        // extract the image object in the intent
        mImage = (Image) intent.getSerializableExtra("image");

        // get the link
        mCurrentAnnotatedImagePath = mImage.getAnnotatedImageLink();

        displayImage();


    }

    /**
     * Create menu
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_calibrate, menu);
        return super.onCreateOptionsMenu(menu);
    }


    /**
     * Set action for menu items
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        ArrayList<DrawingItem> drawingItemList = mDrawingView.getDrawingItemList();

        switch (item.getItemId()) {

            case R.id.menu_save_calibrate:
                finish();
                // Show text message
                Toast.makeText(getApplicationContext(), "Save", Toast.LENGTH_SHORT).show();
                return true;


        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Display the main image
     */
    private void displayImage() {
        // create mutable bitmap
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inMutable = true;
        mBitmap = BitmapFactory.decodeFile(mCurrentAnnotatedImagePath, bitmapOptions);

        mBitmap = rotateImage(mBitmap);

        // create a canvas to hold the bitmap
        mCanvas = new Canvas(mBitmap);

        mImageView.setImageBitmap(mBitmap);
    }


    /**
     * rotate image using ExifInterface
     */
    public Bitmap rotateImage(Bitmap bitmap) {
        ExifInterface exifInterface = null;
        try {
            exifInterface = new ExifInterface(mCurrentAnnotatedImagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

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
     * Enter real size between the point and calculate length per µm
     */
    public void enterRealSize() {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View scaleBarDialogView = layoutInflater.inflate(R.layout.enter_real_size, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        final EditText realSizeEditText = (EditText) scaleBarDialogView.findViewById(R.id.edit_text_enter_real_size);
        final Spinner spinner = (Spinner) scaleBarDialogView.findViewById(R.id.units_spinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.units_array, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        // Add action to when spinner (drop-down menu) option change
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {




            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                Log.d(getClass().getName(), "" + i);
                if (i == 0)
                {
                    toMicron = 1;
                }
                else if (i == 1)
                {
                    toMicron = 1000;
                }
                else if (i == 2)
                {
                    toMicron = 1000000;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });


        // set scale_bar_dialogalog.xml to alert dialog builder
        // calculate the length per pixel
        alertDialogBuilder.setView(scaleBarDialogView).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                String realSizeString = realSizeEditText.getText().toString();
//                String objectSizeInOcularUnitsString = objectSizeInOcularUnits.getText().toString();

                if (!realSizeString.equals("")) {
                    double realSizeDouble = Double.parseDouble(realSizeString);
//                    double objectSizeInOcularUnitsDouble = Double.parseDouble(objectSizeInOcularUnitsString);

                    Log.d(getClass().getName(), "" + realSizeDouble);

                    DrawingItem point1 = mDrawingView.getDrawingItemList().get(0);
                    DrawingItem point2 = mDrawingView.getDrawingItemList().get(1);

                    float centerX1 = (point1.getRectangle().left + point1.getRectangle().right) / 2;
                    float centerY1 = (point1.getRectangle().top + point1.getRectangle().bottom) / 2;

                    float centerX2 = (point2.getRectangle().left + point2.getRectangle().right) / 2;
                    float centerY2 = (point2.getRectangle().top + point2.getRectangle().bottom) / 2;

                    if (centerX1 == centerX2 && centerY1 == centerY2)
                    {
                        onCalibrationPointsMsg();
//                        Toast.makeText(getApplicationContext(), "Please move 2 points apart", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        double pixels = (double) Math.sqrt(Math.pow(centerX1 - centerX2, 2) + Math.pow(centerY1 - centerY2, 2));
                        Log.d(getClass().getName(), "" + pixels);

                        // length per pixel in µm
                        double lengthPerPixel = realSizeDouble / pixels * toMicron;


                        // set the value to AnnotateActivity
                        AnnotateActivity.lengthPerPixel = lengthPerPixel;
                        Log.d(getClass().getName(), "" + AnnotateActivity.lengthPerPixel);

                        onCalibrationDoneMsg();
//                        Toast.makeText(getApplicationContext(), "Done calibrating", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog,int id) {
                dialog.cancel();
            }
        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }



    /**
     *   CALIBRATION MESSAGES
     */


    /**
     *   Calibration Help Msg
     */
    public void onCalibrationMsg() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Calibration Help");
        builder.setMessage("Please set calibration by following these two steps: \n\nStep 1: Add 2 points \n\nStep 2: Enter the real length between the points");
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

    /**
     *   Calibration Move the Points Msg
     */
    public void onAddPointsMsg() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Calibration Help");
        builder.setMessage("Please add 2 points on the screen");
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

    /**
     *   Calibration Move the Points Msg
     */
    public void onCalibrationPointsMsg() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Calibration Points Error");
        builder.setMessage("Please move the 2 points apart");
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

    /**
     *   Calibration Done Msg
     */
    public void onCalibrationDoneMsg() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Calibration Completed");
        // Add the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                finish();
            }
        });

        // Create the AlertDialog
        AlertDialog dialog = builder.create();

        dialog.show();
    }

}
