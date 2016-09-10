package io.github.ryanhoo.music.ui.details;

import android.content.Context;
import io.github.ryanhoo.music.R;
import io.github.ryanhoo.music.data.model.Song;
import io.github.ryanhoo.music.ui.common.AbstractSummaryAdapter;

import java.util.List;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/11/16
 * Time: 6:41 AM
 * Desc: SongAdapter
 */
public class SongAdapter extends AbstractSummaryAdapter<Song, SongItemView> {

    private Context mContext;

    public SongAdapter(Context context, List<Song> data) {
        super(context, data);
        mContext = context;
    }

    @Override
    protected String getEndSummaryText(int dataCount) {
        return mContext.getString(R.string.mp_play_list_details_footer_end_summary_formatter, dataCount);
    }

    @Override
    protected SongItemView createView(Context context) {
        return new SongItemView(context);
    }
}
