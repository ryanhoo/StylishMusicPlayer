package io.github.ryanhoo.music.ui.playlist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.ryanhoo.music.R;
import io.github.ryanhoo.music.data.model.PlayList;
import io.github.ryanhoo.music.ui.base.BaseFragment;
import io.github.ryanhoo.music.ui.base.adapter.OnItemClickListener;
import io.github.ryanhoo.music.ui.common.DefaultDividerDecoration;
import io.github.ryanhoo.music.utils.DBUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/1/16
 * Time: 9:58 PM
 * Desc: PlayListFragment
 */
public class PlayListFragment extends BaseFragment implements EditPlayListDialogFragment.Callback {


    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    PlayListAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_play_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        PlayList favorite = DBUtils.generateFavoritePlayList(getActivity());
        List<PlayList> list = new ArrayList<>();
        list.add(favorite);

        mAdapter = new PlayListAdapter(getActivity(), new ArrayList<>(list));
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // TODO
                Toast.makeText(getActivity(), "List Item " + position, Toast.LENGTH_SHORT).show();
            }
        });
        mAdapter.setAddPlayListCallback(new PlayListAdapter.AddPlayListCallback() {
            @Override
            public void onAddPlayList() {
                EditPlayListDialogFragment.createPlayList()
                        .setCallback(PlayListFragment.this)
                        .show(getFragmentManager().beginTransaction(), "CreatePlayList");
            }
        });
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new DefaultDividerDecoration());
    }

    // Create or Edit Play List Callbacks

    @Override
    public void onCreated(PlayList playList) {
        mAdapter.getData().add(playList);
        mAdapter.notifyItemInserted(mAdapter.getData().size() - 1);
        mAdapter.updateFooterView();
    }

    @Override
    public void onEdited(PlayList playList) {

    }
}
