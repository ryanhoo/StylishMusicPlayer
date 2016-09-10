package io.github.ryanhoo.music.data.source;

import android.content.Context;
import android.util.Log;
import com.litesuits.orm.LiteOrm;
import io.github.ryanhoo.music.data.model.PlayList;
import io.github.ryanhoo.music.utils.DBUtils;
import rx.Observable;
import rx.Subscriber;

import java.util.List;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/10/16
 * Time: 4:54 PM
 * Desc: AppLocalDataSource
 */
/* package */ class AppLocalDataSource implements AppContract.Local {

    private static final String TAG = "AppLocalDataSource";

    private Context mContext;
    private LiteOrm mLiteOrm;

    public AppLocalDataSource(Context context, LiteOrm orm) {
        mContext = context;
        mLiteOrm = orm;
    }

    // Play List

    @Override
    public Observable<List<PlayList>> playLists() {
        return Observable.create(new Observable.OnSubscribe<List<PlayList>>() {
            @Override
            public void call(Subscriber<? super List<PlayList>> subscriber) {
                List<PlayList> playLists = mLiteOrm.query(PlayList.class);
                if (playLists.isEmpty()) {
                    // First query, create the default play list
                    PlayList playList = DBUtils.generateFavoritePlayList(mContext);
                    long result = mLiteOrm.save(playList);
                    Log.d(TAG, "Create default playlist(Favorite) with " + (result == 1 ? "success" : "failure"));
                    playLists.add(playList);
                }
                subscriber.onNext(playLists);
                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> create(final PlayList playList) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                long result = mLiteOrm.save(playList);
                subscriber.onNext(result > 0);
                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> update(final PlayList playList) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                long result = mLiteOrm.update(playList);
                subscriber.onNext(result > 0);
                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> delete(final PlayList playList) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                long result = mLiteOrm.delete(playList);
                subscriber.onNext(result > 0);
                subscriber.onCompleted();
            }
        });
    }
}
