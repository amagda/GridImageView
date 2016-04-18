package com.amagda.android.sample;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.util.AttributeSet;

/**
 * Displays set of an arbitrary image resources as grid
 * into the circular shape
 */
public class CircularGridImageView extends GridImageView {

    /**
     * Path for clipping result into the circular shape
     */
    private Path circlePathForClipping;

    public CircularGridImageView(Context context) {
        this(context, null);
    }

    public CircularGridImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircularGridImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.clipPath(getCirclePathForClipping());
        super.onDraw(canvas);
    }

    /**
     * Gets path for clipping image result into the circular shape
     */
    private Path getCirclePathForClipping() {
        if (circlePathForClipping == null) {
            circlePathForClipping = new Path();
            circlePathForClipping.addCircle(
                    (float) ((getWidth() / 2)),
                    (float) ((getHeight() / 2)),
                    (float) ((getWidth() - getPaddingLeft() - getPaddingRight()) / 2),
                    Path.Direction.CW);
        }
        return circlePathForClipping;
    }

    @Override
    protected void invalidateRectangles() {
        super.invalidateRectangles();
        circlePathForClipping = null;
    }
}
