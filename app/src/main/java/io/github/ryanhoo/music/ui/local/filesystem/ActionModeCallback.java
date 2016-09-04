package io.github.ryanhoo.music.ui.local.filesystem;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import io.github.ryanhoo.music.R;
import io.github.ryanhoo.music.utils.ViewUtils;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/4/16
 * Time: 5:40 PM
 * Desc: ActionModeCallback
 */
public class ActionModeCallback implements ActionMode.Callback {

    private static final int ANIMATION_DURATION = 350;
    private static final String STATUS_BAR_COLOR = "statusBarColor";

    private Activity context;
    private Window window;
    private ActionListener actionListener;

    private ActionMode actionMode;

    private Animator actionModeInAnimator, actionModeOutAnimator;

    private int statusBarColor;

    private boolean isShowing;

    public ActionModeCallback(Activity activity, ActionListener listener) {
        context = activity;
        window = activity.getWindow();
        actionListener = listener;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            statusBarColor = window.getStatusBarColor();
            int actionModeStatusBarColor = ContextCompat.getColor(context,
                    R.color.mp_theme_dark_blue_actionMode_statusBarColor);
            int startColor = Color.argb(
                    0,
                    Color.red(actionModeStatusBarColor),
                    Color.green(actionModeStatusBarColor),
                    Color.blue(actionModeStatusBarColor)
            );
            actionModeInAnimator = ObjectAnimator.ofObject(
                    window,
                    STATUS_BAR_COLOR,
                    new ArgbEvaluator(),
                    startColor,
                    actionModeStatusBarColor
            );
            actionModeOutAnimator = ObjectAnimator.ofObject(
                    window,
                    STATUS_BAR_COLOR,
                    new ArgbEvaluator(),
                    actionModeStatusBarColor,
                    startColor
            );
            actionModeInAnimator.setDuration(ANIMATION_DURATION);
            actionModeOutAnimator.setDuration(ANIMATION_DURATION);
        }
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        actionMode = mode;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            actionModeOutAnimator.cancel();
            actionModeInAnimator.setDuration(ANIMATION_DURATION).start();
            ViewUtils.setLightStatusBar(window.getDecorView());
        }
        mode.getMenuInflater().inflate(R.menu.file_system_action_mode, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        if (item.getItemId() == R.id.menu_item_done) {
            if (actionListener != null) {
                actionListener.onDoneAction();
            }
            // mode.finish();
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(statusBarColor);
            if (actionModeInAnimator != null) {
                actionModeInAnimator.cancel();
            }
            if (actionModeOutAnimator != null) {
                actionModeOutAnimator.start();
            }
            ViewUtils.clearLightStatusBar(window.getDecorView());
        }
        isShowing = false;
        if (actionListener != null) {
            actionListener.onDismissAction();
        }
    }

    public void updateSelectedItemCount(int selectedItemCount) {
        if (actionMode != null) {
            actionMode.setTitle(context.getResources().getQuantityString(
                    R.plurals.mp_selected_folders_formatter, selectedItemCount, selectedItemCount));
        }
    }

    public void dismiss() {
        if (actionMode != null) {
            actionMode.finish();
        }
    }

    public boolean isShowing() {
        return isShowing;
    }

    public void setShowing(boolean isShowing) {
        this.isShowing = isShowing;
    }

    public interface ActionListener {
        void onDismissAction();

        void onDoneAction();
    }
}
