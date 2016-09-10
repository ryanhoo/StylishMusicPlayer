package io.github.ryanhoo.music.utils;

import android.media.MediaMetadataRetriever;
import io.github.ryanhoo.music.data.model.Song;

import java.io.File;
import java.io.FileFilter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

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
     *
     * @param size Original file size in byte
     * @return Readable file size in formats
     */
    public static String readableFileSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"b", "kb", "M", "G", "T"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.##").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public static boolean isMusic(File file) {
        final String REGEX = "(.*/)*.+\\.(mp3|ogg|wav|aac)$";
        return file.getName().matches(REGEX);
    }

    public static boolean isLyric(File file) {
        return file.getName().toLowerCase().endsWith(".lrc");
    }

    public static List<Song> musicFiles(File dir) {
        List<Song> songs = new ArrayList<>();
        if (dir != null && dir.isDirectory()) {
            final File[] files = dir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File item) {
                    return item.isFile() && isMusic(item);
                }
            });
            for (File file : files) {
                Song song = fileToMusic(file);
                if (song != null) {
                    songs.add(song);
                }
            }
        }
        return songs;
    }

    public static Song fileToMusic(File file) {
        if (file.length() == 0) return null;

        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        metadataRetriever.setDataSource(file.getAbsolutePath());

        String title = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        String displayName = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        String artist = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        String album = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        int duration = Integer.parseInt(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));

        if (duration == 0) return null;

        Song song = new Song();
        song.setTitle(title);
        song.setDisplayName(displayName);
        song.setArtist(artist);
        song.setPath(file.getAbsolutePath());
        song.setAlbum(album);
        song.setDuration(duration);
        song.setSize((int) file.length());
        return song;
    }
}
