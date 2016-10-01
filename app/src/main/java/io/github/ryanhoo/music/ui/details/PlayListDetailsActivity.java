package io.github.ryanhoo.music.ui.details;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.ryanhoo.music.R;
import io.github.ryanhoo.music.RxBus;
import io.github.ryanhoo.music.data.model.Folder;
import io.github.ryanhoo.music.data.model.PlayList;
import io.github.ryanhoo.music.data.model.Song;
import io.github.ryanhoo.music.data.source.AppRepository;
import io.github.ryanhoo.music.event.PlayListNowEvent;
import io.github.ryanhoo.music.ui.base.BaseActivity;
import io.github.ryanhoo.music.ui.base.adapter.OnItemClickListener;
import io.github.ryanhoo.music.ui.common.DefaultDividerDecoration;
import io.github.ryanhoo.music.ui.playlist.AddToPlayListDialogFragment;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/11/16
 * Time: 6:34 AM
 * Desc: PlayListDetailsActivity
 */
public class PlayListDetailsActivity extends BaseActivity implements PlayListDetailsContract.View, SongAdapter.ActionCallback {

    private static final String TAG = "PlayListDetailsActivity";

    public static final String EXTRA_FOLDER = "extraFolder";
    public static final String EXTRA_PLAY_LIST = "extraPlayList";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.text_view_empty)
    View emptyView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    boolean isFolder;
    PlayList mPlayList;

    SongAdapter mAdapter;

    PlayListDetailsContract.Presenter mPresenter;
    int mDeleteIndex;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Folder folder = getIntent().getParcelableExtra(EXTRA_FOLDER);
        mPlayList = getIntent().getParcelableExtra(EXTRA_PLAY_LIST);
        if (folder == null && mPlayList == null) {
            Log.e(TAG, "onCreate: folder & play list can't be both null!");
            finish();
        }
        if (folder != null) {
            isFolder = true;
            mPlayList = PlayList.fromFolder(folder);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list_details);
        ButterKnife.bind(this);
        supportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(mPlayList.getName());
        }

        mAdapter = new SongAdapter(this, mPlayList.getSongs());
        mAdapter.setActionCallback(this);
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                RxBus.getInstance().post(new PlayListNowEvent(mPlayList, position));
            }
        });
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new DefaultDividerDecoration());
        emptyView.setVisibility(mPlayList.getNumOfSongs() > 0 ? View.GONE : View.VISIBLE);

        new PlayListDetailsPresenter(AppRepository.getInstance(), this).subscribe();
    }

    @Override
    protected void onDestroy() {
        mPresenter.unsubscribe();
        super.onDestroy();
    }

    public static Intent launchIntentForFolder(Context context, Folder folder) {
        Intent intent = new Intent(context, PlayListDetailsActivity.class);
        intent.putExtra(EXTRA_FOLDER, folder);
        return intent;
    }

    public static Intent launchIntentForPlayList(Context context, PlayList playList) {
        Intent intent = new Intent(context, PlayListDetailsActivity.class);
        intent.putExtra(EXTRA_PLAY_LIST, playList);
        return intent;
    }

    // Adapter Action Callback

    @Override
    public void onAction(View actionView, final int position) {
        final Song song = mAdapter.getItem(position);
        PopupMenu actionMenu = new PopupMenu(this, actionView, Gravity.END | Gravity.BOTTOM);
        actionMenu.inflate(R.menu.music_action);
        actionMenu.getMenu().findItem(R.id.menu_item_delete).setVisible(!isFolder);
        actionMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_item_add_to_play_list:
                        new AddToPlayListDialogFragment()
                                .setCallback(new AddToPlayListDialogFragment.Callback() {
                                    @Override
                                    public void onPlayListSelected(PlayList playList) {
                                        if (playList.getId() == mPlayList.getId()) return;
                                        mPresenter.addSongToPlayList(song, playList);
                                    }
                                })
                                .show(getSupportFragmentManager().beginTransaction(), "AddToPlayList");
                        break;
                    case R.id.menu_item_delete:
                        mDeleteIndex = position;
                        mPresenter.delete(song, mPlayList);
                        break;
                }
                return true;
            }
        });
        actionMenu.show();
    }

    // MVP View

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void handleError(Throwable e) {
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSongDeleted(Song song) {
        mAdapter.notifyItemRemoved(mDeleteIndex);
        mAdapter.updateSummaryText();
    }

    @Override
    public void setPresenter(PlayListDetailsContract.Presenter presenter) {
        mPresenter = presenter;
    }
}
