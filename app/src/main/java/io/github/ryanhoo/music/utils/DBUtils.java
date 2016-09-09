package io.github.ryanhoo.music.utils;

import android.content.Context;
import io.github.ryanhoo.music.R;
import io.github.ryanhoo.music.data.model.PlayList;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/9/16
 * Time: 10:27 PM
 * Desc: DBUtils
 */
public class DBUtils {

    public static PlayList generateFavoritePlayList(Context context) {
        PlayList favorite = new PlayList();
        favorite.setId(PlayList.FAVORIT_PLAY_LIST_ID);
        favorite.setName(context.getString(R.string.mp_play_list_favorite));
        return favorite;
    }
}
