package io.github.ryanhoo.music.ui.music;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import io.github.ryanhoo.music.RxBus;
import io.github.ryanhoo.music.data.model.Song;
import io.github.ryanhoo.music.data.source.AppRepository;
import io.github.ryanhoo.music.data.source.PreferenceManager;
import io.github.ryanhoo.music.event.FavoriteChangeEvent;
import io.github.ryanhoo.music.player.PlayMode;
import io.github.ryanhoo.music.player.PlaybackService;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/12/16
 * Time: 8:30 AM
 * Desc: MusicPlayerPresenter
 */
public class MusicPlayerPresenter implements MusicPlayerContract.Presenter {

    private Context mContext;
    private MusicPlayerContract.View mView;
    private AppRepository mRepository;
    private CompositeDisposable mDisposables;

    private PlaybackService mPlaybackService;
    private boolean mIsServiceBound;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            mPlaybackService = ((PlaybackService.LocalBinder) service).getService();
            mView.onPlaybackServiceBound(mPlaybackService);
            mView.onSongUpdated(mPlaybackService.getPlayingSong());
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            mPlaybackService = null;
            mView.onPlaybackServiceUnbound();
        }
    };

    public MusicPlayerPresenter(Context context, AppRepository repository, MusicPlayerContract.View view) {
        mContext = context;
        mView = view;
        mRepository = repository;
        mDisposables = new CompositeDisposable();
        mView.setPresenter(this);
    }

    @Override
    public void subscribe() {
        bindPlaybackService();

        retrieveLastPlayMode();

        // TODO
        if (mPlaybackService != null && mPlaybackService.isPlaying()) {
            mView.onSongUpdated(mPlaybackService.getPlayingSong());
        } else {
            // - load last play list/folder/song
        }
    }

    @Override
    public void unsubscribe() {
        unbindPlaybackService();
        // Release context reference
        mContext = null;
        mView = null;
        mDisposables.clear();
    }

    @Override
    public void retrieveLastPlayMode() {
        PlayMode lastPlayMode = PreferenceManager.lastPlayMode(mContext);
        mView.updatePlayMode(lastPlayMode);
    }

    @Override
    public void setSongAsFavorite(Song song, boolean favorite) {
        DisposableObserver disposableObserver = new DisposableObserver<Song>() {

            @Override
            public void onNext(Song song) {
                mView.onSongSetAsFavorite(song);
                RxBus.getInstance().post(new FavoriteChangeEvent(song));
            }

            @Override
            public void onError(Throwable e) {
                mView.handleError(e);
            }

            @Override
            public void onComplete() {
            }
        };
        mRepository.setSongAsFavorite(song, favorite)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(disposableObserver);
        mDisposables.add(disposableObserver);
    }

    @Override
    public void bindPlaybackService() {
        // Establish a connection with the service.  We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).
        mContext.bindService(new Intent(mContext, PlaybackService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsServiceBound = true;
    }

    @Override
    public void unbindPlaybackService() {
        if (mIsServiceBound) {
            // Detach our existing connection.
            mContext.unbindService(mConnection);
            mIsServiceBound = false;
        }
    }
}
