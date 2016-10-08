package io.github.ryanhoo.music.ui.common;

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
public class DefaultDividerDecoration extends RecyclerView.ItemDecoration {

    private static final int DIVIDER_HEIGHT = 1; // 1 pixel

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = DIVIDER_HEIGHT;
    }
}
