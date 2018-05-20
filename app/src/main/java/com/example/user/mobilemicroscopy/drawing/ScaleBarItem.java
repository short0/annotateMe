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

public class ScaleBarItem extends DrawingItem{
    private static final float BUTTON_SIZE = 100;

    private static final float EXTRA_SPACE = 50;

    private static final float DEFAULT_HEIGHT = 20;

    // initialize only once
    private static Bitmap deleteBitmap;

    private Matrix matrix;

    private RectF line;

    private RectF rectangle;

    private RectF deleteRectangle;

    private RectF scaleRectangle;

    private RectF rotateRectangle;

    private boolean haveButtons = false;

    private int color = -1;

    public ScaleBarItem(Context context, int color)
    {
        this.color = color;

        if (deleteBitmap == null) {
            deleteBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_delete);
        }
    }

    public ScaleBarItem(Context context)
    {

    }

    public int getColor()
    {
        return color;
    }

    public RectF getLine()
    {
        return line;
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

    public RectF getRotateRectangle() {
        return rotateRectangle;
    }

    public void setHaveButtons(boolean haveButtons)
    {
        this.haveButtons = haveButtons;
    }

    public void initialize(View parentView)
    {
        float inches = 0.3937f; //convert 1cm to inches

        float xdpi = parentView.getResources().getDisplayMetrics().xdpi;
        float xDots = inches * xdpi;

        haveButtons = true;

        float width = xDots;
        float height = DEFAULT_HEIGHT;

        float left = parentView.getWidth() - 200;
        float top = parentView.getHeight() - 200;
        float right = left + width;
        float bottom = top + height;

        line = new RectF(left, top, left + width, top + height);
        rectangle = new RectF(left, top, left + width, top + EXTRA_SPACE); // make invisible box bigger to select
        deleteRectangle = new RectF(left - BUTTON_SIZE, top - BUTTON_SIZE, left, top);
        scaleRectangle = new RectF(right, top - BUTTON_SIZE, right + BUTTON_SIZE, top);
        rotateRectangle = new RectF(right, bottom, right + BUTTON_SIZE, bottom + BUTTON_SIZE);

        matrix = new Matrix();

        matrix.postTranslate(rectangle.left, rectangle.top);

        Log.d("aaaaaaaaaaaaaaaaaaaa", "" + width + " " + height + " " + left + " " + top);
    }

    public void draw(Canvas canvas)
    {
        canvas.save();

        if (haveButtons)
        {
//            Paint paint = new Paint();
//            paint.setColor(Color.WHITE);
//            canvas.drawRect(rectangle, paint);
//            paint.setColor(Color.RED);
//            canvas.drawRect(deleteRectangle, paint);

            canvas.drawBitmap(deleteBitmap, null, deleteRectangle, null);
        }

        Paint paint = new Paint();
        paint.setColor(color);
        canvas.drawRect(line, paint);

        canvas.restore();
    }

    public void updatePosition(float dx, float dy)
    {
        matrix.postTranslate(dx, dy);

        rectangle.offset(dx, dy);
        deleteRectangle.offset(dx, dy);

        line.offset(dx, dy);
    }
}
