package io.github.ryanhoo.music.ui.local.filesystem;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.ryanhoo.music.R;
import io.github.ryanhoo.music.ui.base.BaseActivity;
import io.github.ryanhoo.music.ui.base.adapter.OnItemClickListener;
import io.github.ryanhoo.music.ui.common.DefaultDividerDecoration;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/3/16
 * Time: 11:31 PM
 * Desc: SystemFileActivity
 */
public class SystemFileActivity extends BaseActivity {

    private static final String TAG = "SystemFileActivity";

    final File SDCARD = Environment.getExternalStorageDirectory();

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.text_view_empty)
    View emptyView;

    SystemFileAdapter mAdapter;
    FileTreeStack mFileTreeStack;

    File mFileParent;
    List<File> mFiles;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_files);
        ButterKnife.bind(this);
        supportActionBar(toolbar);

        mFileTreeStack = new FileTreeStack();
        mAdapter = new SystemFileAdapter(this, null);
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                File file = mAdapter.getItem(position);
                if (file.isDirectory()) {
                    storeSnapshot();
                    toolbar.setTitle(getToolbarTitle(file));
                    loadFiles(file);
                }
            }
        });
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new DefaultDividerDecoration());

        loadFiles(SDCARD);
    }

    // Handle stack back events
    @Override
    public void onBackPressed() {
        if (mFileTreeStack.size() == 0) {
            super.onBackPressed();
        } else {
            restoreSnapshot(mFileTreeStack.pop());
        }
    }

    // FileTreeSnapshot

    private void storeSnapshot() {
        FileTreeStack.FileTreeSnapshot snapshot = new FileTreeStack.FileTreeSnapshot();
        snapshot.parent = mFileParent;
        snapshot.files = mFiles;
        snapshot.scrollOffset = recyclerView.computeVerticalScrollOffset();
        mFileTreeStack.push(snapshot);
    }

    private void restoreSnapshot(FileTreeStack.FileTreeSnapshot snapshot) {
        final File parent = snapshot.parent;
        final List<File> files = snapshot.files;
        final int scrollOffset = snapshot.scrollOffset;

        mFileParent = parent;
        mFiles = files;

        final int oldScrollOffset = recyclerView.computeVerticalScrollOffset();

        toolbar.setTitle(getToolbarTitle(parent));
        mAdapter.setData(files);
        mAdapter.notifyDataSetChanged();
        toggleEmptyViewVisibility();

        recyclerView.scrollBy(0, scrollOffset - oldScrollOffset);
    }

    private String getToolbarTitle(File parent) {
        return parent.getAbsolutePath().equals(SDCARD.getAbsolutePath()) ? "SDCard" : parent.getName();
    }

    // Load files

    private void loadFiles(final File parent) {
        Subscription subscription = Observable.just(parent)
                .flatMap(new Func1<File, Observable<List<File>>>() {
                    @Override
                    public Observable<List<File>> call(File file) {
                        List<File> files = Arrays.asList(parent.listFiles(SystemFileFilter.DEFAULT_INSTANCE));
                        Collections.sort(files, new Comparator<File>() {
                            @Override
                            public int compare(File f1, File f2) {
                                if (f1.isDirectory() && f2.isFile()) {
                                    return -1;
                                }
                                if (f2.isDirectory() && f1.isFile()) {
                                    return 1;
                                }
                                return f1.getName().compareToIgnoreCase(f2.getName());
                            }
                        });
                        return Observable.just(files);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<File>>() {
                    @Override
                    public void onCompleted() {
                        toggleEmptyViewVisibility();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e(TAG, "onError: ", throwable);
                    }

                    @Override
                    public void onNext(List<File> files) {
                        onFilesLoaded(parent, files);
                    }
                });
        addSubscription(subscription);
    }

    private void onFilesLoaded(File parent, List<File> files) {
        mFileParent = parent;
        mFiles = files;
        mAdapter.setData(files);
        mAdapter.notifyDataSetChanged();
        recyclerView.scrollTo(0, 0);
    }

    private void toggleEmptyViewVisibility() {
        emptyView.setVisibility(mAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }
}
