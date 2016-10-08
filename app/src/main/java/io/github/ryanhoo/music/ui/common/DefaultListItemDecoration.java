package io.github.ryanhoo.music.ui.common;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/2/16
 * Time: 6:15 PM
 * Desc: DefaultListItemDecoration
 */
public class DefaultListItemDecoration extends RecyclerView.ItemDecoration {

    private static final int DIVIDER_HEIGHT = 1; // 1 pixel
    private static final int BACKGROUND_COLOR = 0x1FDDF9FF;

    Paint mPaint;

    public DefaultListItemDecoration() {
        mPaint = new Paint();
        mPaint.setColor(BACKGROUND_COLOR);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        Rect bounds = new Rect();
        for (int i = 0; i < parent.getChildCount(); i++) {
            View itemView = parent.getChildAt(i);
            bounds.top = itemView.getTop();
            bounds.left = itemView.getLeft();
            bounds.right = itemView.getRight();
            bounds.bottom = itemView.getBottom();

            c.drawRect(bounds, mPaint);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = DIVIDER_HEIGHT;
    }
}
