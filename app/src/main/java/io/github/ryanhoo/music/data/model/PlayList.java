package io.github.ryanhoo.music.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.litesuits.orm.db.annotation.*;
import com.litesuits.orm.db.enums.AssignType;
import com.litesuits.orm.db.enums.Relation;
import io.github.ryanhoo.music.player.PlayMode;

import java.util.*;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/5/16
 * Time: 5:53 PM
 * Desc: PlayList
 */
@Table("playlist")
public class PlayList implements Parcelable {

    // Play List: Favorite
    public static final int NO_POSITION = -1;
    public static final String COLUMN_FAVORITE = "favorite";

    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private int id;

    private String name;

    private int numOfSongs;

    @Column(COLUMN_FAVORITE)
    private boolean favorite;

    private Date createdAt;

    private Date updatedAt;

    @MapCollection(ArrayList.class)
    @Mapping(Relation.OneToMany)
    private List<Song> songs = new ArrayList<>();

    @Ignore
    private int playingIndex = -1;

    /**
     * Use a singleton play mode
     */
    private PlayMode playMode = PlayMode.LOOP;

    public PlayList() {
        // EMPTY
    }

    public PlayList(Song song) {
        songs.add(song);
        numOfSongs = 1;
    }

    public PlayList(Parcel in) {
        readFromParcel(in);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumOfSongs() {
        return numOfSongs;
    }

    public void setNumOfSongs(int numOfSongs) {
        this.numOfSongs = numOfSongs;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @NonNull
    public List<Song> getSongs() {
        if (songs == null) {
            songs = new ArrayList<>();
        }
        return songs;
    }

    public void setSongs(@Nullable List<Song> songs) {
        if (songs == null) {
            songs = new ArrayList<>();
        }
        this.songs = songs;
    }

    public int getPlayingIndex() {
        return playingIndex;
    }

    public void setPlayingIndex(int playingIndex) {
        this.playingIndex = playingIndex;
    }

    public PlayMode getPlayMode() {
        return playMode;
    }

    public void setPlayMode(PlayMode playMode) {
        this.playMode = playMode;
    }

    // Parcelable

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeInt(this.numOfSongs);
        dest.writeByte(this.favorite ? (byte) 1 : (byte) 0);
        dest.writeLong(this.createdAt != null ? this.createdAt.getTime() : -1);
        dest.writeLong(this.updatedAt != null ? this.updatedAt.getTime() : -1);
        dest.writeTypedList(this.songs);
        dest.writeInt(this.playingIndex);
        dest.writeInt(this.playMode == null ? -1 : this.playMode.ordinal());
    }

    public void readFromParcel(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.numOfSongs = in.readInt();
        this.favorite = in.readByte() != 0;
        long tmpCreatedAt = in.readLong();
        this.createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
        long tmpUpdatedAt = in.readLong();
        this.updatedAt = tmpUpdatedAt == -1 ? null : new Date(tmpUpdatedAt);
        this.songs = in.createTypedArrayList(Song.CREATOR);
        this.playingIndex = in.readInt();
        int tmpPlayMode = in.readInt();
        this.playMode = tmpPlayMode == -1 ? null : PlayMode.values()[tmpPlayMode];
    }

    public static final Creator<PlayList> CREATOR = new Creator<PlayList>() {
        @Override
        public PlayList createFromParcel(Parcel source) {
            return new PlayList(source);
        }

        @Override
        public PlayList[] newArray(int size) {
            return new PlayList[size];
        }
    };

    // Utils

    public int getItemCount() {
        return songs == null ? 0 : songs.size();
    }

    public void addSong(@Nullable Song song) {
        if (song == null) return;

        songs.add(song);
        numOfSongs = songs.size();
    }

    public void addSong(@Nullable Song song, int index) {
        if (song == null) return;

        songs.add(index, song);
        numOfSongs = songs.size();
    }

    public void addSong(@Nullable List<Song> songs, int index) {
        if (songs == null || songs.isEmpty()) return;

        this.songs.addAll(index, songs);
        this.numOfSongs = this.songs.size();
    }

    public boolean removeSong(Song song) {
        if (song == null) return false;

        int index;
        if ((index = songs.indexOf(song)) != -1) {
            if (songs.remove(index) != null) {
                numOfSongs = songs.size();
                return true;
            }
        } else {
            for (Iterator<Song> iterator = songs.iterator(); iterator.hasNext(); ) {
                Song item = iterator.next();
                if (song.getPath().equals(item.getPath())) {
                    iterator.remove();
                    numOfSongs = songs.size();
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Prepare to play
     */
    public boolean prepare() {
        if (songs.isEmpty()) return false;
        if (playingIndex == NO_POSITION) {
            playingIndex = 0;
        }
        return true;
    }

    /**
     * The current song being played or is playing based on the {@link #playingIndex}
     */
    public Song getCurrentSong() {
        if (playingIndex != NO_POSITION) {
            return songs.get(playingIndex);
        }
        return null;
    }

    public boolean hasLast() {
        return songs != null && songs.size() != 0;
    }

    public Song last() {
        switch (playMode) {
            case LOOP:
            case LIST:
            case SINGLE:
                int newIndex = playingIndex - 1;
                if (newIndex < 0) {
                    newIndex = songs.size() - 1;
                }
                playingIndex = newIndex;
                break;
            case SHUFFLE:
                playingIndex = randomPlayIndex();
                break;
        }
        return songs.get(playingIndex);
    }

    /**
     * @return Whether has next song to play.
     * <p/>
     * If this query satisfies these conditions
     * - comes from media player's complete listener
     * - current play mode is PlayMode.LIST (the only limited play mode)
     * - current song is already in the end of the list
     * then there shouldn't be a next song to play, for this condition, it returns false.
     * <p/>
     * If this query is from user's action, such as from play controls, there should always
     * has a next song to play, for this condition, it returns true.
     */
    public boolean hasNext(boolean fromComplete) {
        if (songs.isEmpty()) return false;
        if (fromComplete) {
            if (playMode == PlayMode.LIST && playingIndex + 1 >= songs.size()) return false;
        }
        return true;
    }

    /**
     * Move the playingIndex forward depends on the play mode
     *
     * @return The next song to play
     */
    public Song next() {
        switch (playMode) {
            case LOOP:
            case LIST:
            case SINGLE:
                int newIndex = playingIndex + 1;
                if (newIndex >= songs.size()) {
                    newIndex = 0;
                }
                playingIndex = newIndex;
                break;
            case SHUFFLE:
                playingIndex = randomPlayIndex();
                break;
        }
        return songs.get(playingIndex);
    }

    private int randomPlayIndex() {
        int randomIndex = new Random().nextInt(songs.size());
        // Make sure not play the same song twice if there are at least 2 songs
        if (songs.size() > 1 && randomIndex == playingIndex) {
            randomPlayIndex();
        }
        return randomIndex;
    }

    public static PlayList fromFolder(@NonNull Folder folder) {
        PlayList playList = new PlayList();
        playList.setName(folder.getName());
        playList.setSongs(folder.getSongs());
        playList.setNumOfSongs(folder.getNumOfSongs());
        return playList;
    }
}
