package io.github.ryanhoo.music.player;

import io.github.ryanhoo.music.data.model.PlayList;
import io.github.ryanhoo.music.data.model.Song;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/5/16
 * Time: 6:02 PM
 * Desc: IPlayer
 */
public interface IPlayer {

    void setPlayList(PlayList list);

    boolean play();

    boolean play(PlayList list);

    boolean play(Song song);

    boolean pause();

    boolean isPlaying();

    int getProgress();

    Song getPlayingSong();

    void seekTo(int progress);
}
