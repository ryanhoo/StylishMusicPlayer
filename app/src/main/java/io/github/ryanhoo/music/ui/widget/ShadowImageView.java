package io.github.ryanhoo.music.ui.widget;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
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

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/8/16
 * Time: 4:23 PM
 * Desc: ShadowImageView
 * Stole from {@link android.support.v4.widget.SwipeRefreshLayout}'s implementation to display beautiful shadow
 * for circle ImageView.
 */
public class ShadowImageView extends ImageView {

    private static final int KEY_SHADOW_COLOR = 0x1E000000;
    private static final int FILL_SHADOW_COLOR = 0x3D000000;

    private static final float X_OFFSET = 0f;
    private static final float Y_OFFSET = 1.75f;

    private static final float SHADOW_RADIUS = 24f;
    private static final int SHADOW_ELEVATION = 16;

    private static final int DEFAULT_BACKGROUND_COLOR = 0xFF3C5F78;

    private int mShadowRadius;

    // Animation
    private ObjectAnimator mRotateAnimator;
    private long mLastAnimationValue;

    public ShadowImageView(Context context) {
        this(context, null);
    }

    public ShadowImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShadowImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressWarnings("unused")
    public ShadowImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        final float density = getContext().getResources().getDisplayMetrics().density;
        final int shadowXOffset = (int) (density * X_OFFSET);
        final int shadowYOffset = (int) (density * Y_OFFSET);

        mShadowRadius = (int) (density * SHADOW_RADIUS);

        ShapeDrawable circle;
        if (elevationSupported()) {
            circle = new ShapeDrawable(new OvalShape());
            ViewCompat.setElevation(this, SHADOW_ELEVATION * density);
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
        circle.getPaint().setColor(DEFAULT_BACKGROUND_COLOR);
        setBackground(circle);

        mRotateAnimator = ObjectAnimator.ofFloat(this, "rotation", 0f, 360f);
        mRotateAnimator.setDuration(7200);
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
            final int viewWidth = ShadowImageView.this.getWidth();
            final int viewHeight = ShadowImageView.this.getHeight();
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
