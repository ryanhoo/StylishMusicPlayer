package io.github.ryanhoo.music.ui.details;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.ryanhoo.music.R;
import io.github.ryanhoo.music.data.model.Folder;
import io.github.ryanhoo.music.data.model.PlayList;
import io.github.ryanhoo.music.ui.base.BaseActivity;
import io.github.ryanhoo.music.ui.common.DefaultDividerDecoration;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/11/16
 * Time: 6:34 AM
 * Desc: PlayListDetailsActivity
 */
public class PlayListDetailsActivity extends BaseActivity {

    private static final String TAG = "PlayListDetailsActivity";

    public static final String EXTRA_FOLDER = "extraFolder";
    public static final String EXTRA_PLAY_LIST = "extraPlayList";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.text_view_empty)
    View emptyView;

    PlayList mPlayList;

    SongAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Folder folder = getIntent().getParcelableExtra(EXTRA_FOLDER);
        mPlayList = getIntent().getParcelableExtra(EXTRA_PLAY_LIST);
        if (folder == null && mPlayList == null) {
            Log.e(TAG, "onCreate: folder & play list can't be both null!");
            finish();
        }
        if (folder != null) {
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
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new DefaultDividerDecoration());
        emptyView.setVisibility(mPlayList.getNumOfSongs() > 0 ? View.GONE : View.VISIBLE);
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
}
