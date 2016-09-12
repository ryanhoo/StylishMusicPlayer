package io.github.ryanhoo.music.ui.music;

import io.github.ryanhoo.music.RxBus;
import io.github.ryanhoo.music.data.model.Song;
import io.github.ryanhoo.music.data.source.AppRepository;
import io.github.ryanhoo.music.event.FavoriteChangeEvent;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/12/16
 * Time: 8:30 AM
 * Desc: MusicPlayerPresenter
 */
public class MusicPlayerPresenter implements MusicPlayerContract.Presenter {

    private MusicPlayerContract.View mView;
    private AppRepository mRepository;
    private CompositeSubscription mSubscriptions;

    public MusicPlayerPresenter(AppRepository repository, MusicPlayerContract.View view) {
        mView = view;
        mRepository = repository;
        mSubscriptions = new CompositeSubscription();
        mView.setPresenter(this);
    }

    @Override
    public void subscribe() {
        // TODO
        // - load last play list/folder/song
        // - last play mode
    }

    @Override
    public void unsubscribe() {
        mView = null;
        mSubscriptions.clear();
    }

    @Override
    public void setSongAsFavorite(Song song, boolean favorite) {
        Subscription subscription = mRepository.setSongAsFavorite(song, favorite)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Song>() {
                    @Override
                    public void onCompleted() {
                        // Empty
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.handleError(e);
                    }

                    @Override
                    public void onNext(Song song) {
                        mView.onSongSetAsFavorite(song);
                        RxBus.getInstance().post(new FavoriteChangeEvent(song));
                    }
                });
        mSubscriptions.add(subscription);
    }
}
