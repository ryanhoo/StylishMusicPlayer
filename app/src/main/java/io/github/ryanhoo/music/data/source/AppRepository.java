package io.github.ryanhoo.music.data.source;

import io.github.ryanhoo.music.Injection;
import io.github.ryanhoo.music.data.model.PlayList;
import io.github.ryanhoo.music.data.source.db.LiteOrmHelper;
import rx.Observable;

import java.util.List;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/10/16
 * Time: 4:17 PM
 * Desc: AppRepository
 */
public class AppRepository implements AppContract {

    private static volatile AppRepository sInstance;

    private AppContract.Local mLocalDataSource;

    private AppRepository() {
        mLocalDataSource = new AppLocalDataSource(Injection.provideContext(), LiteOrmHelper.getInstance());
    }

    public static AppRepository getInstance() {
        if (sInstance == null) {
            synchronized (AppRepository.class) {
                if (sInstance == null) {
                    sInstance = new AppRepository();
                }
            }
        }
        return sInstance;
    }

    // Play List

    @Override
    public Observable<List<PlayList>> playLists() {
        return mLocalDataSource.playLists();
    }

    @Override
    public Observable<Boolean> create(PlayList playList) {
        return mLocalDataSource.create(playList);
    }

    @Override
    public Observable<Boolean> update(PlayList playList) {
        return mLocalDataSource.update(playList);
    }

    @Override
    public Observable<Boolean> delete(PlayList playList) {
        return mLocalDataSource.delete(playList);
    }
}
