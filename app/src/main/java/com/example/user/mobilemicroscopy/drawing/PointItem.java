package com.example.user.mobilemicroscopy.drawing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.Log;
import android.view.View;

import com.example.user.mobilemicroscopy.R;

public class PointItem extends DrawingItem {
    /**
     * Default button size
     */
    private static final float BUTTON_SIZE = 100;

    /**
     * Hold original with of arrow
     */
    private float originalWidth;

    private float rotateAngle = 0;

    // initialize only once
    /**
     * Hold the bitmap of actual arrow
     */
    private Bitmap pointBitmap;

    /**
     * Hold the bitmap of delete button
     */
    private static Bitmap deleteBitmap;

    /**
     * Hold the bitmap of scale button
     */
    private static Bitmap scaleBitmap;

    /**
     * Hold the bitmap of rotate button
     */
    private static Bitmap rotateBitmap;

    /**
     * Hold the matrix to do manipulation
     */
    private Matrix matrix;

    /**
     * Hold area of the selection rectangle
     */
    public RectF rectangle;

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
     */
    public PointItem(Context context) {
        this.color = color;

        // Initialize required bitmap only once
        if (pointBitmap == null) {
            pointBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.crop_box);
        }

        if (deleteBitmap == null) {
            deleteBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_delete);
        }
    }

    /**
     * Accessor method
     */
    public int getColor() {
        return color;
    }

    /**
     * Accessor method
     */
    public Matrix getMatrix() {
        return matrix;
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
     * Initialize the point when first created
     */
    public void initialize(View parentView) {
        haveButtons = true;

        // scale the point down
        float width = pointBitmap.getWidth();
        float height = pointBitmap.getHeight();
        Log.d(getClass().getName(), width + " " + height);

        // calculate the position of rectangle
        float left = (parentView.getWidth() - width) / 2;
        float top = (parentView.getHeight() - height) / 2;
        float right = left + width;
        float bottom = top + height;

        // create rectangles for buttons
        rectangle = new RectF(left, top, left + width, top + height);
        deleteRectangle = new RectF(left - BUTTON_SIZE, top - BUTTON_SIZE, left, top);

        // get the original width
        originalWidth = rectangle.width();

        // create new matrix
        matrix = new Matrix();

        // move the point to position
        matrix.postTranslate(rectangle.left, rectangle.top);

        Log.d(getClass().getName(), "" + width + " " + height + " " + left + " " + top);
    }

    /**
     * Draw the point on specified canvas when called
     */
    public void draw(Canvas canvas) {
        canvas.save();

        if (haveButtons) {
            canvas.drawBitmap(deleteBitmap, null, deleteRectangle, null);
        }

        canvas.drawBitmap(pointBitmap, matrix, null);

        canvas.restore();
    }

    /**
     * Update position when moved
     */
    public void updatePosition(float dx, float dy) {
        matrix.postTranslate(dx, dy);

        rectangle.offset(dx, dy);
        deleteRectangle.offset(dx, dy);
    }

}
