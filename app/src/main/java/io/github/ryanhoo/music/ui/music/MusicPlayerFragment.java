package io.github.ryanhoo.music.ui.music;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
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
import io.github.ryanhoo.music.data.model.Song;
import io.github.ryanhoo.music.event.PlaySongEvent;
import io.github.ryanhoo.music.player.Player;
import io.github.ryanhoo.music.ui.base.BaseFragment;
import io.github.ryanhoo.music.ui.widget.AlbumImageView;
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

    private static final String TAG = "MusicPlayerFragment";

    @BindView(R.id.image_view_album)
    AlbumImageView imageViewAlbum;
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

    @BindView(R.id.button_play_toggle)
    ImageView buttonPlayToggle;

    Player mPlayer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPlayer = Player.getInstance();
        mPlayer.setCallback(this);
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
                    updateProgressText(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Empty
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekTo(getDuration(seekBar.getProgress()));
            }
        });
    }

    // Click Events

    @OnClick(R.id.button_play_toggle)
    public void onPlayToggleAction(View view) {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
            buttonPlayToggle.setImageResource(R.drawable.ic_play);
            imageViewAlbum.pauseRotateAnimation();
        } else {
            if (mPlayer.play()) {
                imageViewAlbum.resumeRotateAnimation();
                buttonPlayToggle.setImageResource(R.drawable.ic_pause);
            }
        }
    }

    @OnClick(R.id.button_play_mode_toggle)
    public void onPlayModeToggleAction(View view) {

    }

    @OnClick(R.id.button_play_last)
    public void onPlayLastAction(View view) {

    }

    @OnClick(R.id.button_play_next)
    public void onPlayNextAction(View view) {

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
                        }
                    }
                })
                .subscribe(RxBus.defaultSubscriber());
    }

    private void onPlaySongEvent(PlaySongEvent event) {
        Song song = event.song;
        playSong(song);
    }

    // Music Controls

    private void playSong(Song song) {
        if (song == null) return;

        boolean result = mPlayer.play(song);
        Log.d(TAG, String.format("onPlaySongEvent: %s at path: %s", result ? "success" : "failure", song.getPath()));

        textViewName.setText(song.getDisplayName());
        textViewArtist.setText(song.getArtist());

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
    }

    private void updateProgressText(int progress) {
        int targetDuration = getDuration(progress);
        textViewProgress.setText(TimeUtils.formatDuration(targetDuration));
    }

    private void seekTo(int duration) {
        mPlayer.seekTo(duration);
    }

    private int getDuration(int progress) {
        return (int) (mPlayer.getPlayingSong().getDuration() * ((float) progress / 100));
    }

    // Player Callbacks

    @Override
    public void onComplete(Song completed, Song next) {
        if (next == null) {
            imageViewAlbum.cancelRotateAnimation();
            buttonPlayToggle.setImageResource(R.drawable.ic_play);
            seekTo(0);
        } else {
            playSong(next);
        }
    }
}
