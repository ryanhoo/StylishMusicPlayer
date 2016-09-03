package io.github.ryanhoo.music.ui.local.filesystem;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import java.io.FilenameFilter;
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

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    SystemFileAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_files);
        ButterKnife.bind(this);
        supportActionBar(toolbar);

        mAdapter = new SystemFileAdapter(this, null);
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // TODO
            }
        });
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new DefaultDividerDecoration());

        loadFiles();
    }

    private void loadFiles() {
        final File SDCARD = Environment.getExternalStorageDirectory();
        Subscription subscription = Observable.just(SDCARD)
                .flatMap(new Func1<File, Observable<List<File>>>() {
                    @Override
                    public Observable<List<File>> call(File file) {
                        List<File> files = Arrays.asList(SDCARD.listFiles(new FilenameFilter() {
                            @Override
                            public boolean accept(File dir, String name) {
                                // Ignore system files/folders start with ., such as .android, .git
                                return !name.startsWith(".");
                            }
                        }));
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
                        // Empty
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e(TAG, "onError: ", throwable);
                    }

                    @Override
                    public void onNext(List<File> files) {
                        onFilesLoaded(files);
                    }
                });
        addSubscription(subscription);
    }

    private void onFilesLoaded(List<File> files) {
        mAdapter.setData(files);
        mAdapter.notifyDataSetChanged();
    }
}
