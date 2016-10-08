package io.github.ryanhoo.music.utils;

import android.media.MediaMetadataRetriever;
import android.text.TextUtils;
import io.github.ryanhoo.music.data.model.Folder;
import io.github.ryanhoo.music.data.model.Song;

import java.io.File;
import java.io.FileFilter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/3/16
 * Time: 11:11 PM
 * Desc: FileUtils
 */
public class FileUtils {

    private static final String UNKNOWN = "unknown";

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
        final String REGEX = "(.*/)*.+\\.(mp3|m4a|ogg|wav|aac)$";
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
        if (songs.size() > 1) {
            Collections.sort(songs, new Comparator<Song>() {
                @Override
                public int compare(Song left, Song right) {
                    return left.getTitle().compareTo(right.getTitle());
                }
            });
        }
        return songs;
    }

    public static Song fileToMusic(File file) {
        if (file.length() == 0) return null;

        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        metadataRetriever.setDataSource(file.getAbsolutePath());

        final int duration;

        String keyDuration = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        // ensure the duration is a digit, otherwise return null song
        if (keyDuration == null || !keyDuration.matches("\\d+")) return null;
        duration = Integer.parseInt(keyDuration);

        final String title = extractMetadata(metadataRetriever, MediaMetadataRetriever.METADATA_KEY_TITLE, file.getName());
        final String displayName = extractMetadata(metadataRetriever, MediaMetadataRetriever.METADATA_KEY_TITLE, file.getName());
        final String artist = extractMetadata(metadataRetriever, MediaMetadataRetriever.METADATA_KEY_ARTIST, UNKNOWN);
        final String album = extractMetadata(metadataRetriever, MediaMetadataRetriever.METADATA_KEY_ALBUM, UNKNOWN);

        final Song song = new Song();
        song.setTitle(title);
        song.setDisplayName(displayName);
        song.setArtist(artist);
        song.setPath(file.getAbsolutePath());
        song.setAlbum(album);
        song.setDuration(duration);
        song.setSize((int) file.length());
        return song;
    }

    public static Folder folderFromDir(File dir) {
        Folder folder = new Folder(dir.getName(), dir.getAbsolutePath());
        List<Song> songs = musicFiles(dir);
        folder.setSongs(songs);
        folder.setNumOfSongs(songs.size());
        return folder;
    }

    private static String extractMetadata(MediaMetadataRetriever retriever, int key, String defaultValue) {
        String value = retriever.extractMetadata(key);
        if (TextUtils.isEmpty(value)) {
            value = defaultValue;
        }
        return value;
    }
}
