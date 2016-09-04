package io.github.ryanhoo.music.ui.local.filesystem;

import android.support.annotation.UiThread;

import java.io.File;
import java.util.List;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/4/16
 * Time: 12:58 AM
 * Desc: FileTreeStack
 */
@UiThread
public class FileTreeStack {

    private Node mFirstNode;
    private int mSize;

    public FileTreeSnapshot pop() {
        if (mFirstNode == null) return null;
        FileTreeSnapshot snapshot = mFirstNode.snapshot;
        mFirstNode = mFirstNode.next;
        mSize--;
        return snapshot;
    }

    public void push(FileTreeSnapshot snapshot) {
        Node node = new Node();
        node.snapshot = snapshot;
        node.next = mFirstNode;
        mFirstNode = node;
        mSize++;
    }

    public int size() {
        return mSize;
    }

    static class Node {
        FileTreeSnapshot snapshot;
        Node next;
    }

    static class FileTreeSnapshot {
        public File parent;
        public List<FileWrapper> files;
        public int scrollOffset;
    }
}
