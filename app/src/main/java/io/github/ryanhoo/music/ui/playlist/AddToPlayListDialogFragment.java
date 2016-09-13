package io.github.ryanhoo.music.ui.playlist;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import io.github.ryanhoo.music.R;
import io.github.ryanhoo.music.data.model.PlayList;
import io.github.ryanhoo.music.data.source.AppRepository;
import io.github.ryanhoo.music.ui.base.BaseDialogFragment;
import io.github.ryanhoo.music.ui.base.adapter.ListAdapter;
import io.github.ryanhoo.music.ui.base.adapter.OnItemClickListener;
import io.github.ryanhoo.music.ui.common.DefaultDividerDecoration;
import rx.subscriptions.CompositeSubscription;

import java.util.List;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/13/16
 * Time: 9:42 PM
 * Desc: AddToPlayListDialogFragment
 */
public class AddToPlayListDialogFragment extends BaseDialogFragment implements OnItemClickListener, DialogInterface.OnShowListener {

    RecyclerView recyclerView;

    CompositeSubscription mSubscriptions = new CompositeSubscription();

    AddPlayListAdapter mAdapter;
    Callback mCallback;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new AddPlayListAdapter(getActivity(), AppRepository.getInstance().cachedPlayLists());
        mAdapter.setOnItemClickListener(this);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.mp_play_list_dialog_add_to)
                .setView(R.layout.dialog_add_to_play_list)
                .setNegativeButton(R.string.mp_cancel, null)
                .create();
        dialog.setOnShowListener(this);
        return dialog;
    }

    @Override
    public void onDestroyView() {
        mSubscriptions.clear();
        super.onDestroyView();
    }

    @Override
    public void onShow(DialogInterface dialog) {
        resizeDialogSize();
        if (recyclerView == null) {
            recyclerView = (RecyclerView) getDialog().findViewById(R.id.recycler_view);
            recyclerView.setAdapter(mAdapter);
            recyclerView.addItemDecoration(new DefaultDividerDecoration());
        }
    }

    @Override
    public void onItemClick(int position) {
        if (mCallback != null) {
            mCallback.onPlayListSelected(mAdapter.getItem(position));
        }
        dismiss();
    }

    public AddToPlayListDialogFragment setCallback(Callback callback) {
        mCallback = callback;
        return this;
    }

    private static class AddPlayListAdapter extends ListAdapter<PlayList, PlayListItemView> {

        public AddPlayListAdapter(Context context, List<PlayList> data) {
            super(context, data);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder holder = super.onCreateViewHolder(parent, viewType);
            if (holder.itemView instanceof PlayListItemView) {
                PlayListItemView itemView = (PlayListItemView) holder.itemView;
                itemView.buttonAction.setVisibility(View.GONE);
            }
            return holder;
        }

        @Override
        protected PlayListItemView createView(Context context) {
            return new PlayListItemView(context);
        }
    }

    public interface Callback {
        void onPlayListSelected(PlayList playList);
    }
}
