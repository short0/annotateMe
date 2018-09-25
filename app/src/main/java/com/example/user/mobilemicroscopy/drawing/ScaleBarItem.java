package com.example.user.mobilemicroscopy.drawing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.View;

import com.example.user.mobilemicroscopy.R;

/**
 * Class for a scale bar
 */
public class ScaleBarItem extends DrawingItem {
    /**
     * Default button size
     */
    private static final float BUTTON_SIZE = 100;

    /**
     * Extra space for selection rectangle
     */
    private static final float EXTRA_SPACE = 50;

    /**
     * Default height
     */
    private static final float DEFAULT_HEIGHT = 20;

    private float originalWidth;

    // initialize only once
    /**
     * Hold the bitmap of delete button
     */
    private static Bitmap deleteBitmap;

    /**
     * Hold the matrix to do manipulation
     */
    private Matrix matrix;

    /**
     * Hold the line
     */
    private RectF line;

    /**
     * Hold area of the selection rectangle
     */
    private RectF rectangle;

    /**
     * Hold area of the delete button
     */
    private RectF deleteRectangle;

    /**
     * Hold the state of being selected
     */
    private boolean haveButtons = false;

    /**
     * Color of arrow to select which image to be used
     */
    private int color = -1;

    /**
     * Constructor
     *
     * @param context
     * @param color
     */
    public ScaleBarItem(Context context, int color) {
        this.color = color;

        // initialize only once
        if (deleteBitmap == null) {
            deleteBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_delete);
        }

    }

    /**
     * Constructor
     *
     * @param context
     */
    public ScaleBarItem(Context context) {

    }

    /**
     * Accessor method
     */
    public int getColor() {
        return color;
    }

    /**
     * Get Line
     */
    public RectF getLine() {
        return line;
    }

    /**
     * Accessor method
     */
    public RectF getRectangle() {
        return rectangle;
    }

    /**
     * Accessor method
     */
    public RectF getDeleteRectangle() {
        return deleteRectangle;
    }

    /**
     * Mutator method
     */
    public void setHaveButtons(boolean haveButtons) {
        this.haveButtons = haveButtons;
    }

    /**
     * Initialize the scale bar when first created
     */
    public void initialize(View parentView) {
        float inches = 0.3937f; // convert 1cm to inches

        // calculate the dots
        float xdpi = parentView.getResources().getDisplayMetrics().xdpi;
        float xDots = inches * xdpi;

        haveButtons = true;

        float width = xDots;
        float height = DEFAULT_HEIGHT;

        // calculate the position of rectangle
        float left = parentView.getWidth() / 2;
        float top = parentView.getHeight() / 2;
        float right = left + width;
        float bottom = top + height;

        // create rectangles for buttons
        line = new RectF(left, top, left + width, top + height);
        rectangle = new RectF(left, top, left + width, top + EXTRA_SPACE); // make invisible box bigger to select
        deleteRectangle = new RectF(left - BUTTON_SIZE, top - BUTTON_SIZE, left, top);

        originalWidth = rectangle.width();

        matrix = new Matrix();

        // move the scale bar to position
        matrix.postTranslate(rectangle.left, rectangle.top);

        Log.d("aaaaaaaaaaaaaaaaaaaa", "" + width + " " + height + " " + left + " " + top);
    }

    /**
     * Draw the scale bar on specified canvas when called
     */
    public void draw(Canvas canvas) {
        canvas.save();

        if (haveButtons) {
            canvas.drawBitmap(deleteBitmap, null, deleteRectangle, null);
        }

        Paint paint = new Paint();
        paint.setColor(color);
        canvas.drawRect(line, paint);

        canvas.restore();
    }

    /**
     * Update position when moved
     */
    public void updatePosition(float dx, float dy) {
        matrix.postTranslate(dx, dy);

        // move the buttons along
        rectangle.offset(dx, dy);
        deleteRectangle.offset(dx, dy);

        line.offset(dx, dy);
    }

}
