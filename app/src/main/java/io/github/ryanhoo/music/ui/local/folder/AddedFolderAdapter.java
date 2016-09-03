package io.github.ryanhoo.music.ui.local.folder;

import android.content.Context;
import io.github.ryanhoo.music.R;
import io.github.ryanhoo.music.data.model.Folder;
import io.github.ryanhoo.music.ui.common.AbstractCommonAdapter;

import java.util.List;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/3/16
 * Time: 7:22 PM
 * Desc: AddedFolderAdapter
 */
public class AddedFolderAdapter extends AbstractCommonAdapter<Folder, AddedFolderItemView> {

    private Context mContext;

    public AddedFolderAdapter(Context context, List<Folder> data) {
        super(context, data);
        mContext = context;
    }

    @Override
    protected String getEndSummaryText(int dataCount) {
        return mContext.getString(R.string.mp_local_files_folder_list_end_summary_formatter, dataCount);
    }

    @Override
    protected AddedFolderItemView createView(Context context) {
        return new AddedFolderItemView(context);
    }
}
