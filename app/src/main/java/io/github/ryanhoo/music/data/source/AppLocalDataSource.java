package io.github.ryanhoo.music.data.source;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.model.ConflictAlgorithm;

import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import io.github.ryanhoo.music.data.model.Folder;
import io.github.ryanhoo.music.data.model.PlayList;
import io.github.ryanhoo.music.data.model.Song;
import io.github.ryanhoo.music.utils.DBUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/10/16
 * Time: 4:54 PM
 * Desc: AppLocalDataSource
 */
/* package */ class AppLocalDataSource implements AppContract {

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
        return Observable.create(new ObservableOnSubscribe<List<PlayList>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<PlayList>> e) {
                List<PlayList> playLists = mLiteOrm.query(PlayList.class);
                if (playLists.isEmpty()) {
                    // First query, create the default play list
                    PlayList playList = DBUtils.generateFavoritePlayList(mContext);
                    long result = mLiteOrm.save(playList);
                    Log.d(TAG, "Create default playlist(Favorite) with " + (result == 1 ? "success" : "failure"));
                    playLists.add(playList);
                }
                e.onNext(playLists);
                e.onComplete();
            }
        });
    }

    @Override
    public List<PlayList> cachedPlayLists() {
        return null;
    }

    @Override
    public Observable<PlayList> create(final PlayList playList) {
        return Observable.create(new ObservableOnSubscribe<PlayList>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<PlayList> e) {
                Date now = new Date();
                playList.setCreatedAt(now);
                playList.setUpdatedAt(now);

                long result = mLiteOrm.save(playList);
                if (result > 0) {
                    e.onNext(playList);
                } else {
                    e.onError(new Exception("Create play list failed"));
                }
                e.onComplete();
            }
        });

    }

    @Override
    public Observable<PlayList> update(final PlayList playList) {
        return Observable.create(new ObservableOnSubscribe<PlayList>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<PlayList> e) {
                playList.setUpdatedAt(new Date());

                long result = mLiteOrm.update(playList);
                if (result > 0) {
                    e.onNext(playList);
                } else {
                    e.onError(new Exception("Update play list failed"));
                }
                e.onComplete();
            }
        });
    }

    @Override
    public Observable<PlayList> delete(final PlayList playList) {
        return Observable.create(new ObservableOnSubscribe<PlayList>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<PlayList> e) {
                long result = mLiteOrm.delete(playList);
                if (result > 0) {
                    e.onNext(playList);
                } else {
                    e.onError(new Exception("Delete play list failed"));
                }
                e.onComplete();
            }
        });
    }

    // Folder

    @Override
    public Observable<List<Folder>> folders() {
        return Observable.create(new ObservableOnSubscribe<List<Folder>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<Folder>> e) {
                if (PreferenceManager.isFirstQueryFolders(mContext)) {
                    List<Folder> defaultFolders = DBUtils.generateDefaultFolders();
                    long result = mLiteOrm.save(defaultFolders);
                    Log.d(TAG, "Create default folders effected " + result + "rows");
                    PreferenceManager.reportFirstQueryFolders(mContext);
                }
                List<Folder> folders = mLiteOrm.query(
                        QueryBuilder.create(Folder.class).appendOrderAscBy(Folder.COLUMN_NAME)
                );
                e.onNext(folders);
                e.onComplete();
            }
        });
    }

    @Override
    public Observable<Folder> create(final Folder folder) {
        return Observable.create(new ObservableOnSubscribe<Folder>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Folder> e) {
                folder.setCreatedAt(new Date());

                long result = mLiteOrm.save(folder);
                if (result > 0) {
                    e.onNext(folder);
                } else {
                    e.onError(new Exception("Create folder failed"));
                }
                e.onComplete();
            }
        });
    }

    @Override
    public Observable<List<Folder>> create(final List<Folder> folders) {
        return Observable.create(new ObservableOnSubscribe<List<Folder>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<Folder>> e) {
                Date now = new Date();
                for (Folder folder : folders) {
                    folder.setCreatedAt(now);
                }

                long result = mLiteOrm.save(folders);
                if (result > 0) {
                    List<Folder> allNewFolders = mLiteOrm.query(
                            QueryBuilder.create(Folder.class).appendOrderAscBy(Folder.COLUMN_NAME)
                    );
                    e.onNext(allNewFolders);
                } else {
                    e.onError(new Exception("Create folders failed"));
                }
                e.onComplete();
            }
        });
    }

    @Override
    public Observable<Folder> update(final Folder folder) {
        return Observable.create(new ObservableOnSubscribe<Folder>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Folder> e) {
                mLiteOrm.delete(folder);
                long result = mLiteOrm.save(folder);
                if (result > 0) {
                    e.onNext(folder);
                } else {
                    e.onError(new Exception("Update folder failed"));
                }
                e.onComplete();
            }
        });
    }

    @Override
    public Observable<Folder> delete(final Folder folder) {
        return Observable.create(new ObservableOnSubscribe<Folder>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Folder> e) {
                long result = mLiteOrm.delete(folder);
                if (result > 0) {
                    e.onNext(folder);
                } else {
                    e.onError(new Exception("Delete folder failed"));
                }
                e.onComplete();
            }
        });
    }

    @Override
    public Observable<List<Song>> insert(final List<Song> songs) {
        return Observable.create(new ObservableOnSubscribe<List<Song>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<Song>> e) {
                for (Song song : songs) {
                    mLiteOrm.insert(song, ConflictAlgorithm.Abort);
                }
                List<Song> allSongs = mLiteOrm.query(Song.class);
                File file;
                for (Iterator<Song> iterator = allSongs.iterator(); iterator.hasNext(); ) {
                    Song song = iterator.next();
                    file = new File(song.getPath());
                    boolean exists = file.exists();
                    if (!exists) {
                        iterator.remove();
                        mLiteOrm.delete(song);
                    }
                }
                e.onNext(allSongs);
                e.onComplete();
            }
        });
    }

    @Override
    public Observable<Song> update(final Song song) {
        return Observable.create(new ObservableOnSubscribe<Song>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Song> e) {
                int result = mLiteOrm.update(song);
                if (result > 0) {
                    e.onNext(song);
                } else {
                    e.onError(new Exception("Update song failed"));
                }
                e.onComplete();
            }
        });
    }

    @Override
    public Observable<Song> setSongAsFavorite(final Song song, final boolean isFavorite) {
        return Observable.create(new ObservableOnSubscribe<Song>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Song> e) {
                List<PlayList> playLists = mLiteOrm.query(
                        QueryBuilder.create(PlayList.class).whereEquals(PlayList.COLUMN_FAVORITE, String.valueOf(true))
                );
                if (playLists.isEmpty()) {
                    PlayList defaultFavorite = DBUtils.generateFavoritePlayList(mContext);
                    playLists.add(defaultFavorite);
                }
                PlayList favorite = playLists.get(0);
                song.setFavorite(isFavorite);
                favorite.setUpdatedAt(new Date());
                if (isFavorite) {
                    // Insert song to the beginning of the list
                    favorite.addSong(song, 0);
                } else {
                    favorite.removeSong(song);
                }
                mLiteOrm.insert(song, ConflictAlgorithm.Replace);
                long result = mLiteOrm.insert(favorite, ConflictAlgorithm.Replace);
                if (result > 0) {
                    e.onNext(song);
                } else {
                    if (isFavorite) {
                        e.onError(new Exception("Set song as favorite failed"));
                    } else {
                        e.onError(new Exception("Set song as unfavorite failed"));
                    }
                }
                e.onComplete();
            }
        });
    }
}
