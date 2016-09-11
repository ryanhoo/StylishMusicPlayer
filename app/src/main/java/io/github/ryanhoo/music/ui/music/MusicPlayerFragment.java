package io.github.ryanhoo.music.ui.music;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.ryanhoo.music.R;
import io.github.ryanhoo.music.RxBus;
import io.github.ryanhoo.music.data.model.PlayList;
import io.github.ryanhoo.music.data.model.Song;
import io.github.ryanhoo.music.data.source.PreferenceManager;
import io.github.ryanhoo.music.event.PlayListNowEvent;
import io.github.ryanhoo.music.event.PlaySongEvent;
import io.github.ryanhoo.music.player.PlayMode;
import io.github.ryanhoo.music.player.Player;
import io.github.ryanhoo.music.ui.base.BaseFragment;
import io.github.ryanhoo.music.ui.widget.ShadowImageView;
import io.github.ryanhoo.music.utils.TimeUtils;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/1/16
 * Time: 9:58 PM
 * Desc: MusicPlayerFragment
 */
public class MusicPlayerFragment extends BaseFragment implements Player.Callback {

    // private static final String TAG = "MusicPlayerFragment";

    // Update seek bar every second
    private static final long UPDATE_PROGRESS_INTERVAL = 1000;

    @BindView(R.id.image_view_album)
    ShadowImageView imageViewAlbum;
    @BindView(R.id.text_view_name)
    TextView textViewName;
    @BindView(R.id.text_view_artist)
    TextView textViewArtist;
    @BindView(R.id.text_view_progress)
    TextView textViewProgress;
    @BindView(R.id.text_view_duration)
    TextView textViewDuration;
    @BindView(R.id.seek_bar)
    SeekBar seekBarProgress;

    @BindView(R.id.button_play_mode_toggle)
    ImageView buttonPlayModeToggle;
    @BindView(R.id.button_play_toggle)
    ImageView buttonPlayToggle;

    private Player mPlayer;

    private Handler mHandler;

    private Runnable mProgressCallback = new Runnable() {
        @Override
        public void run() {
            if (isDetached()) return;

            if (mPlayer.isPlaying()) {
                int progress = (int) (seekBarProgress.getMax()
                        * ((float) mPlayer.getProgress() / (float) getCurrentSongDuration()));
                updateProgressTextWithDuration(mPlayer.getProgress());
                if (progress >= 0 && progress <= seekBarProgress.getMax()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        seekBarProgress.setProgress(progress, true);
                    } else {
                        seekBarProgress.setProgress(progress);
                    }
                    mHandler.postDelayed(this, UPDATE_PROGRESS_INTERVAL);
                }
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPlayer = Player.getInstance();
        mPlayer.setCallback(this);
        mHandler = new Handler();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_music, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        seekBarProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    updateProgressTextWithProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mHandler.removeCallbacks(mProgressCallback);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekTo(getDuration(seekBar.getProgress()));
                if (mPlayer.isPlaying()) {
                    mHandler.removeCallbacks(mProgressCallback);
                    mHandler.post(mProgressCallback);
                }
            }
        });
        // Retrieve last play mode
        PlayMode lastPlayMode = PreferenceManager.lastPlayMode(getActivity());
        buttonPlayModeToggle.setImageResource(getPlayModeDrawableRes(lastPlayMode));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mHandler.removeCallbacks(mProgressCallback);
    }

    // Click Events

    @OnClick(R.id.button_play_toggle)
    public void onPlayToggleAction(View view) {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
            buttonPlayToggle.setImageResource(R.drawable.ic_play);
            imageViewAlbum.pauseRotateAnimation();

            mHandler.removeCallbacks(mProgressCallback);
        } else {
            if (mPlayer.play()) {
                imageViewAlbum.resumeRotateAnimation();
                buttonPlayToggle.setImageResource(R.drawable.ic_pause);

                mHandler.removeCallbacks(mProgressCallback);
                mHandler.post(mProgressCallback);
            }
        }
    }

    @OnClick(R.id.button_play_mode_toggle)
    public void onPlayModeToggleAction(View view) {
        PlayMode current = PreferenceManager.lastPlayMode(getActivity());
        PlayMode newMode = PlayMode.switchNextMode(current);
        PreferenceManager.setPlayMode(getActivity(), newMode);
        mPlayer.setPlayMode(newMode);
        buttonPlayModeToggle.setImageResource(getPlayModeDrawableRes(newMode));
    }

    @OnClick(R.id.button_play_last)
    public void onPlayLastAction(View view) {
        mPlayer.playLast();
    }

    @OnClick(R.id.button_play_next)
    public void onPlayNextAction(View view) {
        mPlayer.playNext();
    }

    @OnClick(R.id.button_favorite_toggle)
    public void onFavoriteToggleAction(View view) {

    }

    // RXBus Events

    @Override
    protected Subscription subscribeEvents() {
        return RxBus.getInstance().toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        if (o instanceof PlaySongEvent) {
                            onPlaySongEvent((PlaySongEvent) o);
                        } else if (o instanceof PlayListNowEvent) {
                            onPlayListNowEvent((PlayListNowEvent) o);
                        }
                    }
                })
                .subscribe(RxBus.defaultSubscriber());
    }

    private void onPlaySongEvent(PlaySongEvent event) {
        Song song = event.song;
        playSong(song);
    }

    private void onPlayListNowEvent(PlayListNowEvent event) {
        PlayList playList = event.playList;
        playList.setPlayMode(PreferenceManager.lastPlayMode(getActivity()));
        int playIndex = event.playIndex;
        playSong(playList, playIndex);
    }

    // Music Controls

    private void playSong(Song song) {
        PlayList playList = new PlayList(song);
        playList.setPlayMode(PreferenceManager.lastPlayMode(getActivity()));
        playSong(playList, 0);
    }

    private void playSong(PlayList playList, int playIndex) {
        if (playList == null) return;

        boolean result = mPlayer.play(playList, playIndex);

        Song song = playList.getCurrentSong();
        onSongUpdated(song);

        seekBarProgress.setProgress(0);
        seekBarProgress.setEnabled(result);
        textViewProgress.setText(R.string.mp_music_default_duration);

        if (result) {
            imageViewAlbum.startRotateAnimation();
            buttonPlayToggle.setImageResource(R.drawable.ic_pause);
            textViewDuration.setText(TimeUtils.formatDuration(song.getDuration()));
        } else {
            buttonPlayToggle.setImageResource(R.drawable.ic_play);
            textViewDuration.setText(R.string.mp_music_default_duration);
        }

        mHandler.removeCallbacks(mProgressCallback);
        mHandler.post(mProgressCallback);
    }

    private void onSongUpdated(Song song) {
        if (song == null) return;

        textViewName.setText(song.getDisplayName());
        textViewArtist.setText(song.getArtist());
        textViewDuration.setText(TimeUtils.formatDuration(song.getDuration()));
    }

    private void updateProgressTextWithProgress(int progress) {
        int targetDuration = getDuration(progress);
        textViewProgress.setText(TimeUtils.formatDuration(targetDuration));
    }

    private void updateProgressTextWithDuration(int duration) {
        textViewProgress.setText(TimeUtils.formatDuration(duration));
    }

    private void seekTo(int duration) {
        mPlayer.seekTo(duration);
    }

    private int getDuration(int progress) {
        return (int) (getCurrentSongDuration() * ((float) progress / seekBarProgress.getMax()));
    }

    private int getCurrentSongDuration() {
        Song currentSong = mPlayer.getPlayingSong();
        int duration = 0;
        if (currentSong != null) {
            duration = currentSong.getDuration();
        }
        return duration;
    }

    @DrawableRes
    private int getPlayModeDrawableRes(PlayMode playMode) {
        if (playMode == null) {
            playMode = PlayMode.getDefault();
        }
        switch (playMode) {
            case LIST:
                return R.drawable.ic_play_mode_list;
            case LOOP:
                return R.drawable.ic_play_mode_loop;
            case SHUFFLE:
                return R.drawable.ic_play_mode_shuffle;
            case SINGLE:
                return R.drawable.ic_play_mode_single;
        }
        return R.drawable.ic_play_mode_loop;
    }

    // Player Callbacks

    @Override
    public void onSwitchLast(Song last) {

    }

    @Override
    public void onSwitchNext(Song next) {
        onSongUpdated(next);
    }

    @Override
    public void onComplete(Song next) {
        if (next == null) {
            imageViewAlbum.cancelRotateAnimation();
            buttonPlayToggle.setImageResource(R.drawable.ic_play);
            seekBarProgress.setProgress(0);
            updateProgressTextWithProgress(0);
            seekTo(0);
        } else {
            onSongUpdated(next);
        }
    }
}
