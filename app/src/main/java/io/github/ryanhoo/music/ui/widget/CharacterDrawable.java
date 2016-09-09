package io.github.ryanhoo.music.ui.widget;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DimenRes;
import android.support.annotation.Dimension;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 7/16/16
 * Time: 6:59 AM
 * Desc: CharacterDrawable
 */
public class CharacterDrawable extends Drawable {

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

    Paint mPaint = new Paint();
    Path mClipPath = new Path();
    RectF mBackgroundRect = new RectF();

    int mWidth, mHeight;

    private CharacterDrawable() {
        // Avoid direct instantiate
    }

    @Override
    public void draw(Canvas canvas) {
        mWidth = getBounds().right - getBounds().left;
        mHeight = getBounds().bottom - getBounds().top;

        mPaint.setAntiAlias(true);

        // Draw background
        mPaint.setColor(mBackgroundColor);
        mBackgroundRect.set(0, 0, mWidth, mHeight);

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
        mPaint.setTextSize(mHeight - mCharacterPadding * 2);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTypeface(Typeface.DEFAULT);

        if (mCharacter != null) {
            //获取paint中的字体信息  setTextSize 要在他前面
            Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
            // 计算文字高度baseline
            float textBaseY = mHeight - fontMetrics.bottom / 2 - mCharacterPadding;
            //获取字体的长度
            float fontWidth = mPaint.measureText(mCharacter);
            //计算文字长度的baseline
            float textBaseX = (mWidth - fontWidth) / 2;
            canvas.drawText(mCharacter, textBaseX, textBaseY, mPaint);
        }
        // Clip the circle path
        // http://stackoverflow.com/a/22829656/2290191
        // canvas.drawPath(mClipPath, mPaint);
    }

    @Override
    public void setAlpha(int i) {
        // TODO
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        // TODO
    }

    @Override
    public int getOpacity() {
        // TODO
        return 0;
    }

    // Getters & Setters

    public String getCharacter() {
        return mCharacter;
    }

    public void setCharacter(String character) {
        this.mCharacter = character;
    }

    public int getCharacterTextColor() {
        return mCharacterTextColor;
    }

    public void setCharacterTextColor(int characterTextColor) {
        this.mCharacterTextColor = characterTextColor;
    }

    public boolean isBackgroundRoundAsCircle() {
        return mBackgroundRoundAsCircle;
    }

    public void setBackgroundRoundAsCircle(boolean backgroundRoundAsCircle) {
        this.mBackgroundRoundAsCircle = backgroundRoundAsCircle;
    }

    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.mBackgroundColor = backgroundColor;
    }

    public float getBackgroundRadius() {
        return mBackgroundRadius;
    }

    public void setBackgroundRadius(float backgroundRadius) {
        this.mBackgroundRadius = backgroundRadius;
    }

    public float getCharacterPadding() {
        return mCharacterPadding;
    }

    public void setCharacterPadding(float characterPadding) {
        this.mCharacterPadding = characterPadding;
    }

    public static class Builder {

        private static final int DEFAULT_TEXT_COLOR = Color.parseColor("#CCCCCC");
        private static final int DEFAULT_BACKGROUND_COLOR = Color.parseColor("#EEEEEE");

        private String character;

        @ColorInt
        private int characterTextColor = DEFAULT_TEXT_COLOR;

        private boolean backgroundRoundAsCircle;
        @ColorInt
        private int backgroundColor = DEFAULT_BACKGROUND_COLOR;
        @Dimension
        private float backgroundRadius;

        @Dimension
        private float mCharacterPadding;

        public Builder applyStyle(int style) {

            return this;
        }

        public Builder setCharacter(char character) {
            this.character = String.valueOf(character);
            return this;
        }

        public Builder setCharacter(String character) {
            this.character = character;
            return this;
        }

        public Builder setCharacterTextColor(@ColorInt int textColor) {
            this.characterTextColor = textColor;
            return this;
        }

        public Builder setBackgroundRoundAsCircle(boolean roundAsCircle) {
            this.backgroundRoundAsCircle = roundAsCircle;
            return this;
        }

        public Builder setBackgroundColor(@ColorInt int backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        public Builder setBackgroundRadius(@Dimension float backgroundRadius) {
            this.backgroundRadius = backgroundRadius;
            return this;
        }

        public Builder setCharacterPadding(@Dimension float padding) {
            this.mCharacterPadding = padding;
            return this;
        }

        public CharacterDrawable build() {
            CharacterDrawable drawable = new CharacterDrawable();
            drawable.setCharacter(character);
            drawable.setCharacterTextColor(characterTextColor);
            drawable.setBackgroundRoundAsCircle(backgroundRoundAsCircle);
            drawable.setBackgroundColor(backgroundColor);
            drawable.setBackgroundRadius(backgroundRadius);
            drawable.setCharacterPadding(mCharacterPadding);
            return drawable;
        }
    }

    public static CharacterDrawable create(Context context, char character, boolean roundAsCircle, @DimenRes int padding) {
        return new CharacterDrawable.Builder()
                .setCharacter(character)
                .setBackgroundRoundAsCircle(roundAsCircle)
                .setCharacterPadding(context.getResources().getDimensionPixelSize(padding))
                .build();
    }
}
