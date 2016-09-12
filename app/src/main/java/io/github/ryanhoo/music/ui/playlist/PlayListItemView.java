package io.github.ryanhoo.music.ui.playlist;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.ryanhoo.music.R;
import io.github.ryanhoo.music.data.model.PlayList;
import io.github.ryanhoo.music.ui.base.adapter.IAdapterView;
import io.github.ryanhoo.music.utils.ViewUtils;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/9/16
 * Time: 10:03 PM
 * Desc: PlayListItemView
 */
public class PlayListItemView extends RelativeLayout implements IAdapterView<PlayList> {

    @BindView(R.id.image_view_album)
    ImageView imageViewAlbum;
    @BindView(R.id.text_view_name)
    TextView textViewName;
    @BindView(R.id.text_view_info)
    TextView textViewInfo;
    @BindView(R.id.layout_action)
    View buttonAction;

    public PlayListItemView(Context context) {
        super(context);
        View.inflate(context, R.layout.item_play_list, this);
        ButterKnife.bind(this);
    }

    @Override
    public void bind(PlayList item, int position) {
        if (item.isFavorite()) {
            imageViewAlbum.setImageResource(R.drawable.ic_favorite_yes);
        } else {
            imageViewAlbum.setImageDrawable(ViewUtils.generateAlbumDrawable(getContext(), item.getName()));
        }
        textViewName.setText(item.getName());
        textViewInfo.setText(getResources().getQuantityString(
                R.plurals.mp_play_list_items_formatter, item.getItemCount(), item.getItemCount()));
    }
}
