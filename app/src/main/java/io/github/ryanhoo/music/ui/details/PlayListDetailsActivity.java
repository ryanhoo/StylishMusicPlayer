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

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.text_view_empty)
    View emptyView;

    Folder mFolder;

    SongAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mFolder = getIntent().getParcelableExtra(EXTRA_FOLDER);
        if (mFolder == null) {
            Log.e(TAG, "onCreate: folder can't be null!");
            finish();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list_details);
        ButterKnife.bind(this);
        supportActionBar(toolbar);
        toolbar.setTitle(mFolder.getName());

        mAdapter = new SongAdapter(this, mFolder.getSongs());
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new DefaultDividerDecoration());
        emptyView.setVisibility(mFolder.getNumOfSongs() > 0 ? View.GONE : View.VISIBLE);
    }

    public static Intent launchIntentForFolder(Context context, Folder folder) {
        Intent intent = new Intent(context, PlayListDetailsActivity.class);
        intent.putExtra(EXTRA_FOLDER, folder);
        return intent;
    }
}
