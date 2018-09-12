package com.example.user.mobilemicroscopy.drawing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.View;

import com.example.user.mobilemicroscopy.R;

/**
 * Class to for an arrow
 */
public class ArrowItem extends DrawingItem {
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
    private Bitmap arrowBitmap;
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
    private RectF rectangle;

    /**
     * Hold area of the delete button
     */
    private RectF deleteRectangle;

    /**
     * Hold area of the scale button
     */
    private RectF scaleRectangle;

    /**
     * Hold area of the rotate button
     */
    private RectF rotateRectangle;

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
    public ArrowItem(Context context) {

    }

    /**
     * Constructor
     *
     * @param context
     * @param color
     */
    public ArrowItem(Context context, int color) {
        this.color = color;

        // Initialize required bitmap only once
        if (arrowBitmap == null) {
            if (color == Color.WHITE) {
                arrowBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.white_arrow_with_tail);
            } else if (color == Color.BLACK) {
                arrowBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.black_arrow_with_tail);
            }
        }

        if (deleteBitmap == null) {
            deleteBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_delete);
        }

        if (scaleBitmap == null) {
            scaleBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_scale_vert);
        }

        if (rotateBitmap == null) {
            rotateBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_rotate);
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
     * Accessor method
     */
    public RectF getScaleRectangle() {
        return scaleRectangle;
    }

    /**
     * Accessor method
     */
    public RectF getRotateRectangle() {
        return rotateRectangle;
    }

    /**
     * Mutator method
     */
    public void setHaveButtons(boolean haveButtons) {
        this.haveButtons = haveButtons;
    }

    /**
     * Initialize the arrow when first created
     */
    public void initialize(View parentView) {
        haveButtons = true;

        // scale the arrow down
        float width = arrowBitmap.getWidth() / 4;
        float height = arrowBitmap.getHeight() / 4;
        Log.d("wwwwwwwwwwwwwhhhhhhhhh", width + " " + height);

        // calculate the position of rectangle
        float left = (parentView.getWidth() - width) / 2;
        float top = (parentView.getHeight() - height) / 2;
        float right = left + width;
        float bottom = top + height;

        // create rectangles for buttons
        rectangle = new RectF(left, top, left + width, top + height);
        deleteRectangle = new RectF(left - BUTTON_SIZE, top - BUTTON_SIZE, left, top);
        scaleRectangle = new RectF(right, top - BUTTON_SIZE, right + BUTTON_SIZE, top);
        rotateRectangle = new RectF(right, bottom, right + BUTTON_SIZE, bottom + BUTTON_SIZE);

        // get the original width
        originalWidth = rectangle.width();

        // create new matrix
        matrix = new Matrix();

        // scale the arrow down
        matrix.postScale(0.25f, 0.25f);

        // move the arrow to position
        matrix.postTranslate(rectangle.left, rectangle.top);

        Log.d("aaaaaaaaaaaaaaaaaaaa", "" + width + " " + height + " " + left + " " + top);
    }

    /**
     * Draw the arrow on specified canvas when called
     */
    public void draw(Canvas canvas) {
        canvas.save();

//        canvas.rotate(rotateAngle, rectangle.centerX(), rectangle.centerY());

        if (haveButtons) {
//            Paint paint = new Paint();
//            paint.setColor(Color.WHITE);
//            canvas.drawRect(rectangle, paint);
//            paint.setColor(Color.RED);
//            canvas.drawRect(deleteRectangle, paint);
//            paint.setColor(Color.BLUE);
//            canvas.drawRect(scaleRectangle, paint);
//            paint.setColor(Color.GREEN);
//            canvas.drawRect(rotateRectangle, paint);

            canvas.drawBitmap(deleteBitmap, null, deleteRectangle, null);
            canvas.drawBitmap(scaleBitmap, null, scaleRectangle, null);
            canvas.drawBitmap(rotateBitmap, null, rotateRectangle, null);
        }

        canvas.drawBitmap(arrowBitmap, matrix, null);
//        canvas.drawBitmap(arrowBitmap, null, rectangle, null);

        canvas.restore();
    }

    /**
     * Update position when moved
     */
    public void updatePosition(float dx, float dy) {
        matrix.postTranslate(dx, dy);

        rectangle.offset(dx, dy);
        deleteRectangle.offset(dx, dy);
        scaleRectangle.offset(dx, dy);
        rotateRectangle.offset(dx, dy);
    }

    /**
     * Update position when scaled
     */
    public void updateScalePosition(float dx, float dy) {

        float centerX = rectangle.centerX();
        float centerY = rectangle.centerY();

        float width = rectangle.width();
        float height = rectangle.height();

        // scale based on diagonal line
        float h1 = (float) Math.sqrt((rectangle.right - centerX) * (rectangle.right - centerX) + (rectangle.top - centerY) * (rectangle.top - centerY));
        float h2 = (float) Math.sqrt((rectangle.right + dx - centerX) * (rectangle.right + dx - centerX) + (rectangle.top - dy - centerY) * (rectangle.top - dy - centerY));

        float scale = h1 / h2;

        if (scale < 0.1 || scale > 1.5) // safe guard for sudden scale
        {
            return;
        }

        float ratio = width * scale / originalWidth;
        if (ratio < 0.5 || ratio > 3) // don't let user scale too small or too big
        {
            return;
        }

        Log.d("aaaaaaaaaaaaaaaaaaaaaas", "" + scale);

        // scale using centre point of the rectangle
        matrix.postScale(scale, scale, rectangle.centerX(), rectangle.centerY());
        Log.d("aaaaaaaaaaaaaaaaaaaaaa", "" + width + " " + height);

        float newWidth = width * scale;
        float newHeight = height * scale;

        float differenceX = (newWidth - width) / 2;
        float differenceY = (newHeight - height) / 2;

        // scale the rectangle
        rectangle.left -= differenceX;
        rectangle.right += differenceX;
        rectangle.top -= differenceY;
        rectangle.bottom += differenceY;

        // move the buttons along
        deleteRectangle.offset(-differenceX, -differenceY);
        scaleRectangle.offset(differenceX, -differenceY);
        rotateRectangle.offset(differenceX, differenceY);
    }

    /**
     * Update position when rotated
     */
    public void updateRotatePosition(float oldX, float oldY, float dx, float dy) {

        float centerX = rectangle.centerX();
        float centerY = rectangle.centerY();

//        float r1 = (float) Math.sqrt((rotateRectangle.left - centerX) * (rotateRectangle.left - centerX) + (rotateRectangle.top - centerY) * (rotateRectangle.top - centerY));
//        float temp1 = rotateRectangle.left - centerX;
//        float angle1 = (float) Math.toDegrees(Math.acos(temp1/r1));
//
//        float r2 = (float) Math.sqrt((oldX - centerX) * (oldX - centerX) + (oldY - centerY) * (oldY - centerY));
//        float temp2 = oldX - centerX;
//        float angle2 = (float) Math.toDegrees(Math.acos(temp2/r2));

        // calculate old and new angle using linear equation: y = mx +c
        float m1 = (rotateRectangle.top - centerY) / (rotateRectangle.left - centerX);
        float m2 = (oldY - centerY) / (oldX - centerX);

        float angle1 = (float) Math.toDegrees(Math.atan(m1));
        float angle2 = (float) Math.toDegrees(Math.atan(m2));

        float angle = angle2 - angle1;
        if (angle1 * angle2 < 0) {
            angle = angle2 + angle1;
        }

//        rotateAngle += angle;

        Log.d("aaaaaaaaaaaaaaaaaaaaaaa", "" + angle2 + " " + angle1 + " " + angle);

        // rotate the arrow using the center of rectangle
        matrix.postRotate(angle, rectangle.centerX(), rectangle.centerY());

        float sin = (float) Math.sin(Math.toRadians(angle));
        float cos = (float) Math.cos(Math.toRadians(angle));

//        rotateRectangle.offset(dx, dy);
        float x = rotateRectangle.centerX();
        float y = rotateRectangle.centerY();
        float newX = centerX + (x - centerX) * cos - (y - centerY) * sin;
        float newY = centerY + (y - centerY) * cos + (x - centerX) * sin;

        float dx1 = newX - x;
        float dy1 = newY - y;

        // move the buttons along
        rotateRectangle.offset(dx1, dy1);
        deleteRectangle.offset(-dx1, -dy1);
        scaleRectangle.offset(-dx1, -dy1);
    }
}

