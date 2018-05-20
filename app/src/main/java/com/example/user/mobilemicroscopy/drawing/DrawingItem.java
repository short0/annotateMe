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

/**
 * Parent class of ArrowItem, TextBoxItem and ScaleBarItem (only for convenience)
 */
public class DrawingItem {

    /**
     * Empty constructor
     */
    public DrawingItem() {

    }

    /**
     * Constructor
     */
    public DrawingItem(Context context) {

    }

    /**
     * Accessor method
     */
    public Matrix getMatrix() {
        return null;
    }

    /**
     * Accessor method
     */
    public int getColor() {
        return -1;
    }

    /**
     * Accessor method
     */
    public float getTextSize() {
        return 0;
    }

    /**
     * Accessor method
     */
    public String getText() {
        return null;
    }

    /**
     * Accessor method
     */
    public RectF getLine() {
        return null;
    }

    /**
     * Accessor method
     */
    public RectF getRectangle() {
        return null;
    }

    /**
     * Accessor method
     */
    public RectF getDeleteRectangle() {
        return null;
    }

    /**
     * Accessor method
     */
    public RectF getScaleRectangle() {
        return null;
    }

    /**
     * Accessor method
     */
    public RectF getRotateRectangle() {
        return null;
    }

    /**
     * Mutator method
     */
    public void setHaveButtons(boolean haveButtons) {

    }

    /**
     * Initialize item when first created
     */
    public void initialize(View parentView) {

    }

    /**
     * Draw on canvas
     */
    public void draw(Canvas canvas) {

    }

    /**
     * Update position when moved
     */
    public void updatePosition(float dx, float dy) {

    }

    /**
     * Update position when scaled
     */
    public void updateScalePosition(float dx, float dy) {

    }

    /**
     * Update position when rotated
     */
    public void updateRotatePosition(float oldX, float oldY, float dx, float dy) {

    }
}

