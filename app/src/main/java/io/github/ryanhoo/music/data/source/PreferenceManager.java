package io.github.ryanhoo.music.data.source;

import android.content.Context;
import android.content.SharedPreferences;

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
     */
    private static final String KEY_FOLDERS_FIRST_QUERY = "firstQueryFolders";

    private static SharedPreferences preferences(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    private static SharedPreferences.Editor edit(Context context) {
        return preferences(context).edit();
    }

    public static boolean isFirstQueryFolders(Context context) {
        return preferences(context).getBoolean(KEY_FOLDERS_FIRST_QUERY, true);
    }

    public static void reportFirstQueryFolders(Context context) {
        edit(context).putBoolean(KEY_FOLDERS_FIRST_QUERY, false).commit();
    }
}
