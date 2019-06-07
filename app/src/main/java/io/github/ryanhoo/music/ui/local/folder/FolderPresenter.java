package io.github.ryanhoo.music.ui.local.folder;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.github.ryanhoo.music.RxBus;
import io.github.ryanhoo.music.data.model.Folder;
import io.github.ryanhoo.music.data.model.PlayList;
import io.github.ryanhoo.music.data.model.Song;
import io.github.ryanhoo.music.data.source.AppRepository;
import io.github.ryanhoo.music.event.PlayListUpdatedEvent;
import io.github.ryanhoo.music.utils.FileUtils;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/10/16
 * Time: 11:38 PM
 * Desc: FolderPresenter
 */
public class FolderPresenter implements FolderContract.Presenter {

    private FolderContract.View mView;
    private AppRepository mRepository;
    private CompositeDisposable mDisposables;

    public FolderPresenter(AppRepository repository, FolderContract.View view) {
        mView = view;
        mRepository = repository;
        mDisposables = new CompositeDisposable();
        mView.setPresenter(this);
    }

    @Override
    public void subscribe() {
        loadFolders();
    }

    @Override
    public void unsubscribe() {
        mView = null;
        mDisposables.clear();
    }

    @Override
    public void loadFolders() {
        DisposableObserver disposableObserver = new DisposableObserver<List<Folder>>() {
            @Override
            protected void onStart() {
                mView.showLoading();
            }

            @Override
            public void onNext(List<Folder> folders) {
                mView.onFoldersLoaded(folders);
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
        mRepository.folders()
                .subscribeOn(Schedulers.io())
                .doOnNext(new Consumer<List<Folder>>() {
                    @Override
                    public void accept(List<Folder> folders) {
                        Collections.sort(folders, new Comparator<Folder>() {
                            @Override
                            public int compare(Folder f1, Folder f2) {
                                return f1.getName().compareToIgnoreCase(f2.getName());
                            }
                        });
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(disposableObserver);
        mDisposables.add(disposableObserver);
    }

    @Override
    public void addFolders(List<File> folders, final List<Folder> existedFolders) {
        DisposableObserver disposableObserver = new DisposableObserver<List<Folder>>() {
            @Override
            protected void onStart() {
                mView.showLoading();
            }

            @Override
            public void onNext(List<Folder> allNewFolders) {
                mView.onFoldersAdded(allNewFolders);
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
        Observable.fromIterable(folders)
                .filter(new Predicate<File>() {
                    @Override
                    public boolean test(File file) throws Exception {
                        for (Folder folder : existedFolders) {
                            if (file.getAbsolutePath().equals(folder.getPath())) {
                                return false;
                            }
                        }
                        return true;
                    }
                })
                .flatMap(new Function<File, Observable<Folder>>() {
                    @Override
                    public Observable<Folder> apply(File file) {
                        Folder folder = new Folder();
                        folder.setName(file.getName());
                        folder.setPath(file.getAbsolutePath());
                        List<Song> musicFiles = FileUtils.musicFiles(file);
                        folder.setSongs(musicFiles);
                        folder.setNumOfSongs(musicFiles.size());
                        return Observable.just(folder);
                    }
                })
                .toList()
                .toObservable()
                .flatMap(new Function<List<Folder>, Observable<List<Folder>>>() {
                    @Override
                    public Observable<List<Folder>> apply(List<Folder> folders) {
                        return mRepository.create(folders);
                    }
                })
                .doOnNext(new Consumer<List<Folder>>() {
                    @Override
                    public void accept(List<Folder> folders) {
                        Collections.sort(folders, new Comparator<Folder>() {
                            @Override
                            public int compare(Folder f1, Folder f2) {
                                return f1.getName().compareToIgnoreCase(f2.getName());
                            }
                        });
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(disposableObserver);
        mDisposables.add(disposableObserver);
    }

    @Override
    public void refreshFolder(final Folder folder) {
        DisposableObserver disposableObserver = new DisposableObserver<Folder>() {
            @Override
            protected void onStart() {
                mView.showLoading();
            }

            @Override
            public void onNext(Folder folder) {
                mView.onFolderUpdated(folder);
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
        Observable.just(FileUtils.musicFiles(new File(folder.getPath())))
                .flatMap(new Function<List<Song>, Observable<Folder>>() {
                    @Override
                    public Observable<Folder> apply(List<Song> songs) {
                        folder.setSongs(songs);
                        folder.setNumOfSongs(songs.size());
                        return mRepository.update(folder);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(disposableObserver);
        mDisposables.add(disposableObserver);
    }

    @Override
    public void deleteFolder(Folder folder) {
        DisposableObserver disposableObserver = new DisposableObserver<Folder>() {
            @Override
            protected void onStart() {
                mView.showLoading();
            }

            @Override
            public void onNext(Folder folder) {
                mView.onFolderDeleted(folder);
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

        mRepository.delete(folder)
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
            public void onNext(PlayList playList) {
                mView.onPlayListCreated(playList);
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

        mRepository
                .create(playList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(disposableObserver);
        mDisposables.add(disposableObserver);
    }

    @Override
    public void addFolderToPlayList(final Folder folder, PlayList playList) {
        if (folder.getSongs().isEmpty()) return;

        if (playList.isFavorite()) {
            for (Song song : folder.getSongs()) {
                song.setFavorite(true);
            }
        }
        playList.addSong(folder.getSongs(), 0);

        DisposableObserver disposableObserver = new DisposableObserver<PlayList>() {
            @Override
            protected void onStart() {
                mView.showLoading();
            }

            @Override
            public void onNext(PlayList playList) {
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
}
