package com.example.user.mobilemicroscopy;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.user.mobilemicroscopy.drawing.ArrowItem;
import com.example.user.mobilemicroscopy.drawing.DrawingItem;
import com.example.user.mobilemicroscopy.drawing.DrawingView;
import com.example.user.mobilemicroscopy.drawing.ScaleBarItem;
import com.example.user.mobilemicroscopy.drawing.TextBoxItem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class AnnotateActivity extends AppCompatActivity {

    /**
     * Hold the context
     */
    final Context context = this;

    /**
     * Hold drawing board
     */
    DrawingView mDrawingView;

    /**
     * Image view to hold the image
     */
    ImageView mImageView;

    /**
     * Hold the main bitmap
     */
    Bitmap mBitmap;

    /**
     * Hold the canvas to draw
     */
    Canvas mCanvas;

    /**
     * variable to store the current annotated image path
     */
    String mCurrentAnnotatedImagePath;

    // hold the bottom menu buttons
    ImageView buttonWhiteArrow;
    ImageView buttonBlackArrow;
    ImageView buttonWhiteText;
    ImageView buttonBlackText;
    ImageView buttonWhiteScaleBar;
    ImageView buttonBlackScaleBar;

    /**
     * Mehtod called when activity created
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annotate);

        // get drawing view and image view
        mDrawingView = (DrawingView) findViewById(R.id.drawing_view);
        mImageView = (ImageView) findViewById(R.id.annotate_image_view);

        // get the intent passed in
        Intent intent = getIntent();

        // get the link
        mCurrentAnnotatedImagePath = ((Image) intent.getSerializableExtra("image")).getAnnotatedImageLink();

        displayImage();

        // find all bottom menu buttons
        buttonWhiteArrow = (ImageView) findViewById(R.id.button_white_arrow);
        buttonBlackArrow = (ImageView) findViewById(R.id.button_black_arrow);
        buttonWhiteText = (ImageView) findViewById(R.id.button_white_text);
        buttonBlackText = (ImageView) findViewById(R.id.button_black_text);
        buttonWhiteScaleBar = (ImageView) findViewById(R.id.button_white_scale_bar);
        buttonBlackScaleBar = (ImageView) findViewById(R.id.button_black_scale_bar);

        buttonWhiteArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addArrow(Color.WHITE);
                Toast.makeText(getApplicationContext(), "White arrow", Toast.LENGTH_SHORT).show();
            }
        });

        buttonBlackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addArrow(Color.BLACK);
                Toast.makeText(getApplicationContext(), "Black arrow", Toast.LENGTH_SHORT).show();
            }
        });

        buttonWhiteText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addText(Color.WHITE);
                Toast.makeText(getApplicationContext(), "White text", Toast.LENGTH_SHORT).show();
            }
        });

        buttonBlackText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addText(Color.BLACK);
                Toast.makeText(getApplicationContext(), "Black text", Toast.LENGTH_SHORT).show();
            }
        });

        buttonWhiteScaleBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addScaleBar(Color.WHITE);
                Toast.makeText(getApplicationContext(), "White scale bar", Toast.LENGTH_SHORT).show();
            }
        });

        buttonBlackScaleBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addScaleBar(Color.BLACK);
                Toast.makeText(getApplicationContext(), "Black scale bar", Toast.LENGTH_SHORT).show();
            }
        });
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
     * Add arrow
     */
    public void addArrow(int color) {
        mDrawingView.addArrow(color);
        mDrawingView.invalidate();
    }

    /**
     * Add text
     */
    public void addText(final int color) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View textDiaglogView = layoutInflater.inflate(R.layout.text_dialog, null);

        final EditText editTextTextDialog = (EditText) textDiaglogView.findViewById(R.id.edit_text_text_dialog);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // set text_dialogalog.xml to alertdialog builder
        alertDialogBuilder
                .setView(textDiaglogView) // set the view
//                                        .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // get user input and set it to result edit text
                                String textInput = editTextTextDialog.getText().toString();
                                Log.d("aaaaaaaaaaaaaaaaaaaaaaa", textInput);

                                if (textInput != null && !textInput.equals("")) {
                                    mDrawingView.addText(textInput, color);
                                    mDrawingView.invalidate();
                                }

                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    /**
     * Add scale bar
     */
    public void addScaleBar(final int color) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View scaleBarDialogView = layoutInflater.inflate(R.layout.scale_bar_dialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        final EditText objectiveLens = (EditText) scaleBarDialogView.findViewById(R.id.edit_text_objective_lens);
        final EditText eyepiece = (EditText) scaleBarDialogView.findViewById(R.id.edit_text_eyepiece);

        // set text_dialogalog.xml to alertdialog builder
        alertDialogBuilder
                .setView(scaleBarDialogView)
//                                        .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String objectiveLensString = objectiveLens.getText().toString();
                                String eyepieceString = eyepiece.getText().toString();

                                if (!objectiveLensString.equals("") && !eyepieceString.equals("")) {
                                    float objectiveLensFloat = Float.parseFloat(objectiveLensString);
                                    float eyepieceFloat = Float.parseFloat(eyepieceString);

                                    if (objectiveLensFloat != 0 && eyepieceFloat != 0) {
                                        float magnification = objectiveLensFloat * eyepieceFloat;
                                        float realSize = 10000 / magnification;

                                        String realSizeWithUnit = "";
                                        String formattedRealSize = "";

                                        DecimalFormat formatter = new DecimalFormat("#.##");
                                        // if real size > 1000 µm
                                        if (realSize > 1000) {
                                            formattedRealSize = formatter.format(realSize / 1000);
                                            realSizeWithUnit = formattedRealSize + " mm";
                                        } else {
                                            formattedRealSize = formatter.format(realSize);
                                            realSizeWithUnit = formattedRealSize + " µm";
                                        }

//                                        Log.d("aaaaaaaaaaaaaaaaaaaaaaa", result1 + " " + result2);

                                        mDrawingView.addTextForScaleBar(realSizeWithUnit, color);
                                        mDrawingView.addScaleBar(color);

                                        mDrawingView.invalidate();
                                    }
                                }

                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    /**
     * Method call when top menu created
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu
        getMenuInflater().inflate(R.menu.menu_annotate, menu);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Action when a menu item is click
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_save:
                drawOnBitmap();
                saveBitmap(mBitmap, mCurrentAnnotatedImagePath);
                finish();

                Toast.makeText(getApplicationContext(), "Save", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.menu_clear:

                Toast.makeText(getApplicationContext(), "Clear", Toast.LENGTH_SHORT).show();

                // clear all drawing item on view
                mDrawingView.clear();

                return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    /**
     * Save all drawing items to main bitmap and display
     */
    public void drawOnBitmap() {
        Bitmap whiteArrowBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.white_arrow_with_tail);
        Bitmap blackArrowBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.black_arrow_with_tail);

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
            if (item instanceof ArrowItem) {
                Matrix concatMatrix = new Matrix(((ArrowItem) item).getMatrix());
                concatMatrix.postConcat(newMatrix);
                if (((ArrowItem) item).getColor() == Color.WHITE) {
                    mCanvas.drawBitmap(whiteArrowBitmap, concatMatrix, null);
                } else if (((ArrowItem) item).getColor() == Color.BLACK) {
                    mCanvas.drawBitmap(blackArrowBitmap, concatMatrix, null);
                }
            }

            if (item instanceof TextBoxItem) {
                float differenceX = values[Matrix.MTRANS_X];
                float differenceY = values[Matrix.MTRANS_Y];
                float scale_X = values[Matrix.MSCALE_X];
                float scale_Y = values[Matrix.MSCALE_Y];

                // prepare to draw
                mCanvas.save();
                mCanvas.translate(differenceX, differenceY);
                mCanvas.scale(scale_X, scale_Y);

                Paint paint = new Paint();
                if (((TextBoxItem) item).getColor() == Color.WHITE) {
                    paint.setColor(Color.WHITE);
                } else if (((TextBoxItem) item).getColor() == Color.BLACK) {
                    paint.setColor(Color.BLACK);
                }
                paint.setTextSize(((TextBoxItem) item).getTextSize());

                mCanvas.drawText(((TextBoxItem) item).getText(), item.getRectangle().left, item.getRectangle().bottom, paint);

                mCanvas.restore();
            }

            if (item instanceof ScaleBarItem) {
                float differenceX = values[Matrix.MTRANS_X];
                float differenceY = values[Matrix.MTRANS_Y];
                float scale_X = values[Matrix.MSCALE_X];
                float scale_Y = values[Matrix.MSCALE_Y];
                mCanvas.save();
                mCanvas.translate(differenceX, differenceY);
                mCanvas.scale(scale_X, scale_Y);

                Paint paint = new Paint();
                if (((ScaleBarItem) item).getColor() == Color.WHITE) {
                    paint.setColor(Color.WHITE);
                } else if (((ScaleBarItem) item).getColor() == Color.BLACK) {
                    paint.setColor(Color.BLACK);
                }
                ;

                mCanvas.drawRect(((ScaleBarItem) item).getLine(), paint);

                mCanvas.restore();
            }
        }

        // force to draw the view
        mImageView.invalidate();
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
}
