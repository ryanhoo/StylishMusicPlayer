package io.github.ryanhoo.music.ui.music;

import io.github.ryanhoo.music.data.model.Song;
import io.github.ryanhoo.music.ui.base.BasePresenter;
import io.github.ryanhoo.music.ui.base.BaseView;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/12/16
 * Time: 8:27 AM
 * Desc: MusicPlayerContract
 */
/* package */ interface MusicPlayerContract {

    interface View extends BaseView<Presenter> {

        void handleError(Throwable error);

        void onSongSetAsFavorite(Song song);
    }

    interface Presenter extends BasePresenter {

        void setSongAsFavorite(Song song, boolean favorite);
    }
}
