package io.github.ryanhoo.music.ui.widget;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import io.github.ryanhoo.music.BuildConfig;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/6/16
 * Time: 11:39 PM
 * Desc: AlbumImageView
 * Referenced {@link android.support.v4.widget.SwipeRefreshLayout}'s implementation.
 */
public class AlbumImageView extends ImageView {

    // private static final String TAG = "AlbumImageView";

    private static final int KEY_SHADOW_COLOR = 0x1E000000;
    private static final int FILL_SHADOW_COLOR = 0x3D000000;

    private static final float X_OFFSET = 0f;
    private static final float Y_OFFSET = 1.75f;

    private static final float SHADOW_RADIUS = 24f;
    private static final int SHADOW_ELEVATION = 16;

    private static final int DEFAULT_ALBUM_COLOR = 0xFF3C5F78;
    private static final int MIDDLE_RECT_COLOR = 0xFF4C718C;
    private static final int INNER_RECT_COLOR = 0x4FD8D8D8;

    private static final int ALBUM_CIRCLE_TEXT_COLOR = 0xFF9CBDCC;

    private static final float ALBUM_CIRCLE_TEXT_SIZE = 3.5f;
    private static final float ALBUM_CIRCLE_TEXT_SIZE_SMALL = 2f;

    private static final int MIDDLE_RECT_SIZE = 80;
    private static final int INNER_RECT_SIZE = 64;
    private static final int ALBUM_TEXT_PATH_RECT_SIZE = 56;
    private int mShadowRadius;

    Paint mPaint = new Paint();
    RectF mMiddleRect = new RectF();
    RectF mInnerRect = new RectF();
    RectF mAlbumPathRect = new RectF();
    Path mAlbumTextPath = new Path();

    float mDensity;

    private static final String ALBUM_TEXT = "MUSIC PLAYER MUSIC PLAYER MUSIC PLAYER MUSIC PLAYER MUSIC PLAYER MUSIC PLAYER MUSIC PLAYER MUSIC PLAYER ";
    private static final String APP_NAME = "Mini Player";
    private static final String APP_SLOGAN = "Make music simpler";
    private static final String COPY_RIGHT = "Ryan Hoo ©2016";
    @SuppressLint("DefaultLocale")
    private static final String BUILD = String.format("build release %s-%d (%s)",
            BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE, BuildConfig.FLAVOR);

    // Animation
    private ObjectAnimator mRotateAnimator;
    private long mLastAnimationValue;

    public AlbumImageView(Context context) {
        this(context, null);
    }

    public AlbumImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AlbumImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressWarnings("unused")
    public AlbumImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mDensity = getContext().getResources().getDisplayMetrics().density;
        final int shadowXOffset = (int) (mDensity * X_OFFSET);
        final int shadowYOffset = (int) (mDensity * Y_OFFSET);

        mShadowRadius = (int) (mDensity * SHADOW_RADIUS);

        ShapeDrawable circle;
        if (elevationSupported()) {
            circle = new ShapeDrawable(new OvalShape());
            ViewCompat.setElevation(this, SHADOW_ELEVATION * mDensity);
        } else {
            OvalShape oval = new OvalShadow(mShadowRadius);
            circle = new ShapeDrawable(oval);
            ViewCompat.setLayerType(this, ViewCompat.LAYER_TYPE_SOFTWARE, circle.getPaint());
            circle.getPaint().setShadowLayer(mShadowRadius, shadowXOffset, shadowYOffset, KEY_SHADOW_COLOR);
            final int padding = mShadowRadius;
            // set padding so the inner image sits correctly within the shadow.
            setPadding(padding, padding, padding, padding);
        }
        circle.getPaint().setAntiAlias(true);
        circle.getPaint().setColor(DEFAULT_ALBUM_COLOR);
        setBackground(circle);

        mPaint.setAntiAlias(true);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(DEFAULT_ALBUM_COLOR);
        mPaint.setTextSize(ALBUM_CIRCLE_TEXT_SIZE * mDensity);

        mRotateAnimator = ObjectAnimator.ofFloat(this, "rotation", 0f, 360f);
        mRotateAnimator.setDuration(3600);
        mRotateAnimator.setInterpolator(new LinearInterpolator());
        mRotateAnimator.setRepeatMode(ValueAnimator.RESTART);
        mRotateAnimator.setRepeatCount(ValueAnimator.INFINITE);
    }

    private boolean elevationSupported() {
        return android.os.Build.VERSION.SDK_INT >= 21;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!elevationSupported()) {
            setMeasuredDimension(getMeasuredWidth() + mShadowRadius * 2, getMeasuredHeight() + mShadowRadius * 2);
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(MIDDLE_RECT_COLOR);
        canvas.drawOval(mMiddleRect, mPaint);
        mPaint.setColor(INNER_RECT_COLOR);
        canvas.drawOval(mInnerRect, mPaint);

        mPaint.setTextSize(ALBUM_CIRCLE_TEXT_SIZE * mDensity);
        mPaint.setColor(ALBUM_CIRCLE_TEXT_COLOR);
        canvas.drawTextOnPath(ALBUM_TEXT, mAlbumTextPath, 2 * mDensity, 2 * mDensity, mPaint);

        mPaint.setTextSize(ALBUM_CIRCLE_TEXT_SIZE_SMALL * mDensity);
        canvas.drawText(APP_NAME, getWidth() / 2, getHeight() / 2, mPaint);
        canvas.drawText(APP_SLOGAN, getWidth() / 2, getHeight() / 2 + 4 * mDensity, mPaint);
        canvas.drawText(BUILD, getWidth() / 2, getHeight() / 2 + 8 * mDensity, mPaint);
        canvas.drawText(COPY_RIGHT, getWidth() / 2, getHeight() / 2 + 12 * mDensity, mPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        final float middleRectSize = mDensity * MIDDLE_RECT_SIZE;
        final float innerRectSize = mDensity * INNER_RECT_SIZE;
        final float albumRectSize = mDensity * ALBUM_TEXT_PATH_RECT_SIZE;
        mMiddleRect.set(0, 0, middleRectSize, middleRectSize);
        mInnerRect.set(0, 0, innerRectSize, innerRectSize);
        mAlbumPathRect.set(0, 0, albumRectSize, albumRectSize);

        mMiddleRect.offset(w / 2 - middleRectSize / 2, h / 2 - middleRectSize / 2);
        mInnerRect.offset(w / 2 - innerRectSize / 2, h / 2 - innerRectSize / 2);
        mAlbumPathRect.offset(w / 2 - albumRectSize / 2, h / 2 - albumRectSize / 2);

        mAlbumTextPath.addOval(mAlbumPathRect, Path.Direction.CW);
    }

    // Animation

    public void startRotateAnimation() {
        mRotateAnimator.cancel();
        mRotateAnimator.start();
    }

    public void cancelRotateAnimation() {
        mLastAnimationValue = 0;
        mRotateAnimator.cancel();
    }

    public void pauseRotateAnimation() {
        mLastAnimationValue = mRotateAnimator.getCurrentPlayTime();
        mRotateAnimator.cancel();
    }

    public void resumeRotateAnimation() {
        mRotateAnimator.start();
        mRotateAnimator.setCurrentPlayTime(mLastAnimationValue);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mRotateAnimator != null) {
            mRotateAnimator.cancel();
            mRotateAnimator = null;
        }
    }

    /**
     * Draw oval shadow below ImageView under lollipop.
     */
    private class OvalShadow extends OvalShape {
        private RadialGradient mRadialGradient;
        private Paint mShadowPaint;

        OvalShadow(int shadowRadius) {
            super();
            mShadowPaint = new Paint();
            mShadowRadius = shadowRadius;
            updateRadialGradient((int) rect().width());
        }

        @Override
        protected void onResize(float width, float height) {
            super.onResize(width, height);
            updateRadialGradient((int) width);
        }

        @Override
        public void draw(Canvas canvas, Paint paint) {
            final int viewWidth = AlbumImageView.this.getWidth();
            final int viewHeight = AlbumImageView.this.getHeight();
            canvas.drawCircle(viewWidth / 2, viewHeight / 2, viewWidth / 2, mShadowPaint);
            canvas.drawCircle(viewWidth / 2, viewHeight / 2, viewWidth / 2 - mShadowRadius, paint);
        }

        private void updateRadialGradient(int diameter) {
            mRadialGradient = new RadialGradient(diameter / 2, diameter / 2,
                    mShadowRadius, new int[]{FILL_SHADOW_COLOR, Color.TRANSPARENT},
                    null, Shader.TileMode.CLAMP);
            mShadowPaint.setShader(mRadialGradient);
        }
    }
}
