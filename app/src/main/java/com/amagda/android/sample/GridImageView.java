package com.amagda.android.sample;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Displays set of an arbitrary image resources as grid into the rectangle shape.
 * Image loading processes based on the third party library Picasso
 * (http://square.github.io/picasso/)
 */
public class GridImageView extends ImageView {

    /**
     * Max available images count for displaying
     */
    private static final int MAX_IMG_COUNT = 4;

    /**
     * Rectangle with specific coordinates for the single image
     * with full-parent-width and full-parent-height sizes
     */
    private Rect fullRect;

    /**
     * Rectangle with specific coordinates for the left side image
     * with half-parent-width and full-parent-height sizes
     */
    private Rect leftRect;

    /**
     * Rectangle with specific coordinates for the right side image
     * with half-parent-width and full-parent-height sizes
     */
    private Rect rightRect;

    /**
     * Rectangle with specific coordinates for the left top side image
     * with half-parent-width and half-parent-height sizes
     */
    private Rect leftTopRect;

    /**
     * Rectangle with specific coordinates for the left bottom side image
     * with half-parent-width and half-parent-height sizes
     */
    private Rect leftBottomRect;

    /**
     * Rectangle with specific coordinates for the right top side image
     * with half-parent-width and half-parent-height sizes
     */
    private Rect rightTopRect;

    /**
     * Rectangle with specific coordinates for the right bottom side image
     * with half-parent-width and half-parent-height sizes
     */
    private Rect rightBottomRect;

    /**
     * Rectangle with specific coordinates for clipping unnecessary part
     * of the right side image which draws into the {@link #rightRect}
     */
    private Rect rightRectForClipping;

    /**
     * Rectangle with specific coordinates for clipping unnecessary part
     * of the left side image which draws into the {@link #leftRect}
     */
    private Rect leftRectForClipping;

    /**
     * Padding between images. You can customize this property
     * into layout via {@code padding_btw_images} attribute
     */
    private int paddingBtwImages;

    /**
     * Array of image paths required for loading and displaying.
     * Each path may be a remote URL,
     * file resource (prefixed with {@code file:}),
     * content resource(prefixed with {@code content:}),
     * android resource (prefixed with {@code android.resource:}
     */
    private String[] imgPaths;

    /**
     * Array of already loaded and ready for displaying bitmaps
     */
    private Bitmap[] imgBitmaps;

    /**
     * Array of image loading listeners which required for manipulating loading process
     *
     * @see com.squareup.picasso.Target
     */
    private Target[] imgLoadingListeners;


    public GridImageView(Context context) {
        this(context, null);
    }

    public GridImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GridImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CircularGridImageView, 0, 0);
        try {
            paddingBtwImages = a.getDimensionPixelOffset(R.styleable.CircularGridImageView_padding_btw_images, 0);
            if (paddingBtwImages > 0) {
                paddingBtwImages /= 2;
            }
        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        invalidateRectangles();
    }

    @Override
    public void setImageResource(int resId) {
        setImagePaths(null);
        super.setImageResource(resId);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        setImagePaths(null);
        super.setImageDrawable(drawable);
    }

    @Override
    public void setImageURI(Uri uri) {
        setImagePaths(null);
        super.setImageURI(uri);
    }

    /**
     * Resets current drawn state and all active image loading processes,
     * then tries to load and redraw images by paths if {@param imagePaths} is not empty
     *
     * @param imagePaths Array of image paths required for loading and displaying.
     *                   May be {@code null} if you need to invalidate current drawn state.
     *                   Each path may be a remote URL,
     *                   file resource (prefixed with {@code file:}),
     *                   content resource(prefixed with {@code content:}),
     *                   android resource (prefixed with {@code android.resource:}
     */
    public void setImagePaths(String... imagePaths) {
        invalidateImageLoadingListeners();
        invalidateImageBitmaps();
        invalidateImagePaths();
        super.setImageDrawable(null);
        if (imagePaths != null && imagePaths.length > 0) {
            imgPaths = imagePaths;
            imgBitmaps = new Bitmap[getImagesCount()];
            loadImages();
        }
    }

    /**
     * Gets available images count for displaying
     *
     * @return current {@link #imgPaths} size if lower or the same as max available count,
     * {@link #MAX_IMG_COUNT} otherwise
     */
    private int getImagesCount() {
        return imgPaths.length >= MAX_IMG_COUNT ? MAX_IMG_COUNT : imgPaths.length;
    }


    //----------DRAWING----------//

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (hasDataForDrawing()) {
            if (imgBitmaps.length == 1) {
                drawAtCenter(canvas, imgBitmaps[0]);
            } else if (imgBitmaps.length == 2) {
                drawOnLeft(canvas, imgBitmaps[0]);
                drawOnRight(canvas, imgBitmaps[1]);
            } else if (imgBitmaps.length == 3) {
                drawOnLeft(canvas, imgBitmaps[0]);
                drawOnRightTop(canvas, imgBitmaps[1]);
                drawOnRightBottom(canvas, imgBitmaps[2]);
            } else if (imgBitmaps.length >= MAX_IMG_COUNT) {
                drawOnLeftTop(canvas, imgBitmaps[0]);
                drawOnRightTop(canvas, imgBitmaps[1]);
                drawOnRightBottom(canvas, imgBitmaps[2]);
                drawOnLeftBottom(canvas, imgBitmaps[3]);
            }
        }
    }

    private boolean hasDataForDrawing() {
        return imgBitmaps != null && imgBitmaps.length > 0;
    }

    /**
     * Draws loaded image at the center.
     * Width and height are same as parents
     */
    private void drawAtCenter(Canvas canvas, Bitmap bitmap) {
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, null, getFullRect(), null);
        }
    }

    /**
     * Draws loaded image on the left side
     * with half-width and full-height of parent's sizes
     */
    private void drawOnLeft(Canvas canvas, Bitmap bitmap) {
        if (bitmap != null) {
            canvas.save();
            canvas.clipRect(getLeftRectForClipping());
            canvas.drawBitmap(bitmap, null, getLeftRect(), null);
            canvas.restore();
        }
    }

    /**
     * Draws loaded image on the right side
     * with half-width and full-height of parent's sizes
     */
    private void drawOnRight(Canvas canvas, Bitmap bitmap) {
        if (bitmap != null) {
            canvas.save();
            canvas.clipRect(getRightRectForClipping());
            canvas.drawBitmap(bitmap, null, getRightRect(), null);
            canvas.restore();
        }
    }

    /**
     * Draws loaded image on the left top side
     * with half-width and half-height of parent's sizes
     */
    private void drawOnLeftTop(Canvas canvas, Bitmap bitmap) {
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, null, getLeftTopRect(), null);
        }
    }

    /**
     * Draws loaded image on the left bottom side
     * with half-width and half-height of parent's sizes
     */
    private void drawOnLeftBottom(Canvas canvas, Bitmap bitmap) {
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, null, getLeftBottomRect(), null);
        }
    }

    /**
     * Draws loaded image on the right top side
     * with half-width and half-height of parent's sizes
     */
    private void drawOnRightTop(Canvas canvas, Bitmap bitmap) {
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, null, getRightTopRect(), null);
        }
    }

    /**
     * Draws loaded image on the right bottom side
     * with half width and height of parent's sizes
     */
    private void drawOnRightBottom(Canvas canvas, Bitmap bitmap) {
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, null, getRightBottomRect(), null);
        }
    }

    /**
     * Gets specific rectangle for the image
     * with full-parent-width and full-parent-height sizes
     */
    private Rect getFullRect() {
        if (fullRect == null) {
            fullRect = new Rect(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
        }
        return fullRect;
    }

    /**
     * Gets specific rectangle for the left side image
     * with half-parent-width and full-parent-height sizes
     */
    private Rect getLeftRect() {
        if (leftRect == null) {
            leftRect = new Rect(
                    -getWidth() / 2 + getPaddingLeft() - paddingBtwImages, getPaddingTop(),
                    getWidth() / 2 + getPaddingRight() - paddingBtwImages, getHeight() - getPaddingBottom());
            leftRect.offset(getWidth() / 4 - paddingBtwImages, 0);
        }
        return leftRect;
    }

    /**
     * Gets specific rectangle for the right side image
     * with half-parent-width and full-parent-height sizes
     */
    private Rect getRightRect() {
        if (rightRect == null) {
            rightRect = new Rect(
                    getWidth() / 2 + getPaddingLeft() + paddingBtwImages, getPaddingTop(),
                    (getWidth() + getWidth() / 2) - getPaddingRight() + paddingBtwImages, getHeight() - getPaddingBottom());
            rightRect.offset(-getWidth() / 4 + paddingBtwImages, 0);
        }
        return rightRect;
    }

    /**
     * Gets specific rectangle for the left top side image
     * with half-parent-width and half-parent-height sizes
     */
    private Rect getLeftTopRect() {
        if (leftTopRect == null) {
            leftTopRect = new Rect(
                    getPaddingLeft() - paddingBtwImages, getPaddingTop() - paddingBtwImages,
                    getWidth() / 2 - paddingBtwImages, getHeight() / 2 - paddingBtwImages);
        }
        return leftTopRect;
    }

    /**
     * Gets specific rectangle for the left bottom side image
     * with half-parent-width and half-parent-height sizes
     */
    private Rect getLeftBottomRect() {
        if (leftBottomRect == null) {
            leftBottomRect = new Rect(
                    getPaddingLeft() - paddingBtwImages, getHeight() / 2 + paddingBtwImages,
                    getWidth() / 2 - paddingBtwImages, getHeight() - getPaddingBottom() + paddingBtwImages);
        }
        return leftBottomRect;
    }

    /**
     * Gets specific rectangle for the right top side image
     * with half-parent-width and half-parent-height sizes
     */
    private Rect getRightTopRect() {
        if (rightTopRect == null) {
            rightTopRect = new Rect(
                    getWidth() / 2 + paddingBtwImages, getPaddingTop() - paddingBtwImages,
                    getWidth() - getPaddingRight() + paddingBtwImages, getHeight() / 2 - paddingBtwImages);
        }
        return rightTopRect;
    }

    /**
     * Gets specific rectangle for the right bottom side image
     * with half-parent-width and half-parent-height sizes
     */
    private Rect getRightBottomRect() {
        if (rightBottomRect == null) {
            rightBottomRect = new Rect(
                    getWidth() / 2 + paddingBtwImages, getHeight() / 2 + paddingBtwImages,
                    getWidth() - getPaddingRight() + paddingBtwImages, getHeight() - getPaddingBottom() + paddingBtwImages);
        }
        return rightBottomRect;
    }

    /**
     * Gets specific rectangle for clipping unnecessary part
     * of the left side image which draws into the {@link #leftRect} rectangle
     */
    private Rect getLeftRectForClipping() {
        if (leftRectForClipping == null) {
            leftRectForClipping = new Rect(0, 0, getWidth() / 2 - paddingBtwImages, getHeight());
        }
        return leftRectForClipping;
    }

    /**
     * Gets specific rectangle for clipping unnecessary part
     * of the right side image which draws into the {@link #rightRect} rectangle
     */
    private Rect getRightRectForClipping() {
        if (rightRectForClipping == null) {
            rightRectForClipping = new Rect(getWidth() / 2 + paddingBtwImages, 0, getWidth(), getHeight());
        }
        return rightRectForClipping;
    }


    //----------IMAGE LOADING----------//

    /**
     * Starts image loading processes
     */
    private void loadImages() {
        int imagesCount = getImagesCount();
        imgLoadingListeners = new Target[imagesCount];
        for (int i = 0; i < imagesCount; i++) {
            final int _i = i;
            Target imgLoadingListener = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    onSingleImageLoaded(bitmap, _i);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                }
            };
            imgLoadingListeners[i] = imgLoadingListener;
            Picasso.with(getContext()).load(imgPaths[i]).into(imgLoadingListener);
        }
    }

    /**
     * Stores specific image loading result and starts redrawing process
     *
     * @param bitmap     image loading result
     * @param imageQueue index of loaded image into the {@link #imgBitmaps} array
     */
    private void onSingleImageLoaded(Bitmap bitmap, int imageQueue) {
        imgBitmaps[imageQueue] = bitmap;
        invalidate();
    }


    //----------INVALIDATION----------//

    /**
     * Invalidates image paths
     */
    private void invalidateImagePaths() {
        imgPaths = null;
    }

    /**
     * Invalidates loaded images
     */
    private void invalidateImageBitmaps() {
        imgBitmaps = null;
    }

    /**
     * Cancels all active image loading processes and
     * invalidates image loading listeners
     */
    private void invalidateImageLoadingListeners() {
        if (imgLoadingListeners != null) {
            for (Target listener : imgLoadingListeners) {
                Picasso.with(getContext()).cancelRequest(listener);
            }
        }
        imgLoadingListeners = null;
    }

    /**
     * Invalidates rectangles for recalculating their coordinates
     * at the next drawing time
     */
    protected void invalidateRectangles() {
        fullRect = null;
        leftRect = null;
        rightRect = null;
        leftTopRect = null;
        leftBottomRect = null;
        rightTopRect = null;
        rightBottomRect = null;
        leftRectForClipping = null;
        rightRectForClipping = null;
    }
}
