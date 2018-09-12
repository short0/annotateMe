package com.example.user.mobilemicroscopy;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.mobilemicroscopy.drawing.PointItem;
import com.example.user.mobilemicroscopy.drawing.DrawingItem;
import com.example.user.mobilemicroscopy.drawing.DrawingView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class CalibrateActivity extends AppCompatActivity {

    final Context context = this;

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

    TextView textViewAddAPoint;
    TextView textViewEnterRealSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibrate);

        Toast.makeText(this, "Please add 2 points, then enter real size", Toast.LENGTH_LONG).show();

        // get drawing view and image view
        mDrawingView = (DrawingView) findViewById(R.id.crop_drawing_view);
        mImageView = (ImageView) findViewById(R.id.crop_image_view);

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
            }
        });

        textViewEnterRealSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<DrawingItem> drawingItemList = mDrawingView.getDrawingItemList();

                if (drawingItemList.size() == 2) {
                    enterRealSize();

                    // Show text message
                    Toast.makeText(getApplicationContext(), "Enter real size", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Please add 2 points on the screen", Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_calibrate, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ArrayList<DrawingItem> drawingItemList = mDrawingView.getDrawingItemList();

        switch (item.getItemId()) {


            case R.id.menu_save_calibrate:

//                crop();
                finish();

                // Show text message
                Toast.makeText(this, "Save", Toast.LENGTH_SHORT).show();

                return true;

//            case R.id.menu_add_point:
//
//
//                if (drawingItemList.size() < 2) {
//
//                    mDrawingView.addCropBox();
//
//                    mDrawingView.invalidate();
//
//                    // Show text message
//                    Toast.makeText(this, "Add point", Toast.LENGTH_SHORT).show();
//                }
//
//                return true;
//
//            case R.id.menu_enter_real_size:
////                ArrayList<DrawingItem> drawingItemList = mDrawingView.getDrawingItemList();
//
//                if (drawingItemList.size() == 2) {
//                    enterRealSize();
//
//                    // Show text message
//                    Toast.makeText(this, "Enter real size", Toast.LENGTH_SHORT).show();
//                }
//                else
//                {
//                    Toast.makeText(this, "Please add 2 points on the screen", Toast.LENGTH_SHORT).show();
//                }
//
//                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void crop()
    {
        // get current image Matrix
        Matrix imageMatrix = mImageView.getImageMatrix();

        // get values from matrix and manipulate them
        float[] values = new float[9]; // get 9 values from imageMatrix
        imageMatrix.getValues(values);
//                        Matrix c = new Matrix(imageMatrix);
        Matrix newMatrix = new Matrix(); // make new matrix to draw on image

        float scaleX = values[Matrix.MSCALE_X];
        float scaleY = values[Matrix.MSCALE_Y];
        values[Matrix.MSCALE_X] = 1 / scaleX;
        values[Matrix.MSKEW_X] = 0;
        values[Matrix.MTRANS_X] = -1 * (values[Matrix.MTRANS_X] / scaleX);
        values[Matrix.MSKEW_Y] = 0;
        values[Matrix.MSCALE_Y] = 1 / scaleY;
        values[Matrix.MTRANS_Y] = (-1) * (values[Matrix.MTRANS_Y] / scaleY);
        values[Matrix.MPERSP_0] = 0;
        values[Matrix.MPERSP_1] = 0;
        values[Matrix.MPERSP_2] = 1;

        // create new matrix to draw
        newMatrix.setValues(values);

        ArrayList<DrawingItem> drawingItemList = mDrawingView.getDrawingItemList();
        for (DrawingItem item : drawingItemList) {
            if (item instanceof PointItem) {
                RectF cropRect = ((PointItem) item).getRectangle();
                newMatrix.mapRect(cropRect);
                Bitmap resultBit = Bitmap.createBitmap(mBitmap, (int) cropRect.left, (int) cropRect.top, (int) cropRect.width(), (int) cropRect.height());

                saveBitmap(resultBit, mCurrentAnnotatedImagePath);

                finish();
            }



        }
    }

    /**
     * Display the main image
     */
    private void displayImage() {
//        // Get the dimensions of the View
//        int targetW = mImageView.getWidth();
//        int targetH = mImageView.getHeight();
//
//        // Get the dimensions of the bitmap
//        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//        bmOptions.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
//        int photoW = bmOptions.outWidth;
//        int photoH = bmOptions.outHeight;
//
//        // Determine how much to scale down the image
//        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
//
//        // Decode the image file into a Bitmap sized to fill the View
//        bmOptions.inJustDecodeBounds = false;
//        bmOptions.inSampleSize = scaleFactor;
//        bmOptions.inPurgeable = true;

//        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentAnnotatedImagePath/*, bmOptions*/);
//        mImageView.setImageBitmap(rotateImage(bitmap));

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
     * Save bitmap to file
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



    public void enterRealSize() {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View scaleBarDialogView = layoutInflater.inflate(R.layout.enter_real_size, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        final EditText realSizeEditText = (EditText) scaleBarDialogView.findViewById(R.id.edit_text_enter_real_size);
//        final EditText objectSizeInOcularUnits = (EditText) scaleBarDialogView.findViewById(R.id.edit_text_object_size_in_ocular_units);

        final Spinner spinner = (Spinner) scaleBarDialogView.findViewById(R.id.units_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.units_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);



        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("ccccccccccccccccc", "" + i);
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
        alertDialogBuilder.setView(scaleBarDialogView).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                String realSizeString = realSizeEditText.getText().toString();
//                String objectSizeInOcularUnitsString = objectSizeInOcularUnits.getText().toString();

                if (!realSizeString.equals("")) {
                    double realSizeDouble = Double.parseDouble(realSizeString);
//                    double objectSizeInOcularUnitsDouble = Double.parseDouble(objectSizeInOcularUnitsString);

                    Log.d("ssssssssssssssss", "" + realSizeDouble);

                    DrawingItem point1 = mDrawingView.getDrawingItemList().get(0);
                    DrawingItem point2 = mDrawingView.getDrawingItemList().get(1);

                    float centerX1 = (point1.getRectangle().left + point1.getRectangle().right) / 2;
                    float centerY1 = (point1.getRectangle().top + point1.getRectangle().bottom) / 2;

                    float centerX2 = (point2.getRectangle().left + point2.getRectangle().right) / 2;
                    float centerY2 = (point2.getRectangle().top + point2.getRectangle().bottom) / 2;

                    if (centerX1 == centerX2 && centerY1 == centerY2)
                    {
                        Toast.makeText(getApplicationContext(), "Please move 2 points apart", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        double pixels = (double) Math.sqrt(Math.pow(centerX1 - centerX2, 2) + Math.pow(centerY1 - centerY2, 2));
                        Log.d("ssssssssssssssss", "" + pixels);

                        // length per pixel in µm
                        double lengthPerPixel = realSizeDouble / pixels * toMicron;


                        // set the value to AnnotateActivity
                        AnnotateActivity.lengthPerPixel = lengthPerPixel;
                        Log.d("ssssssssssssssss", "" + AnnotateActivity.lengthPerPixel);

                        Toast.makeText(getApplicationContext(), "Done calibrating", Toast.LENGTH_SHORT).show();
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
}
