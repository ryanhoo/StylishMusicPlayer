package io.github.ryanhoo.music.player;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/5/16
 * Time: 5:48 PM
 * Desc: PlayMode
 */
public enum PlayMode {
    SINGLE,
    LOOP,
    LIST,
    SHUFFLE;

    public static PlayMode getDefault() {
        return LOOP;
    }
}
