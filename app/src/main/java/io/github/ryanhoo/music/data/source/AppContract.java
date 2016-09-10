package io.github.ryanhoo.music.data.source;

import io.github.ryanhoo.music.data.model.PlayList;
import rx.Observable;

import java.util.List;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/10/16
 * Time: 4:52 PM
 * Desc: AppContract
 */
/* package */ interface AppContract {

    interface Local {

        // Play List

        Observable<List<PlayList>> playLists();

        Observable<Boolean> create(PlayList playList);

        Observable<Boolean> update(PlayList playList);

        Observable<Boolean> delete(PlayList playList);

        // Song

        // Folder

    }

    // Play List

    Observable<List<PlayList>> playLists();

    Observable<Boolean> create(PlayList playList);

    Observable<Boolean> update(PlayList playList);

    Observable<Boolean> delete(PlayList playList);

}
