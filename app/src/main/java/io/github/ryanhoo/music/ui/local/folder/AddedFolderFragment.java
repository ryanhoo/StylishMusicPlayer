package io.github.ryanhoo.music.ui.local.folder;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.ryanhoo.music.R;
import io.github.ryanhoo.music.data.model.Folder;
import io.github.ryanhoo.music.ui.base.BaseFragment;
import io.github.ryanhoo.music.ui.base.adapter.OnItemClickListener;
import io.github.ryanhoo.music.ui.common.DefaultDividerDecoration;
import io.github.ryanhoo.music.ui.local.filesystem.SystemFileActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/3/16
 * Time: 7:29 PM
 * Desc: AddedFolderFragment
 */
public class AddedFolderFragment extends BaseFragment {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    static List<Folder> DEFAULT_FOLDERS;

    AddedFolderAdapter mAdapter;

    static {
        DEFAULT_FOLDERS = new ArrayList<>(3);
        final String SDCARD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
        final String DOWNLOADS_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        final String MUSIC_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath();
        DEFAULT_FOLDERS.add(new Folder("Downloads", DOWNLOADS_PATH));
        DEFAULT_FOLDERS.add(new Folder("Music", MUSIC_PATH));
        DEFAULT_FOLDERS.add(new Folder("SDCard", SDCARD_PATH));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_added_folders, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        mAdapter = new AddedFolderAdapter(getActivity(), DEFAULT_FOLDERS);
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // TODO
            }
        });
        mAdapter.setAddFolderCallback(new AddedFolderAdapter.AddFolderCallback() {
            @Override
            public void onAddFolder() {
                startActivity(new Intent(getActivity(), SystemFileActivity.class));
            }
        });
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new DefaultDividerDecoration());
    }
}
