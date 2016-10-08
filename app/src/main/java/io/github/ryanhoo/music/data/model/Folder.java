package io.github.ryanhoo.music.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.litesuits.orm.db.annotation.*;
import com.litesuits.orm.db.enums.AssignType;
import com.litesuits.orm.db.enums.Relation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/3/16
 * Time: 7:19 PM
 * Desc: Folder
 */
@Table("folder")
public class Folder implements Parcelable {

    public static final String COLUMN_NAME = "name";

    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private int id;

    @Column(COLUMN_NAME)
    private String name;

    @Unique
    private String path;

    private int numOfSongs;

    @MapCollection(ArrayList.class)
    @Mapping(Relation.OneToMany)
    private List<Song> songs = new ArrayList<>();

    private Date createdAt;

    public Folder() {
        // Empty
    }

    public Folder(Parcel in) {
        readFromParcel(in);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Folder(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getNumOfSongs() {
        return numOfSongs;
    }

    public void setNumOfSongs(int numOfSongs) {
        this.numOfSongs = numOfSongs;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.path);
        dest.writeInt(this.numOfSongs);
        dest.writeTypedList(this.songs);
        dest.writeLong(this.createdAt != null ? this.createdAt.getTime() : -1);
    }

    private void readFromParcel(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.path = in.readString();
        this.numOfSongs = in.readInt();
        this.songs = in.createTypedArrayList(Song.CREATOR);
        long tmpCreatedAt = in.readLong();
        this.createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
    }

    public static final Creator<Folder> CREATOR = new Creator<Folder>() {
        @Override
        public Folder createFromParcel(Parcel source) {
            return new Folder(source);
        }

        @Override
        public Folder[] newArray(int size) {
            return new Folder[size];
        }
    };
}
