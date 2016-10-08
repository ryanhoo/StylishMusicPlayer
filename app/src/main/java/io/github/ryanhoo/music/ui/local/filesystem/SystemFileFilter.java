package io.github.ryanhoo.music.ui.local.filesystem;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/4/16
 * Time: 1:39 AM
 * Desc: SystemFileFilter
 */
public class SystemFileFilter implements FilenameFilter {

    public static SystemFileFilter DEFAULT_INSTANCE = new SystemFileFilter();

    @Override
    public boolean accept(File dir, String name) {
        // Ignore system files/folders start with ., such as .android, .git
        return !name.startsWith(".");
    }
}
