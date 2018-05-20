package com.example.user.mobilemicroscopy.drawing;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class DrawingView extends View {

    private static final int MOVING = 0;

    private static final int DELETING = 1;

    private static final int SCALING = 2;

    private static final int ROTATING = 3;

    private static final int FREE = 4;

    private int status = -1;

    private ArrayList<DrawingItem> drawingItemList = new ArrayList<DrawingItem>();

    private float oldX, oldY;

    private DrawingItem currentItem;

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawingView(Context context) {
        super(context);
    }

    public ArrayList<DrawingItem> getDrawingItemList() {
        return drawingItemList;
    }

    public void addArrow(int color) {
        ArrowItem arrowItem = new ArrowItem(getContext(), color);
        arrowItem.initialize(this);
        drawingItemList.add(arrowItem);
        invalidate();
    }

    public void addText(String text, int color) {
        TextBoxItem textBoxItem = new TextBoxItem(getContext(), text, color);
        textBoxItem.initialize(this);
        drawingItemList.add(textBoxItem);
        invalidate();
    }

    public void addScaleBar(int color) {
        ScaleBarItem scaleBarItem = new ScaleBarItem(getContext(), color);
        scaleBarItem.initialize(this);
        drawingItemList.add(scaleBarItem);
        invalidate();
    }

    public void addTextForScaleBar(String text, int color) {
        TextBoxItem textBoxItem = new TextBoxItem(getContext(), text, color);
        textBoxItem.initializeForScaleBar(this);
        drawingItemList.add(textBoxItem);
        invalidate();
    }

    public void clear() {
        drawingItemList.clear();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (DrawingItem item : drawingItemList) {
            item.draw(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = super.onTouchEvent(event);

        int id = -1;

        int action = event.getAction();

        float x = event.getX();
        float y = event.getY();

        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:

                for (DrawingItem item : drawingItemList) {
                    if (item.getDeleteRectangle().contains(x, y)) {
                        status = DELETING;
                        id = drawingItemList.indexOf(item);
                    }

                    if (item instanceof ArrowItem || item instanceof TextBoxItem) {
                        if (item.getScaleRectangle().contains(x, y)) {
                            result = true;
                            currentItem = item;
                            status = SCALING;
                        }
                    }

                    if (item instanceof ArrowItem) {
                        if (item.getRotateRectangle().contains(x, y)) {
                            result = true;
                            currentItem = item;
                            status = ROTATING;
                        }
                    }

                    if (item.getRectangle().contains(x, y)) {
                        result = true;
                        if (currentItem != null) {
                            currentItem.setHaveButtons(false);
                        }
                        currentItem = item;
                        currentItem.setHaveButtons(true);
                        oldX = x;
                        oldY = y;
                        status = MOVING;
                    }


                }
                if (currentItem != null && status == FREE) {
                    currentItem.setHaveButtons(false);
                    currentItem = null;
                    invalidate();
                }

                if (status == DELETING) {
                    if (id != -1) {
                        drawingItemList.remove(id);
                    }
                    status = FREE;
                    invalidate();
                }

                Log.d("aaaaaaaaaaaaaaaaaaaaaa", "" + x + " " + y);
                Log.d("aaaaaaaaaaaaaaaaaaaaaa", "" + currentItem);
//                return true;
                break;

            case MotionEvent.ACTION_MOVE:
                result = true;

                if (status == MOVING) {
                    float differenceX = x - oldX;
                    float differenceY = y - oldY;

                    if (currentItem != null) {
                        currentItem.updatePosition(differenceX, differenceY);
                        invalidate();
                    }

                    oldX = x;
                    oldY = y;

                    Log.d("aaaaaaaaaaaaaaaaaaaaaa", "" + status + " " + differenceX + " " + differenceY);
                } else if (status == SCALING) {
                    float differenceX = x - oldX;
                    float differenceY = y - oldY;

                    if (currentItem instanceof ArrowItem || currentItem instanceof TextBoxItem) {
                        if (currentItem != null) {
                            currentItem.updateScalePosition(differenceX, differenceY);
                            invalidate();
                        }
                    }

                    oldX = x;
                    oldY = y;
                    Log.d("aaaaaaaaaaaaaaaaaaaaaa", "" + status);
                } else if (status == ROTATING) {
                    float differenceX = x - oldX;
                    float differenceY = y - oldY;

                    if (currentItem instanceof ArrowItem) {
                        if (currentItem != null) {
                            currentItem.updateRotatePosition(x, y, differenceX, differenceY);
                            invalidate();
                        }
                    }

                    oldX = x;
                    oldY = y;
                    Log.d("aaaaaaaaaaaaaaaaaaaaaa", "" + status);
                }
//                return true;
                break;

            case MotionEvent.ACTION_UP:
                result = false;
//                currentItem = null;
                status = FREE;
//                return false;
                break;
        }

        return result;
    }
}
