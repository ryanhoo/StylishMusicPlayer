package io.github.ryanhoo.music.data.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import io.github.ryanhoo.music.player.PlayMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/5/16
 * Time: 5:53 PM
 * Desc: PlayList
 */
public class PlayList {

    private static Random DICE = new Random();
    public static final int NO_POSITION = -1;

    private List<Song> songs;

    private int playingIndex = -1;

    private PlayMode playMode = PlayMode.LOOP;

    public PlayList() {
        // EMPTY
    }

    public static int getNoPosition() {
        return NO_POSITION;
    }

    @NonNull
    public List<Song> getSongs() {
        if (songs == null) {
            songs = new ArrayList<>();
        }
        return songs;
    }

    public void setSongs(@Nullable List<Song> songs) {
        if (songs == null) {
            songs = new ArrayList<>();
        }
        this.songs = songs;
    }

    public int getPlayingIndex() {
        return playingIndex;
    }

    public void setPlayingIndex(int playingIndex) {
        this.playingIndex = playingIndex;
    }

    public PlayMode getPlayMode() {
        return playMode;
    }

    public void setPlayMode(PlayMode playMode) {
        this.playMode = playMode;
    }

    // Utils

    /**
     * Prepare to play
     */
    public boolean prepare() {
        if (songs.isEmpty()) return false;
        if (playingIndex == NO_POSITION) {
            playingIndex = 0;
        }
        return true;
    }

    /**
     * The current song being played or is playing based on the {@link #playingIndex}
     */
    public Song getCurrentSong() {
        if (playingIndex != NO_POSITION) {
            return songs.get(playingIndex);
        }
        return null;
    }

    /**
     * Whether has next song to play
     */
    public boolean hasNext() {
        if (songs.isEmpty()) return false;
        if (playMode == PlayMode.LIST && playingIndex + 1 >= songs.size()) return false;

        return true;
    }

    public Song next() {
        switch (playMode) {
            case LOOP:
            case LIST:
            case SINGLE:
                playingIndex = correctPlayIndex(playingIndex + 1);
                break;
            case SHUFFLE:
                playingIndex = randomPlayIndex();
                break;
        }
        return songs.get(playingIndex);
    }

    private int correctPlayIndex(int index) {
        if (index >= songs.size() || index < 0) return 0;
        return index;
    }

    private int randomPlayIndex() {
        int randomIndex = DICE.nextInt(songs.size());
        // Make sure not play the same song twice if there are at least 2 songs
        if (songs.size() > 1 && randomIndex == playingIndex) {
            randomPlayIndex();
        }
        return randomIndex;
    }
}
