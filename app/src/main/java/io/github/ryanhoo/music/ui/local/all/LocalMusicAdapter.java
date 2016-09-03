package io.github.ryanhoo.music.ui.local.all;

import android.content.Context;
import io.github.ryanhoo.music.data.model.Music;
import io.github.ryanhoo.music.ui.base.adapter.ListAdapter;
import io.github.ryanhoo.music.ui.widget.RecyclerViewFastScroller;

import java.util.List;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/2/16
 * Time: 8:21 PM
 * Desc: LocalMusicAdapter
 */
public class LocalMusicAdapter extends ListAdapter<Music, LocalMusicItemView> implements RecyclerViewFastScroller.BubbleTextGetter {

    public LocalMusicAdapter(Context context, List<Music> data) {
        super(context, data);
    }

    @Override
    protected LocalMusicItemView createView(Context context) {
        return new LocalMusicItemView(context);
    }

    @Override
    public String getTextToShowInBubble(int position) {
        return getItem(position).getDisplayName().substring(0, 1);
    }
}
