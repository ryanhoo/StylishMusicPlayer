package io.github.ryanhoo.music.data.model;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/3/16
 * Time: 7:19 PM
 * Desc: Folder
 */
public class Folder {

    private String name;

    private String path;

    public Folder() {
        // Empty
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
}
