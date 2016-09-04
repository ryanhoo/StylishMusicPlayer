package io.github.ryanhoo.music.ui.local.folder;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.ryanhoo.music.R;
import io.github.ryanhoo.music.RxBus;
import io.github.ryanhoo.music.data.model.Folder;
import io.github.ryanhoo.music.event.AddFolderEvent;
import io.github.ryanhoo.music.ui.base.BaseFragment;
import io.github.ryanhoo.music.ui.base.adapter.OnItemClickListener;
import io.github.ryanhoo.music.ui.common.DefaultDividerDecoration;
import io.github.ryanhoo.music.ui.local.filesystem.FileSystemActivity;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/3/16
 * Time: 7:29 PM
 * Desc: AddedFolderFragment
 */
public class AddedFolderFragment extends BaseFragment {

    private static final String TAG = "AddedFolderFragment";

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
                startActivity(new Intent(getActivity(), FileSystemActivity.class));
            }
        });
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new DefaultDividerDecoration());
    }

    // RxBus Events

    @Override
    protected Subscription subscribeEvents() {
        return RxBus.getInstance().toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        if (o instanceof AddFolderEvent) {
                            onAddFolders((AddFolderEvent) o);
                        }
                    }
                })
                .subscribe(RxBus.defaultSubscriber());
    }

    private void onAddFolders(AddFolderEvent event) {
        final List<File> folders = event.folders;
        final List<Folder> existedFolders = mAdapter.getData();
        Subscription subscription = Observable.from(folders)
                .filter(new Func1<File, Boolean>() {
                    @Override
                    public Boolean call(File file) {
                        for (Folder folder : existedFolders) {
                            if (file.getAbsolutePath().equals(folder.getPath())) {
                                return false;
                            }
                        }
                        return true;
                    }
                })
                .flatMap(new Func1<File, Observable<Folder>>() {
                    @Override
                    public Observable<Folder> call(File file) {
                        Folder folder = new Folder();
                        folder.setName(file.getName());
                        folder.setPath(file.getAbsolutePath());
                        return Observable.just(folder);
                    }
                })
                .toList()
                .doOnNext(new Action1<List<Folder>>() {
                    @Override
                    public void call(List<Folder> folders) {
                        existedFolders.addAll(folders);
                        Collections.sort(existedFolders, new Comparator<Folder>() {
                            @Override
                            public int compare(Folder f1, Folder f2) {
                                return f1.getName().compareToIgnoreCase(f2.getName());
                            }
                        });
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Folder>>() {
                    @Override
                    public void onCompleted() {
                        // Empty
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e(TAG, "onError: ", throwable);
                    }

                    @Override
                    public void onNext(List<Folder> newFolders) {
                        mAdapter.notifyDataSetChanged();
                    }
                });
        addSubscription(subscription);
    }
}
