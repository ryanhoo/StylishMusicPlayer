package io.github.ryanhoo.music.ui.playlist;

import io.github.ryanhoo.music.data.model.PlayList;
import io.github.ryanhoo.music.data.source.AppRepository;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import java.util.List;

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
    private CompositeSubscription mSubscriptions;

    public PlayListPresenter(AppRepository repository, PlayListContract.View view) {
        mView = view;
        mRepository = repository;
        mSubscriptions = new CompositeSubscription();
        mView.setPresenter(this);
    }

    @Override
    public void subscribe() {
        loadPlayLists();
    }

    @Override
    public void unsubscribe() {
        mView = null;
        mSubscriptions.clear();
    }

    @Override
    public void loadPlayLists() {
        Subscription subscription = mRepository.playLists()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<PlayList>>() {
                    @Override
                    public void onStart() {
                        mView.showLoading();
                    }

                    @Override
                    public void onCompleted() {
                        mView.hideLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.hideLoading();
                        mView.handleError(e);
                    }

                    @Override
                    public void onNext(List<PlayList> playLists) {
                        mView.onPlayListsLoaded(playLists);
                    }
                });
        mSubscriptions.add(subscription);
    }

    @Override
    public void createPlayList(PlayList playList) {
        Subscription subscription = mRepository
                .create(playList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<PlayList>() {
                    @Override
                    public void onStart() {
                        mView.showLoading();
                    }

                    @Override
                    public void onCompleted() {
                        mView.hideLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.hideLoading();
                        mView.handleError(e);
                    }

                    @Override
                    public void onNext(PlayList result) {
                        mView.onPlayListCreated(result);
                    }
                });
        mSubscriptions.add(subscription);
    }

    @Override
    public void editPlayList(PlayList playList) {
        Subscription subscription = mRepository
                .update(playList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<PlayList>() {
                    @Override
                    public void onStart() {
                        mView.showLoading();
                    }

                    @Override
                    public void onCompleted() {
                        mView.hideLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.hideLoading();
                        mView.handleError(e);
                    }

                    @Override
                    public void onNext(PlayList result) {
                        mView.onPlayListEdited(result);
                    }
                });
        mSubscriptions.add(subscription);
    }

    @Override
    public void deletePlayList(PlayList playList) {
        Subscription subscription = mRepository.delete(playList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<PlayList>() {
                    @Override
                    public void onStart() {
                        mView.showLoading();
                    }

                    @Override
                    public void onCompleted() {
                        mView.hideLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.hideLoading();
                        mView.handleError(e);
                    }

                    @Override
                    public void onNext(PlayList playList) {
                        mView.onPlayListDeleted(playList);
                    }
                });
        mSubscriptions.add(subscription);
    }
}
