package com.example.user.testingapp;

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

public class DrawingItem {

    public DrawingItem() {

    }

    public DrawingItem(Context context) {

    }

    public Matrix getMatrix() {
        return null;
    }

    public int getColor()
    {
        return -1;
    }

    public float getTextSize() {
        return 0;
    }

    public String getText() {
        return null;
    }

    public RectF getLine() {
        return null;
    }

    public RectF getRectangle() {
        return null;
    }

    public RectF getDeleteRectangle() {
        return null;
    }

    public RectF getScaleRectangle() {
        return null;
    }

    public RectF getRotateRectangle() {
        return null;
    }

    public void setHaveButtons(boolean haveButtons) {

    }

    public void initialize(View parentView) {

    }


    public void draw(Canvas canvas) {

    }

    public void updatePosition(float dx, float dy) {

    }

    public void updateScalePosition(float dx, float dy) {

    }

    public void updateRotatePosition(float oldX, float oldY, float dx, float dy) {

    }
}

