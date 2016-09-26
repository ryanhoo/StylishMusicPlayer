package io.github.ryanhoo.music.utils;

import android.graphics.*;
import android.media.MediaMetadataRetriever;
import android.util.Log;
import io.github.ryanhoo.music.data.model.Song;

import java.io.File;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/14/16
 * Time: 8:42 PM
 * Desc: BitmapUtils
 * TODO To be optimized
 */
public class AlbumUtils {

    private static final String TAG = "AlbumUtils";

    public static Bitmap parseAlbum(Song song) {
        return parseAlbum(new File(song.getPath()));
    }

    public static Bitmap parseAlbum(File file) {
        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        try {
            metadataRetriever.setDataSource(file.getAbsolutePath());
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "parseAlbum: ", e);
        }
        byte[] albumData = metadataRetriever.getEmbeddedPicture();
        if (albumData != null) {
            return BitmapFactory.decodeByteArray(albumData, 0, albumData.length);
        }
        return null;
    }

    public static Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }
}
