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

import com.example.user.mobilemicroscopy.MainActivity;
import com.example.user.mobilemicroscopy.R;

/**
 * Class to for an text box
 */
public class TextBoxItem extends DrawingItem {
    /**
     * Default button size
     */
    private static final float BUTTON_SIZE = 100;

    /**
     * Default text size
     */
    private static final float DEFAULT_TEXT_SIZE = 50;

    /**
     * Deault height
     */
    private static final float DEFAULT_HEIGHT = 50;

    /**
     * Default text size
     */
    private float textSize = DEFAULT_TEXT_SIZE;

    /**
     * Text
     */
    private String text;

    // initialize only once
    /**
     * Hold the bitmap of delete button
     */
    private static Bitmap deleteBitmap;
    /**
     * Hold the bitmap of scale button
     */
    private static Bitmap scaleBitmap;

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
     * Hold the state of being selected
     */
    private boolean haveButtons = false;

    /**
     * Hold color and text size
     */
    private Paint mPaint;

    /**
     * Color of arrow to select which image to be used
     */
    private int color = -1;

    /**
     * Constructor
     *
     * @param context
     * @param text
     * @param color
     */
    public TextBoxItem(Context context, String text, int color) {
        // Initialize required bitmap only once
        if (deleteBitmap == null) {
            deleteBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_delete);
        }

        if (scaleBitmap == null) {
            scaleBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_scale);
        }

        this.color = color;
        this.text = text;
        mPaint = new Paint();
        mPaint.setColor(color);
    }

    /**
     * Constructor
     *
     * @param context
     */
    public TextBoxItem(Context context) {

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
    public float getTextSize() {
        return textSize;
    }

    /**
     * Accessor method
     */
    public String getText() {
        return text;
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
     * Mutator method
     */
    public void setHaveButtons(boolean haveButtons) {
        this.haveButtons = haveButtons;
    }

    /**
     * Initialize the text when first created
     */
    public void initialize(View parentView) {
        haveButtons = true;

        Paint paint = new Paint();
        paint.setTextSize(textSize);

        float width = paint.measureText(text);
        float height = DEFAULT_HEIGHT;
//        Log.d("ttttttttttttttttt", width + " " + paint.measureText(text));

        // calculate the position of rectangle
        float left = (parentView.getWidth() - width) / 2;
        float top = (parentView.getHeight() - height) / 2;
        float right = left + width;
        float bottom = top + height;

        // create rectangles for buttons
        rectangle = new RectF(left, top, left + width, top + height);
        deleteRectangle = new RectF(left - BUTTON_SIZE, top - BUTTON_SIZE, left, top);
        scaleRectangle = new RectF(right, top - BUTTON_SIZE, right + BUTTON_SIZE, top);

        matrix = new Matrix();

        // move the text to position
        matrix.postTranslate(rectangle.left, rectangle.top);

        Log.d("aaaaaaaaaaaaaaaaaaaa", "" + width + " " + height + " " + left + " " + top);
    }

    /**
     * Initialize the text for scale bar when first created
     */
    public void initializeForScaleBar(View parentView) {
        haveButtons = true;

        Paint paint = new Paint();
        paint.setTextSize(textSize);

        float width = paint.measureText(text);
        float height = DEFAULT_HEIGHT;
//        Log.d("ttttttttttttttttt", width + " " + paint.measureText(text));

        // calculate the position of rectangle
        float left = parentView.getWidth() - 200;
        float top = parentView.getHeight() - 270;
        float right = left + width;
        float bottom = top + height;

        // create rectangles for buttons
        rectangle = new RectF(left, top, left + width, top + height);
        deleteRectangle = new RectF(left - BUTTON_SIZE, top - BUTTON_SIZE, left, top);
        scaleRectangle = new RectF(right, top - BUTTON_SIZE, right + BUTTON_SIZE, top);
//        rotateRectangle = new RectF(right, bottom, right + 100, bottom + 100);

        matrix = new Matrix();

        // move the text to position
        matrix.postTranslate(rectangle.left, rectangle.top);

        Log.d("aaaaaaaaaaaaaaaaaaaa", "" + width + " " + height + " " + left + " " + top);
    }

    /**
     * Draw the text on specified canvas when called
     */
    public void draw(Canvas canvas) {
        canvas.save();

//        Paint paint = new Paint();
        if (haveButtons) {

//            paint.setColor(Color.WHITE);
//            canvas.drawRect(rectangle, paint); // draw back ground to see layout
//            paint.setColor(Color.RED);
//            canvas.drawRect(deleteRectangle, paint);
//            paint.setColor(Color.BLUE);
//            canvas.drawRect(scaleRectangle, paint);

            canvas.drawBitmap(deleteBitmap, null, deleteRectangle, null);
            canvas.drawBitmap(scaleBitmap, null, scaleRectangle, null);
        }

//        paint.setColor(Color.BLACK);
        mPaint.setTextSize(textSize);

        canvas.drawText(text, rectangle.left, rectangle.bottom, mPaint);

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
    }

    /**
     * Update position when scaled
     */
    public void updateScalePosition(float dx, float dy) {
        float centerX = rectangle.centerX();
        float centerY = rectangle.centerY();

        float width = rectangle.width();
        float height = rectangle.height();

        float h1 = (float) Math.sqrt((rectangle.right - centerX) * (rectangle.right - centerX) + (rectangle.top - centerY) * (rectangle.top - centerY));
        float h2 = (float) Math.sqrt((rectangle.right + dx - centerX) * (rectangle.right + dx - centerX) + (rectangle.top - dy - centerY) * (rectangle.top - dy - centerY));

        float scale = h2 / h1; // something not right, oposite with arrows
        if (scale < 0.1 || scale > 1.5) // safe guard for sudden scale
        {
            return;
        }
        Log.d("aaaaaaaaaaaaaaaaaaaaaas", "" + scale);

        float newTextSize = textSize * scale;
        if (newTextSize < 20 || newTextSize > 200) // don't let user scale too big or small
        {
            return;
        }

        matrix.postScale(scale, scale, rectangle.centerX(), rectangle.centerY());
        Log.d("aaaaaaaaaaaaaaaaaaaaaa", "" + width + " " + height);

        float newWidth = width * scale;
        float newHeight = height * scale;

        float differenceX = (newWidth - width) / 2;
        float differenceY = (newHeight - height) / 2;

        rectangle.left -= differenceX;
        rectangle.right += differenceX;
        rectangle.top -= differenceY;
        rectangle.bottom += differenceY;

        deleteRectangle.offset(-differenceX, -differenceY);
        scaleRectangle.offset(differenceX, -differenceY);

        textSize *= scale;
    }

}
