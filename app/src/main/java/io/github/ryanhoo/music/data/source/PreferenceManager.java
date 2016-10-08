package io.github.ryanhoo.music.data.source;

import android.content.Context;
import android.content.SharedPreferences;
import io.github.ryanhoo.music.player.PlayMode;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/10/16
 * Time: 11:05 PM
 * Desc: PreferenceManager
 */
public class PreferenceManager {

    private static final String PREFS_NAME = "config.xml";

    /**
     * For deciding whether to add the default folders(SDCard/Download/Music),
     * if it's being deleted manually by users, then they should not be auto-recreated.
     * {@link #isFirstQueryFolders(Context)}, {@link #reportFirstQueryFolders(Context)}
     */
    private static final String KEY_FOLDERS_FIRST_QUERY = "firstQueryFolders";

    /**
     * Play mode from the last time.
     */
    private static final String KEY_PLAY_MODE = "playMode";

    private static SharedPreferences preferences(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    private static SharedPreferences.Editor edit(Context context) {
        return preferences(context).edit();
    }

    /**
     * {@link #KEY_FOLDERS_FIRST_QUERY}
     */
    public static boolean isFirstQueryFolders(Context context) {
        return preferences(context).getBoolean(KEY_FOLDERS_FIRST_QUERY, true);
    }

    /**
     * {@link #KEY_FOLDERS_FIRST_QUERY}
     */
    public static void reportFirstQueryFolders(Context context) {
        edit(context).putBoolean(KEY_FOLDERS_FIRST_QUERY, false).commit();
    }

    /**
     * {@link #KEY_PLAY_MODE}
     */
    public static PlayMode lastPlayMode(Context context) {
        String playModeName = preferences(context).getString(KEY_PLAY_MODE, null);
        if (playModeName != null) {
            return PlayMode.valueOf(playModeName);
        }
        return PlayMode.getDefault();
    }

    /**
     * {@link #KEY_PLAY_MODE}
     */
    public static void setPlayMode(Context context, PlayMode playMode) {
        edit(context).putString(KEY_PLAY_MODE, playMode.name()).commit();
    }

}
