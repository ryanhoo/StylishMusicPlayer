package io.github.ryanhoo.music.ui.common;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import io.github.ryanhoo.music.R;
import io.github.ryanhoo.music.ui.base.adapter.IAdapterView;
import io.github.ryanhoo.music.ui.base.adapter.ListAdapter;

import java.util.List;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/3/16
 * Time: 6:00 PM
 * Desc: AbstractCommonAdapter
 */
public abstract class AbstractSummaryAdapter<T, V extends IAdapterView> extends ListAdapter<T, V> {

    protected static final int VIEW_TYPE_ITEM = 1; // Normal list item
    protected static final int VIEW_TYPE_END = 2;  // End summary item, e.g. '2 items in total'

    private Context mContext;
    /**
     * Whether is showing end summary info, e.g. '2 items in total'
     */
    private boolean hasEndSummary;
    private TextView textViewEndSummary;

    public AbstractSummaryAdapter(Context context, List<T> data) {
        super(context, data);
        mContext = context;
        registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                updateSummaryText();
            }
        });
    }

    /**
     * End summary text
     */
    protected abstract String getEndSummaryText(int dataCount);

    protected View getEndSummaryView() {
        if (textViewEndSummary == null) {
            textViewEndSummary = (TextView) View.inflate(mContext, R.layout.default_list_end_summary, null);
            textViewEndSummary.setLayoutParams(new RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        updateSummaryText();
        return textViewEndSummary;
    }

    public void updateSummaryText() {
        if (textViewEndSummary != null) {
            textViewEndSummary.setText(getEndSummaryText(super.getItemCount()));
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_END) {
            View endSummaryView = getEndSummaryView();
            return new RecyclerView.ViewHolder(endSummaryView) {
            };
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
        if (hasEndSummary && position == getItemCount() - 1) {
            return VIEW_TYPE_END;
        }
        return VIEW_TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        int itemCount = super.getItemCount();
        if (itemCount > 1) {
            itemCount += 1;
            hasEndSummary = true;
        } else {
            hasEndSummary = false;
        }
        return itemCount;
    }

    @Override
    public T getItem(int position) {
        if (getItemViewType(position) == VIEW_TYPE_END) {
            return null;
        }
        return super.getItem(position);
    }
}
