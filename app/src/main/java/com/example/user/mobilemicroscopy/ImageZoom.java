// Some of this code was developed using pre-exiting code from Chintan Rathod, Jul 04 2013
// Alterations have been made to suit this app

// THIS CODE CREATES A NEW VIEW for the ZOOM FUNCTION

package com.example.user.mobilemicroscopy;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


/**
 * Class implementing the zoom
 */
public class ImageZoom extends AppCompatImageView
{
    Matrix matrix;
    int mode = 0;

    //Variables for zooming function
    PointF last = new PointF();
    PointF start = new PointF();
    float minScale = 1f;                //original size -> 1f = original image size (if number made smaller, image initially appear smaller
    float maxScale = 10f;               //max zoom -> higher number = bigger zoom (currently set at 10f)
    float[] m;
    int viewWidth, viewHeight;

    float saveScale = 1f;
    protected float originalWidth, originalHeight;
    int oldMeasuredWidth, oldMeasuredHeight;
    ScaleGestureDetector mScaleDetector;
    Context context;


    /**
     * Constructor
     *
     * @param context
     */
    public ImageZoom(Context context)
    {
        super(context);
        sharedConstructing(context);
    }

    /**
     * Constructor
     *
     * @param context
     * @param attrs
     */
    public ImageZoom(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        sharedConstructing(context);
    }


    /**
     * sharedConstructing method
     *
     * @param context
     */
    private void sharedConstructing(Context context)
    {
        super.setClickable(true);
        this.context = context;
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        matrix = new Matrix();
        m = new float[9];
        setImageMatrix(matrix);
        setScaleType(ScaleType.MATRIX);

        setOnTouchListener(new OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                mScaleDetector.onTouchEvent(motionEvent);
                PointF curr = new PointF(motionEvent.getX(), motionEvent.getY());

                switch (motionEvent.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        last.set(curr);
                        start.set(last);
                        mode = 1;
                        break;

                    case MotionEvent.ACTION_MOVE:
                        if (mode == 1)
                        {
                            float positionX = curr.x - last.x;
                            float positionY = curr.y - last.y;
                            float fixTransX = getFixDragTrans(positionX, viewWidth, originalWidth * saveScale);
                            float fixTransY = getFixDragTrans(positionY, viewHeight, originalHeight * saveScale);
                            matrix.postTranslate(fixTransX, fixTransY);
                            fixTrans();
                            last.set(curr.x, curr.y);
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        mode = 0;
                        int xDiff = (int) Math.abs(curr.x - start.x);
                        int yDiff = (int) Math.abs(curr.y - start.y);
                        if (xDiff < 3 && yDiff < 3)
                            performClick();
                        break;

                    case MotionEvent.ACTION_POINTER_UP:
                        mode = 0;
                        break;
                }

                setImageMatrix(matrix);
                invalidate();
                return true; // indicate event was handled
            }
        });
    }

    /**
     * ScaleListener inner class
     */
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener
    {

        //Responds to the beginning of a scaling gesture. Reported by new pointers going down
        //In this case mode = 2 (Which is for ZOOM action)

        /**
         * onScaleBegin method
         *
         * @param scaleGestureDetector
         * @return
         */
        @Override
        public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector)
        {
            mode = 2;
            return true;
        }

        /**
         * onScale method
         *
         * @param scaleGestureDetector
         * @return
         */
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector)
        {
            float mScaleFactor = scaleGestureDetector.getScaleFactor();
            float origScale = saveScale;
            saveScale *= mScaleFactor;

            if (saveScale > maxScale)
            {
                saveScale = maxScale;
                mScaleFactor = maxScale / origScale;
            }
            else if (saveScale < minScale)
            {
                saveScale = minScale;
                mScaleFactor = minScale / origScale;
            }

            if (originalWidth * saveScale <= viewWidth || originalHeight * saveScale <= viewHeight)
                matrix.postScale(mScaleFactor, mScaleFactor, viewWidth / 2, viewHeight / 2);
            else
                matrix.postScale(mScaleFactor, mScaleFactor, scaleGestureDetector.getFocusX(), scaleGestureDetector.getFocusY());

            fixTrans();

            return true;
        }
    }

    /**
     * fixTrans method
     */
    void fixTrans()
    {
        matrix.getValues(m);
        float transX = m[Matrix.MTRANS_X];
        float transY = m[Matrix.MTRANS_Y];
        float fixTransX = getFixTrans(transX, viewWidth, originalWidth * saveScale);
        float fixTransY = getFixTrans(transY, viewHeight, originalHeight * saveScale);

        if (fixTransX != 0 || fixTransY != 0)
            matrix.postTranslate(fixTransX, fixTransY);
    }


    /**
     * getFixTrans method
     *
     * @param trans
     * @param viewSize
     * @param contentSize
     * @return
     */
    float getFixTrans(float trans, float viewSize, float contentSize) {

        float minTrans, maxTrans;

        if (contentSize <= viewSize) {
            minTrans = 0;
            maxTrans = viewSize - contentSize;
        }
        else
        {
            minTrans = viewSize - contentSize;
            maxTrans = 0;
        }

        if (trans < minTrans)
            return -trans + minTrans;

        if (trans > maxTrans)
            return -trans + maxTrans;

        return 0;
    }

    /**
     * getFixDragTrans method
     *
     * @param delta
     * @param viewSize
     * @param contentSize
     * @return
     */
    float getFixDragTrans(float delta, float viewSize, float contentSize)
    {
        if (contentSize <= viewSize)
        {
            return 0;
        }
        return delta;
    }

    /**
     * onMeasure method
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        viewHeight = MeasureSpec.getSize(heightMeasureSpec);
        oldMeasuredHeight = viewHeight;
        oldMeasuredWidth = viewWidth;

        if (saveScale == 1)
        {

            //Fit image to screen

            float scale;
            Drawable drawable = getDrawable();

            if (drawable == null || drawable.getIntrinsicWidth() == 0 || drawable.getIntrinsicHeight() == 0)
                return;

            int bmWidth = drawable.getIntrinsicWidth();
            int bmHeight = drawable.getIntrinsicHeight();

            Log.d("bmSize", "bmWidth: " + bmWidth + " bmHeight : " + bmHeight);

            float scaleX = (float) viewWidth / (float) bmWidth;
            float scaleY = (float) viewHeight / (float) bmHeight;

            scale = Math.min(scaleX, scaleY);
            matrix.setScale(scale, scale);


            // Center image on screen

            float redundantYSpace = (float) viewHeight - (scale * (float) bmHeight);
            float redundantXSpace = (float) viewWidth - (scale * (float) bmWidth);

            redundantYSpace /= (float) 2;
            redundantXSpace /= (float) 2;

            matrix.postTranslate(redundantXSpace, redundantYSpace);
            originalWidth = viewWidth - 2 * redundantXSpace;
            originalHeight = viewHeight - 2 * redundantYSpace;

            setImageMatrix(matrix);
        }

        fixTrans();

    }
}
