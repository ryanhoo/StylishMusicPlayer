package io.github.ryanhoo.music.ui.local.folder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
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
 * Desc: FolderAdapter
 */
public class FolderAdapter extends AbstractFooterAdapter<Folder, FolderItemView> {

    private Context mContext;

    private View mFooterView;
    private TextView textViewSummary;

    private AddFolderCallback mAddFolderCallback;

    public FolderAdapter(Context context, List<Folder> data) {
        super(context, data);
        mContext = context;
        registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                updateFooterView();
            }
        });
    }

    @Override
    protected FolderItemView createView(Context context) {
        return new FolderItemView(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final RecyclerView.ViewHolder holder = super.onCreateViewHolder(parent, viewType);
        if (holder.itemView instanceof FolderItemView) {
            final FolderItemView itemView = (FolderItemView) holder.itemView;
            itemView.buttonAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();
                    if (mAddFolderCallback != null) {
                        mAddFolderCallback.onAction(itemView.buttonAction ,position);
                    }
                }
            });
        }
        return holder;
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
                    }
                }
            });
            textViewSummary = (TextView) mFooterView.findViewById(R.id.text_view_summary);
        }
        updateFooterView();
        return mFooterView;
    }

    public void updateFooterView() {
        if (textViewSummary == null) return;

        int itemCount = getItemCount() - 1; // real data count
        if (itemCount > 1) {
            textViewSummary.setVisibility(View.VISIBLE);
            textViewSummary.setText(mContext.getString(R.string.mp_local_files_folder_list_end_summary_formatter, itemCount));
        } else {
            textViewSummary.setVisibility(View.GONE);
        }
    }

    // Callback

    public void setAddFolderCallback(AddFolderCallback callback) {
        mAddFolderCallback = callback;
    }

    /* package */ interface AddFolderCallback {

        void onAction(View actionView, int position);

        void onAddFolder();
    }
}
