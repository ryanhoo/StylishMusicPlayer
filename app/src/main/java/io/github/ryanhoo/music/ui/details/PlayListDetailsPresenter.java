package io.github.ryanhoo.music.ui.details;

import io.github.ryanhoo.music.RxBus;
import io.github.ryanhoo.music.data.model.PlayList;
import io.github.ryanhoo.music.data.model.Song;
import io.github.ryanhoo.music.data.source.AppRepository;
import io.github.ryanhoo.music.event.PlayListUpdatedEvent;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/14/16
 * Time: 2:35 AM
 * Desc: PlayListDetailsPresenter
 */
public class PlayListDetailsPresenter implements PlayListDetailsContract.Presenter {

    private PlayListDetailsContract.View mView;
    private AppRepository mRepository;
    private CompositeDisposable mDisposables;

    public PlayListDetailsPresenter(AppRepository repository, PlayListDetailsContract.View view) {
        mView = view;
        mRepository = repository;
        mDisposables = new CompositeDisposable();
        mView.setPresenter(this);
    }

    @Override
    public void subscribe() {
        // Nothing to do
    }

    @Override
    public void unsubscribe() {
        mView = null;
        mDisposables.clear();
    }

    @Override
    public void addSongToPlayList(Song song, final PlayList playList) {
        if (playList.isFavorite()) {
            song.setFavorite(true);
        }
        playList.addSong(song, 0);
        DisposableObserver disposableObserver = new DisposableObserver() {
            @Override
            protected void onStart() {
                mView.showLoading();
            }

            @Override
            public void onNext(Object o) {
                RxBus.getInstance().post(new PlayListUpdatedEvent(playList));
            }

            @Override
            public void onError(Throwable e) {
                mView.hideLoading();
                mView.handleError(e);
            }

            @Override
            public void onComplete() {
                mView.hideLoading();
            }
        };
        mRepository.update(playList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(disposableObserver);
        mDisposables.add(disposableObserver);
    }

    @Override
    public void delete(final Song song, final PlayList playList) {
        playList.removeSong(song);
        DisposableObserver disposableObserver = new DisposableObserver() {
            @Override
            protected void onStart() {
                mView.showLoading();
            }

            @Override
            public void onNext(Object o) {
                mView.onSongDeleted(song);
                RxBus.getInstance().post(new PlayListUpdatedEvent(playList));
            }

            @Override
            public void onError(Throwable e) {
                mView.hideLoading();
                mView.handleError(e);
            }

            @Override
            public void onComplete() {
                mView.hideLoading();
            }
        };
        mDisposables.add(disposableObserver);
    }
}
