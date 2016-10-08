package io.github.ryanhoo.music.ui.common;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import io.github.ryanhoo.music.ui.base.adapter.IAdapterView;
import io.github.ryanhoo.music.ui.base.adapter.ListAdapter;

import java.util.List;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/3/16
 * Time: 10:32 PM
 * Desc: AbstractFooterAdapter
 */
public abstract class AbstractFooterAdapter<T, V extends IAdapterView> extends ListAdapter<T, V> {

    protected static final int VIEW_TYPE_ITEM = 1; // Normal list item
    protected static final int VIEW_TYPE_FOOTER = 2;  // Footer

    public AbstractFooterAdapter(Context context, List<T> data) {
        super(context, data);
    }

    /**
     * Default footer view is disabled, override in subclass and return true if want to enable it.
     */
    protected boolean isFooterEnabled() {
        return false;
    }

    /**
     * @return Custom footer view, but override {@link #isFooterEnabled} and return true first.
     */
    protected View createFooterView() {
        return null;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_FOOTER) {
            return new RecyclerView.ViewHolder(createFooterView()) {};
        }
        return super.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_ITEM) {
            super.onBindViewHolder(holder, position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isFooterEnabled() && position == getItemCount() - 1) {
            return VIEW_TYPE_FOOTER;
        }
        return VIEW_TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        int itemCount = super.getItemCount();
        if (isFooterEnabled()) {
            itemCount += 1;
        }
        return itemCount;
    }

    @Override
    public T getItem(int position) {
        if (getItemViewType(position) == VIEW_TYPE_FOOTER) {
            return null;
        }
        return super.getItem(position);
    }
}
