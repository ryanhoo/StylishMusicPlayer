package io.github.ryanhoo.music.utils;

import java.text.DecimalFormat;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/3/16
 * Time: 11:11 PM
 * Desc: FileUtils
 */
public class FileUtils {

    /**
     * http://stackoverflow.com/a/5599842/2290191
     * @param size Original file size in byte
     * @return Readable file size in formats
     * */
    public static String readableFileSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"b", "kb", "M", "G", "T"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.##").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}
