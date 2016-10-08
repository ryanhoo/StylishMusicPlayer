package io.github.ryanhoo.music.utils;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;
import io.github.ryanhoo.music.R;
import io.github.ryanhoo.music.ui.widget.CharacterDrawable;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/4/16
 * Time: 4:08 PM
 * Desc: ViewUtils
 */
public class ViewUtils {

    public static void setLightStatusBar(@NonNull View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
        }
    }

    public static void clearLightStatusBar(@NonNull View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
        }
    }

    public static CharacterDrawable generateAlbumDrawable(Context context, String albumName) {
        if (context == null || albumName == null) return null;

        return new CharacterDrawable.Builder()
                .setCharacter(albumName.length() == 0 ? ' ' : albumName.charAt(0))
                .setBackgroundColor(ContextCompat.getColor(context, R.color.mp_characterView_background))
                .setCharacterTextColor(ContextCompat.getColor(context, R.color.mp_characterView_textColor))
                .setCharacterPadding(context.getResources().getDimensionPixelSize(R.dimen.mp_characterView_padding))
                .build();
    }
}
