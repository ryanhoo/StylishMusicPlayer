package io.github.ryanhoo.music.ui.local.filesystem;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import io.github.ryanhoo.music.ui.base.adapter.ListAdapter;

import java.util.List;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/3/16
 * Time: 11:29 PM
 * Desc: FileSystemAdapter
 */
public class FileSystemAdapter extends ListAdapter<FileWrapper, FileItemView> {

    public FileSystemAdapter(Context context, List<FileWrapper> data) {
        super(context, data);
    }

    @Override
    protected FileItemView createView(Context context) {
        return new FileItemView(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final RecyclerView.ViewHolder holder = super.onCreateViewHolder(parent, viewType);
        if (holder.itemView instanceof FileItemView) {
            FileItemView fileItemView = (FileItemView) holder.itemView;
            fileItemView.imageViewFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = holder.getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        getItemLongClickListener().onItemClick(position);
                    }
                }
            });
        }
        return holder;
    }

    public void clearSelections() {
        if (getData().isEmpty()) return;

        for (FileWrapper wrapper : getData()) {
            wrapper.selected = false;
        }
    }
}
