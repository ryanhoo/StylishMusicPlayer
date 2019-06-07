package io.github.ryanhoo.music.ui.playlist;

import java.util.List;

import io.github.ryanhoo.music.data.model.PlayList;
import io.github.ryanhoo.music.data.source.AppRepository;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/11/16
 * Time: 1:28 AM
 * Desc: PlayListPresenter
 */
public class PlayListPresenter implements PlayListContract.Presenter {

    private PlayListContract.View mView;
    private AppRepository mRepository;
    private CompositeDisposable mDisposables;

    public PlayListPresenter(AppRepository repository, PlayListContract.View view) {
        mView = view;
        mRepository = repository;
        mDisposables = new CompositeDisposable();
        mView.setPresenter(this);
    }

    @Override
    public void subscribe() {
        loadPlayLists();
    }

    @Override
    public void unsubscribe() {
        mView = null;
        mDisposables.clear();
    }

    @Override
    public void loadPlayLists() {
        DisposableObserver disposableObserver = new DisposableObserver<List<PlayList>>() {
            @Override
            protected void onStart() {
                mView.showLoading();
            }

            @Override
            public void onNext(List<PlayList> playLists) {
                mView.onPlayListsLoaded(playLists);
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
        mRepository.playLists()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(disposableObserver);
        mDisposables.add(disposableObserver);
    }

    @Override
    public void createPlayList(PlayList playList) {
        DisposableObserver disposableObserver = new DisposableObserver<PlayList>() {
            @Override
            protected void onStart() {
                mView.showLoading();
            }

            @Override
            public void onNext(PlayList result) {
                mView.onPlayListCreated(result);
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
        mRepository.create(playList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(disposableObserver);
        mDisposables.add(disposableObserver);
    }

    @Override
    public void editPlayList(PlayList playList) {
        DisposableObserver disposableObserver = new DisposableObserver<PlayList>() {
            @Override
            protected void onStart() {
                mView.showLoading();
            }

            @Override
            public void onNext(PlayList result) {
                mView.onPlayListEdited(result);
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
    public void deletePlayList(final PlayList playList) {
        DisposableObserver disposableObserver = new DisposableObserver() {
            @Override
            protected void onStart() {
                mView.showLoading();
            }

            @Override
            public void onNext(Object o) {
                mView.onPlayListDeleted(playList);
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
