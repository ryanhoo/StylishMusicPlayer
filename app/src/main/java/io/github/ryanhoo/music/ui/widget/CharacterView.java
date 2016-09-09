package io.github.ryanhoo.music.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.Dimension;
import android.util.AttributeSet;
import android.view.View;
import io.github.ryanhoo.music.R;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 7/16/16
 * Time: 6:51 AM
 * Desc: CharacterView
 */
public class CharacterView extends View {

    private static final String DEFAULT_CHARACTER = "C";

    // private static final int DEFAULT_TEXT_COLOR = Color.parseColor("#CCCCCC");
    // private static final int DEFAULT_BACKGROUND_COLOR = Color.parseColor("#EEEEEE");

    private String mCharacter;
    @ColorInt
    private int mCharacterTextColor;
    private boolean mBackgroundRoundAsCircle;
    @ColorInt
    private int mBackgroundColor;
    @Dimension
    private float mBackgroundRadius;
    @Dimension
    float mCharacterPadding;

    // Paint mPaint = new Paint();
    // Path mClipPath = new Path();
    // RectF mBackgroundRect = new RectF();

    CharacterDrawable mDrawable;

    public CharacterView(Context context) {
        this(context, null);
    }

    public CharacterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CharacterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CharacterView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.CharacterView,
                defStyleAttr,
                defStyleRes
        );
        try {
            mCharacter = typedArray.getString(R.styleable.CharacterView_character);
            mCharacterTextColor = typedArray.getColor(R.styleable.CharacterView_characterTextColor, 0);
            mBackgroundRoundAsCircle = typedArray.getBoolean(R.styleable.CharacterView_backgroundRoundAsCircle, false);
            mBackgroundColor = typedArray.getColor(R.styleable.CharacterView_backgroundColor, 0);
            mBackgroundRadius = typedArray.getDimension(R.styleable.CharacterView_backgroundRadius, 0);
            mCharacterPadding = typedArray.getDimension(R.styleable.CharacterView_characterPadding, 0);

            mDrawable = new CharacterDrawable.Builder()
                    .setCharacter(mCharacter)
                    .setCharacterTextColor(mCharacterTextColor)
                    .setBackgroundRoundAsCircle(mBackgroundRoundAsCircle)
                    .setBackgroundColor(mBackgroundColor)
                    .setBackgroundRadius(mBackgroundRadius)
                    .setCharacterPadding(mCharacterPadding)
                    .build();
        } finally {
            typedArray.recycle();
        }
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mDrawable.setBounds(0, 0, getWidth(), getHeight());
        mDrawable.draw(canvas);
        /*
        mPaint.setAntiAlias(true);

        // Draw background
        mPaint.setColor(mBackgroundColor);
        mBackgroundRect.set(0, 0, getHeight(), getHeight());

        if (mBackgroundRoundAsCircle) {
            canvas.drawOval(mBackgroundRect, mPaint);
            mClipPath.addOval(mBackgroundRect, Path.Direction.CW);
        } else {
            canvas.drawRoundRect(mBackgroundRect, mBackgroundRadius, mBackgroundRadius, mPaint);
            mClipPath.addRoundRect(mBackgroundRect, mBackgroundRadius, mBackgroundRadius, Path.Direction.CW);
        }
        canvas.clipPath(mClipPath);

        // Draw text in the center of the canvas
        mPaint.setColor(mCharacterTextColor);
        mPaint.setTextSize(getHeight() - mCharacterPadding * 2);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTypeface(Typeface.DEFAULT);

        //获取paint中的字体信息  setTextSize 要在他前面
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        // 计算文字高度baseline
        float textBaseY = getHeight() - fontMetrics.bottom / 2 - mCharacterPadding;
        //获取字体的长度
        float fontWidth = mPaint.measureText(mCharacter);
        //计算文字长度的baseline
        float textBaseX = (getWidth() - fontWidth) / 2;
        canvas.drawText(mCharacter, textBaseX, textBaseY, mPaint);
        */
    }

    // Getters & Setters

    public String getCharacter() {
        return mCharacter;
    }

    public void setCharacter(String character) {
        mDrawable.setCharacter(character);
    }

    public int getCharacterTextColor() {
        return mCharacterTextColor;
    }

    public void setCharacterTextColor(int characterTextColor) {
        mDrawable.setCharacterTextColor(characterTextColor);
    }

    public boolean isBackgroundRoundAsCircle() {
        return mBackgroundRoundAsCircle;
    }

    public void setBackgroundRoundAsCircle(boolean backgroundRoundAsCircle) {
        mDrawable.setBackgroundRoundAsCircle(backgroundRoundAsCircle);
    }

    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        mDrawable.setBackgroundColor(backgroundColor);
    }

    public float getBackgroundRadius() {
        return mBackgroundRadius;
    }

    public void setBackgroundRadius(float backgroundRadius) {
        mDrawable.setBackgroundRadius(backgroundRadius);
    }

    public float getCharacterPadding() {
        return mCharacterPadding;
    }

    public void setCharacterPadding(float characterPadding) {
        mDrawable.setCharacterPadding(characterPadding);
    }
}
