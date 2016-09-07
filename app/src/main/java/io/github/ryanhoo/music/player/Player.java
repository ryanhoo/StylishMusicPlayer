package io.github.ryanhoo.music.player;

import android.media.MediaPlayer;
import android.util.Log;
import io.github.ryanhoo.music.data.model.PlayList;
import io.github.ryanhoo.music.data.model.Song;

import java.io.IOException;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/5/16
 * Time: 5:57 PM
 * Desc: Player
 */
public class Player implements IPlayer, MediaPlayer.OnCompletionListener {

    private static final String TAG = "Player";

    private static volatile Player sInstance;

    private MediaPlayer mPlayer;

    private PlayList mPlayList;
    private Callback mCallback;

    // Player status
    private boolean isPaused;

    private Player() {
        mPlayer = new MediaPlayer();
        mPlayList = new PlayList();
        mPlayer.setOnCompletionListener(this);
    }

    public static Player getInstance() {
        if (sInstance == null) {
            synchronized (Player.class) {
                if (sInstance == null) {
                    sInstance = new Player();
                }
            }
        }
        return sInstance;
    }

    @Override
    public void setPlayList(PlayList list) {
        if (list == null) {
            list = new PlayList();
        }
        mPlayList = list;
    }

    @Override
    public boolean play() {
        if (isPaused) {
            mPlayer.start();
            return true;
        }
        if (mPlayList.prepare()) {
            Song song = mPlayList.getCurrentSong();
            try {
                mPlayer.reset();
                mPlayer.setDataSource(song.getPath());
                mPlayer.prepare();
                mPlayer.start();
            } catch (IOException e) {
                Log.e(TAG, "play: ", e);
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean play(PlayList list) {
        if (list == null) return false;

        isPaused = false;
        setPlayList(list);
        return play();
    }

    @Override
    public boolean play(Song song) {
        if (song == null) return false;

        isPaused = false;
        mPlayList.getSongs().clear();
        mPlayList.getSongs().add(song);
        return play();
    }

    @Override
    public boolean pause() {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
            isPaused = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean isPlaying() {
        return mPlayer.isPlaying();
    }

    @Override
    public int getProgress() {
        return mPlayer.getCurrentPosition();
    }

    @Override
    public Song getPlayingSong() {
        return mPlayList.getCurrentSong();
    }

    @Override
    public void seekTo(int progress) {
        mPlayer.seekTo(progress);
    }

    // Listeners

    @Override
    public void onCompletion(MediaPlayer mp) {
        Song current = mPlayList.getCurrentSong();
        Song next = null;
        boolean hasNext = mPlayList.hasNext();
        if (hasNext) {
            next = mPlayList.next();
        }
        if (mCallback != null) {
            mCallback.onComplete(current, next);
        }
    }

    // Setters & Getters

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public interface Callback {

        void onComplete(Song completed, Song next);
    }
}
