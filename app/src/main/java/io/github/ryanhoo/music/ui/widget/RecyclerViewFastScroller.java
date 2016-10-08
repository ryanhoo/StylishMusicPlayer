package io.github.ryanhoo.music.ui.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import io.github.ryanhoo.music.R;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/2/16
 * Time: 9:17 PM
 * Desc: FastScroller
 */
public class RecyclerViewFastScroller extends LinearLayout {

    // private static final String TAG = "FastScroller";

    private static final int BUBBLE_ANIMATION_DURATION = 100;

    private TextView bubbleView;
    private View fastScroll;
    private RecyclerView recyclerView;
    private ObjectAnimator currentAnimator = null;

    private boolean isDragging = false;

    private final RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(final RecyclerView recyclerView, final int dx, final int dy) {
            if (!isDragging) {
                updateBubbleAndHandlePosition();
            }
        }
    };

    public interface BubbleTextGetter {
        String getTextToShowInBubble(int position);
    }

    public RecyclerViewFastScroller(final Context context) {
        this(context, null);
    }

    public RecyclerViewFastScroller(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecyclerViewFastScroller(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected void init() {
        setOrientation(HORIZONTAL);
        setClipChildren(false);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        bubbleView = (TextView) findViewById(R.id.bubble);
        bubbleView.setVisibility(View.GONE);
        fastScroll = findViewById(R.id.handle);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updateBubbleAndHandlePosition();
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        final int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (event.getX() < fastScroll.getX() - ViewCompat.getPaddingStart(fastScroll))
                    return false;
                if (currentAnimator != null)
                    currentAnimator.cancel();
                if (bubbleView != null && bubbleView.getVisibility() == GONE)
                    showBubble();
                fastScroll.setSelected(true);
            case MotionEvent.ACTION_MOVE:
                isDragging = true;
                setBubbleAndHandlePosition(event.getY());
                setRecyclerViewPosition(event.getY());
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isDragging = false;
                fastScroll.setSelected(false);
                hideBubble();
                return true;
        }
        return super.onTouchEvent(event);
    }

    public void setRecyclerView(@NonNull RecyclerView recyclerView) {
        if (this.recyclerView != recyclerView) {
            if (this.recyclerView != null)
                this.recyclerView.removeOnScrollListener(onScrollListener);
            this.recyclerView = recyclerView;
            recyclerView.addOnScrollListener(onScrollListener);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (recyclerView != null) {
            recyclerView.removeOnScrollListener(onScrollListener);
            recyclerView = null;
        }
    }

    private void setRecyclerViewPosition(float y) {
        if (recyclerView != null) {
            final int itemCount = recyclerView.getAdapter().getItemCount();
            float proportion;
            proportion = y / (float) (getHeight() - getPaddingTop());

            final int verticalScrollOffset = recyclerView.computeVerticalScrollOffset();
            final int verticalScrollRange = recyclerView.computeVerticalScrollRange()
                    - recyclerView.getHeight() + recyclerView.getPaddingTop() + recyclerView.getPaddingBottom();

            float offset = verticalScrollRange * proportion - verticalScrollOffset + 0.5f;
            recyclerView.scrollBy(0, (int) offset);

            /*
            Log.d(TAG, String.format("recyclerView[scrollOffset: %d, scrollRange: %d] " +
                            "fastScroll[y: %.2f] proportion: %.2f, height: %d",
                    verticalScrollOffset, verticalScrollRange, fastScroll.getY(), proportion, getHeight()));
            */
            final int targetPos = getValueInRange(0, itemCount - 1, (int) (proportion * (float) itemCount));
            final String bubbleText = ((BubbleTextGetter) recyclerView.getAdapter()).getTextToShowInBubble(targetPos);
            if (bubbleView != null)
                bubbleView.setText(bubbleText);
        }
    }

    private int getValueInRange(int min, int max, int value) {
        int minimum = Math.max(min, value);
        return Math.min(minimum, max);
    }

    private void updateBubbleAndHandlePosition() {
        if (recyclerView == null || bubbleView == null || fastScroll.isSelected())
            return;

        final int verticalScrollOffset = recyclerView.computeVerticalScrollOffset();
        final int verticalScrollRange = recyclerView.computeVerticalScrollRange();
        float proportion = (float) verticalScrollOffset / ((float) verticalScrollRange - getHeight());
        setBubbleAndHandlePosition(getHeight() * proportion);
    }

    private void setBubbleAndHandlePosition(float y) {
        final int handleHeight = fastScroll.getHeight();
        fastScroll.setY(getValueInRange(
                getPaddingTop(),
                getHeight() - handleHeight - getPaddingBottom(),
                (int) (y - handleHeight / 2)
        ));
        if (bubbleView != null) {
            int bubbleHeight = bubbleView.getHeight();
            bubbleView.setY(getValueInRange(
                    getPaddingTop(),
                    getHeight() - bubbleHeight - handleHeight / 2 - getPaddingBottom(),
                    (int) (y - bubbleHeight)
            ));
        }
    }

    private void showBubble() {
        if (bubbleView == null)
            return;
        bubbleView.setVisibility(VISIBLE);
        if (currentAnimator != null)
            currentAnimator.cancel();
        currentAnimator = ObjectAnimator.ofFloat(bubbleView, "alpha", 0f, 1f).setDuration(BUBBLE_ANIMATION_DURATION);
        currentAnimator.start();
    }

    private void hideBubble() {
        if (bubbleView == null)
            return;
        if (currentAnimator != null)
            currentAnimator.cancel();
        currentAnimator = ObjectAnimator.ofFloat(bubbleView, "alpha", 1f, 0f).setDuration(BUBBLE_ANIMATION_DURATION);
        currentAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                bubbleView.setVisibility(GONE);
                currentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                bubbleView.setVisibility(GONE);
                currentAnimator = null;
            }
        });
        currentAnimator.start();
    }
}
