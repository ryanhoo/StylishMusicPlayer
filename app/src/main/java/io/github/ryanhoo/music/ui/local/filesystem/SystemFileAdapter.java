package io.github.ryanhoo.music.ui.local.filesystem;

import android.content.Context;
import io.github.ryanhoo.music.ui.base.adapter.ListAdapter;

import java.io.File;
import java.util.List;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/3/16
 * Time: 11:29 PM
 * Desc: SystemFileAdapter
 */
public class SystemFileAdapter extends ListAdapter<File, FileItemView> {

    public SystemFileAdapter(Context context, List<File> data) {
        super(context, data);
    }

    @Override
    protected FileItemView createView(Context context) {
        return new FileItemView(context);
    }
}
