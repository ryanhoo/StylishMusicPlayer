package io.github.ryanhoo.music.ui.local.folder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import io.github.ryanhoo.music.R;
import io.github.ryanhoo.music.data.model.Folder;
import io.github.ryanhoo.music.ui.common.AbstractFooterAdapter;

import java.util.List;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/3/16
 * Time: 7:22 PM
 * Desc: AddedFolderAdapter
 */
public class AddedFolderAdapter extends AbstractFooterAdapter<Folder, AddedFolderItemView> {

    private Context mContext;

    private View mFooterView;
    private TextView textViewSummary;

    private AddFolderCallback mAddFolderCallback;

    public AddedFolderAdapter(Context context, List<Folder> data) {
        super(context, data);
        mContext = context;
    }

    @Override
    protected AddedFolderItemView createView(Context context) {
        return new AddedFolderItemView(context);
    }

    // Footer View

    @Override
    protected boolean isFooterEnabled() {
        return true;
    }

    @Override
    protected View createFooterView() {
        if (mFooterView == null) {
            mFooterView = View.inflate(mContext, R.layout.item_local_folder_footer, null);
            View layoutAddFolder = mFooterView.findViewById(R.id.layout_add_folder);
            layoutAddFolder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mAddFolderCallback != null) {
                        mAddFolderCallback.onAddFolder();
                        ;
                    }
                }
            });
            textViewSummary = (TextView) mFooterView.findViewById(R.id.text_view_summary);
        }
        int itemCount = getItemCount() - 1; // real data count
        if (itemCount > 1) {
            textViewSummary.setVisibility(View.VISIBLE);
            textViewSummary.setText(mContext.getString(R.string.mp_local_files_folder_list_end_summary_formatter, itemCount));
        } else {
            textViewSummary.setVisibility(View.GONE);
        }
        return mFooterView;
    }

    // Callback

    public void setAddFolderCallback(AddFolderCallback callback) {
        mAddFolderCallback = callback;
    }

    /* package */ interface AddFolderCallback {
        void onAddFolder();
    }
}
