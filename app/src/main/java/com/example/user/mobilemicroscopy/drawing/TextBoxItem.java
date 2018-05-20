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

public class TextBoxItem extends DrawingItem{
    private static final float BUTTON_SIZE = 100;

    private static final float DEFAULT_TEXT_SIZE = 50;

    private static final float DEFAULT_HEIGHT = 50;

    private float textSize = DEFAULT_TEXT_SIZE;

    private String text;

    // initialize only once
    private static Bitmap deleteBitmap;
    private static Bitmap scaleBitmap;

    private Matrix matrix;

    private RectF rectangle;

    private RectF deleteRectangle;

    private RectF scaleRectangle;

    private boolean haveButtons = false;

    private Paint mPaint;

    private int color = -1;

    public TextBoxItem(Context context, String text, int color)
    {
        if (deleteBitmap == null) {
            deleteBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_delete);
        }

        if (scaleBitmap == null) {
            scaleBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_scale);
        }

        mPaint = new Paint();
        if (color == MainActivity.COLOR_WHITE)
        {
            this.color = color;
            mPaint.setColor(Color.WHITE);
        }
        else if (color == MainActivity.COLOR_BLACK)
        {
            this.color = color;
            mPaint.setColor(Color.BLACK);
        }

        this.text = text;
    }

    public TextBoxItem(Context context)
    {

    }

    public int getColor()
    {
        return color;
    }

    public float getTextSize() {
        return textSize;
    }

    public String getText() {
        return text;
    }

    public Matrix getMatrix()
    {
        return matrix;
    }

    public RectF getRectangle()
    {
        return rectangle;
    }

    public RectF getDeleteRectangle() {
        return deleteRectangle;
    }

    public RectF getScaleRectangle() {
        return scaleRectangle;
    }

    public void setHaveButtons(boolean haveButtons)
    {
        this.haveButtons = haveButtons;
    }

    public void initialize(View parentView)
    {
        haveButtons = true;

        Paint paint = new Paint();
        paint.setTextSize(textSize);

        float width = paint.measureText(text);
        float height = DEFAULT_HEIGHT;
//        Log.d("ttttttttttttttttt", width + " " + paint.measureText(text));

        float left = (parentView.getWidth() - width) / 2;
        float top = (parentView.getHeight() - height) / 2;
        float right = left + width;
        float bottom = top + height;

        rectangle = new RectF(left, top, left + width, top + height);
        deleteRectangle = new RectF(left - BUTTON_SIZE, top - BUTTON_SIZE, left, top);
        scaleRectangle = new RectF(right, top - BUTTON_SIZE, right + BUTTON_SIZE, top);

        matrix = new Matrix();

        matrix.postTranslate(rectangle.left, rectangle.top);

        Log.d("aaaaaaaaaaaaaaaaaaaa", "" + width + " " + height + " " + left + " " + top);
    }

    public void initializeForScaleBar(View parentView)
    {
        haveButtons = true;

        Paint paint = new Paint();
        paint.setTextSize(textSize);

        float width = paint.measureText(text);
        float height = DEFAULT_HEIGHT;
//        Log.d("ttttttttttttttttt", width + " " + paint.measureText(text));

        float left = parentView.getWidth() - 200;
        float top = parentView.getHeight() - 270;
        float right = left + width;
        float bottom = top + height;

        rectangle = new RectF(left, top, left + width, top + height);
        deleteRectangle = new RectF(left - BUTTON_SIZE, top - BUTTON_SIZE, left, top);
        scaleRectangle = new RectF(right, top - BUTTON_SIZE, right + BUTTON_SIZE, top);
//        rotateRectangle = new RectF(right, bottom, right + 100, bottom + 100);

        matrix = new Matrix();

        matrix.postTranslate(rectangle.left, rectangle.top);

        Log.d("aaaaaaaaaaaaaaaaaaaa", "" + width + " " + height + " " + left + " " + top);
    }

    public void draw(Canvas canvas)
    {
        canvas.save();

//        Paint paint = new Paint();
        if (haveButtons)
        {

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

    public void updatePosition(float dx, float dy)
    {
        matrix.postTranslate(dx, dy);

        rectangle.offset(dx, dy);
        deleteRectangle.offset(dx, dy);
        scaleRectangle.offset(dx, dy);
    }

    public void updateScalePosition(float dx, float dy)
    {
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
