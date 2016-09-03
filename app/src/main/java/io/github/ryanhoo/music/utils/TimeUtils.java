package io.github.ryanhoo.music.utils;

import android.annotation.SuppressLint;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/2/16
 * Time: 6:07 PM
 * Desc: TimeUtils
 */
public class TimeUtils {

    /**
     * Parse the time in milliseconds into String with the format: hh:mm:ss or mm:ss
     *
     * @param duration The time needs to be parsed.
     */
    @SuppressLint("DefaultLocale")
    public static String formatDuration(int duration) {
        duration /= 1000; // milliseconds into seconds
        int minute = duration / 60;
        int hour = minute / 60;
        minute %= 60;
        int second = duration % 60;
        if (hour != 0)
            return String.format("%2d:%02d:%02d", hour, minute, second);
        else
            return String.format("%02d:%02d", minute, second);
    }
}
