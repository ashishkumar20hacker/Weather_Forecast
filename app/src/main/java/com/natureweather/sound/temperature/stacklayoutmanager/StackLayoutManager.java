package com.natureweather.sound.temperature.stacklayoutmanager;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IntRange;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class StackLayoutManager extends RecyclerView.LayoutManager {

    private enum FlingOrientation {
        NONE, LEFT_TO_RIGHT, RIGHT_TO_LEFT, TOP_TO_BOTTOM, BOTTOM_TO_TOP
    }

    public enum ScrollOrientation {
        LEFT_TO_RIGHT, RIGHT_TO_LEFT, TOP_TO_BOTTOM, BOTTOM_TO_TOP
    }

    private int mVisibleItemCount;
    private ScrollOrientation mScrollOrientation;
    private int mScrollOffset;

    private RecyclerView.OnScrollListener mOnScrollListener;
    private RecyclerView.OnFlingListener mOnFlingListener;

    private StackAnimation mAnimation;
    private StackLayout mLayout;

    private boolean mPagerMode = true;
    private int mPagerFlingVelocity = 0;
    private boolean mFixScrolling = false;
    private FlingOrientation mFlingOrientation = FlingOrientation.NONE;
    private int itemPosition = 0;
    private boolean isItemPositionChanged = false;
    private ItemChangedListener itemChangedListener;

    public interface ItemChangedListener {
        void onItemChanged(int position);
    }

    public StackLayoutManager(ScrollOrientation scrollOrientation,
                              int visibleCount,
                              Class<? extends StackAnimation> animation,
                              Class<? extends StackLayout> layout) {
        mVisibleItemCount = visibleCount;
        mScrollOrientation = scrollOrientation;
        mScrollOffset = (scrollOrientation == ScrollOrientation.RIGHT_TO_LEFT || scrollOrientation == ScrollOrientation.BOTTOM_TO_TOP) ? 0 : Integer.MAX_VALUE;

        try {
            mAnimation = animation.getDeclaredConstructor(ScrollOrientation.class, int.class).newInstance(scrollOrientation, visibleCount);
            mLayout = layout.getDeclaredConstructor(ScrollOrientation.class, int.class, int.class).newInstance(scrollOrientation, visibleCount, 30);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setPagerMode(boolean isPagerMode) {
        mPagerMode = isPagerMode;
    }

    public boolean getPagerMode() {
        return mPagerMode;
    }

    public void setPagerFlingVelocity(@IntRange(from = 0) int velocity) {
        mPagerFlingVelocity = Math.min(Integer.MAX_VALUE, Math.max(0, velocity));
    }

    public int getPagerFlingVelocity() {
        return mPagerFlingVelocity;
    }

    public void setVisibleItemCount(@IntRange(from = 1) int count) {
        mVisibleItemCount = Math.min(getItemCount() - 1, Math.max(1, count));
        mAnimation.setVisibleCount(mVisibleItemCount);
    }

    public int getVisibleItemCount() {
        return mVisibleItemCount;
    }

    public void setItemOffset(int offset) {
        mLayout.setItemOffset(offset);
    }

    public int getItemOffset() {
        return mLayout == null ? 0 : mLayout.getItemOffset();
    }

    public void setAnimation(StackAnimation animation) {
        mAnimation = animation;
    }

    public StackAnimation getAnimation() {
        return mAnimation;
    }

    public ScrollOrientation getScrollOrientation() {
        return mScrollOrientation;
    }

    public int getFirstVisibleItemPosition() {
        if (getWidth() == 0 || getHeight() == 0) {
            return 0;
        }

        int firstVisiblePosition;
        switch (mScrollOrientation) {
            case RIGHT_TO_LEFT:
                firstVisiblePosition = (int) Math.floor(mScrollOffset * 1.0 / getWidth());
                break;
            case LEFT_TO_RIGHT:
                firstVisiblePosition = getItemCount() - 1 - (int) Math.ceil(mScrollOffset * 1.0 / getWidth());
                break;
            case BOTTOM_TO_TOP:
                firstVisiblePosition = (int) Math.floor(mScrollOffset * 1.0 / getHeight());
                break;
            case TOP_TO_BOTTOM:
                firstVisiblePosition = getItemCount() - 1 - (int) Math.ceil(mScrollOffset * 1.0 / getHeight());
                break;
            default:
                firstVisiblePosition = 0;
        }

        return firstVisiblePosition;
    }

    public void setItemChangedListener(ItemChangedListener listener) {
        itemChangedListener = listener;
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onAttachedToWindow(RecyclerView view) {
        super.onAttachedToWindow(view);

        mOnFlingListener = new RecyclerView.OnFlingListener() {
            @Override
            public boolean onFling(int velocityX, int velocityY) {
                if (mPagerMode) {
                    switch (mScrollOrientation) {
                        case RIGHT_TO_LEFT:
                        case LEFT_TO_RIGHT:
                            mFlingOrientation = (velocityX > mPagerFlingVelocity) ? FlingOrientation.RIGHT_TO_LEFT : (velocityX < -mPagerFlingVelocity) ? FlingOrientation.LEFT_TO_RIGHT : FlingOrientation.NONE;
                            break;
                        case BOTTOM_TO_TOP:
                        case TOP_TO_BOTTOM:
                            mFlingOrientation = (velocityY > mPagerFlingVelocity) ? FlingOrientation.BOTTOM_TO_TOP : (velocityY < -mPagerFlingVelocity) ? FlingOrientation.TOP_TO_BOTTOM : FlingOrientation.NONE;
                            break;
                        default:
                            mFlingOrientation = FlingOrientation.NONE;
                            break;
                    }

                    if (mScrollOffset >= 1 && mScrollOffset < getWidth() * (getItemCount() - 1)) {
                        mFixScrolling = true;
                    }
                    calculateAndScrollToTarget(view);
                }
                return mPagerMode;
            }
        };
        view.setOnFlingListener(mOnFlingListener);

        mOnScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (!mFixScrolling) {
                        mFixScrolling = true;
                        calculateAndScrollToTarget(view);
                    } else {
                        mFixScrolling = false;
                    }
                } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    mFixScrolling = false;
                }
            }
        };
        view.addOnScrollListener(mOnScrollListener);
    }

    @Override
    public void onDetachedFromWindow(RecyclerView view, RecyclerView.Recycler recycler) {
        super.onDetachedFromWindow(view, recycler);
        if (view.getOnFlingListener() == mOnFlingListener) {
            view.setOnFlingListener(null);
        }
        view.removeOnScrollListener(mOnScrollListener);
    }

    @Override
    public boolean canScrollHorizontally() {
        return getItemCount() > 0 && (mScrollOrientation == ScrollOrientation.LEFT_TO_RIGHT || mScrollOrientation == ScrollOrientation.RIGHT_TO_LEFT);
    }

    @Override
    public boolean canScrollVertically() {
        return getItemCount() > 0 && (mScrollOrientation == ScrollOrientation.TOP_TO_BOTTOM || mScrollOrientation == ScrollOrientation.BOTTOM_TO_TOP);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        mLayout.requestLayout();
        removeAndRecycleAllViews(recycler);

        if (getItemCount() > 0) {
            mScrollOffset = getValidOffset(mScrollOffset);
            loadItemView(recycler);
        }
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        return handleScrollBy(dx, recycler);
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        return handleScrollBy(dy, recycler);
    }

    @Override
    public void scrollToPosition(int position) {
        if (position < 0 || position >= getItemCount()) {
            throw new ArrayIndexOutOfBoundsException(position + " is out of bound [0.." + (getItemCount() - 1) + "]");
        }
        mScrollOffset = getPositionOffset(position);
        requestLayout();
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        if (position < 0 || position >= getItemCount()) {
            throw new ArrayIndexOutOfBoundsException(position + " is out of bound [0.." + (getItemCount() - 1) + "]");
        }
        mFixScrolling = true;
        scrollToCenter(position, recyclerView, true);
    }

    private void updatePositionRecordAndNotify(int position) {
        if (itemChangedListener == null) {
            return;
        }
        if (position != itemPosition) {
            isItemPositionChanged = true;
            itemPosition = position;
            itemChangedListener.onItemChanged(itemPosition);
        } else {
            isItemPositionChanged = false;
        }
    }

    private int handleScrollBy(int offset, RecyclerView.Recycler recycler) {
        int expectOffset = mScrollOffset + offset;
        mScrollOffset = getValidOffset(expectOffset);

        int exactMove = mScrollOffset - expectOffset + offset;

        if (exactMove == 0) {
            return 0;
        }

        detachAndScrapAttachedViews(recycler);
        loadItemView(recycler);
        return exactMove;
    }

    private void loadItemView(RecyclerView.Recycler recycler) {
        int firstVisiblePosition = getFirstVisibleItemPosition();
        int lastVisiblePosition = getLastVisibleItemPosition();
        float movePercent = getFirstVisibleItemMovePercent();

        List<View> scrapViews = new ArrayList<>();
        for (int i = lastVisiblePosition; i >= firstVisiblePosition; i--) {
            View view = recycler.getViewForPosition(i);
            scrapViews.add(view);
        }

        for (int i = 0; i < scrapViews.size(); i++) {
            View view = scrapViews.get(i);
            addView(view);
            measureChildWithMargins(view, 0, 0);
            mLayout.doLayout(this, mScrollOffset, movePercent, view, i);
            mAnimation.doAnimation(movePercent, view, i);
        }

        updatePositionRecordAndNotify(firstVisiblePosition);

        recycleViews(recycler, scrapViews);
    }

    private void recycleViews(RecyclerView.Recycler recycler, List<View> scrapViews) {
        int firstVisiblePosition = getFirstVisibleItemPosition();
        int lastVisiblePosition = getLastVisibleItemPosition();

        for (int i = 0; i < scrapViews.size(); i++) {
            View view = scrapViews.get(i);
            int viewPosition = firstVisiblePosition + i;

            if (viewPosition < firstVisiblePosition || viewPosition > lastVisiblePosition) {
                recycler.recycleView(view);
            }
        }
    }

    private void calculateAndScrollToTarget(RecyclerView view) {
        int targetPosition = calculateCenterPosition(getFirstVisibleItemPosition());
        scrollToCenter(targetPosition, view, true);
    }

    private void scrollToCenter(int targetPosition, RecyclerView recyclerView, boolean animation) {
        int targetOffset = getPositionOffset(targetPosition);
        switch (mScrollOrientation) {
            case LEFT_TO_RIGHT:
            case RIGHT_TO_LEFT:
                if (animation) {
                    recyclerView.smoothScrollBy(targetOffset - mScrollOffset, 0);
                } else {
                    recyclerView.scrollBy(targetOffset - mScrollOffset, 0);
                }
                break;
            case TOP_TO_BOTTOM:
            case BOTTOM_TO_TOP:
                if (animation) {
                    recyclerView.smoothScrollBy(0, targetOffset - mScrollOffset);
                } else {
                    recyclerView.scrollBy(0, targetOffset - mScrollOffset);
                }
                break;
        }
    }

    private int getValidOffset(int expectOffset) {
        switch (mScrollOrientation) {
            case LEFT_TO_RIGHT:
            case RIGHT_TO_LEFT:
                return Math.max(Math.min(getWidth() * (getItemCount() - 1), expectOffset), 0);
            case TOP_TO_BOTTOM:
            case BOTTOM_TO_TOP:
                return Math.max(Math.min(getHeight() * (getItemCount() - 1), expectOffset), 0);
            default:
                return expectOffset;
        }
    }

    private int getPositionOffset(int position) {
        switch (mScrollOrientation) {
            case RIGHT_TO_LEFT:
                return position * getWidth();
            case LEFT_TO_RIGHT:
                return (getItemCount() - 1 - position) * getWidth();
            case BOTTOM_TO_TOP:
                return position * getHeight();
            case TOP_TO_BOTTOM:
                return (getItemCount() - 1 - position) * getHeight();
            default:
                return 0;
        }
    }

    private int getLastVisibleItemPosition() {
        int firstVisiblePosition = getFirstVisibleItemPosition();
        int lastVisiblePosition = firstVisiblePosition + mVisibleItemCount - 1;
        return Math.min(lastVisiblePosition, getItemCount() - 1);
    }

    private float getFirstVisibleItemMovePercent() {
        if (getWidth() == 0 || getHeight() == 0) {
            return 0f;
        }

        switch (mScrollOrientation) {
            case RIGHT_TO_LEFT:
                return (mScrollOffset % getWidth()) * 1.0f / getWidth();
            case LEFT_TO_RIGHT:
                float targetPercent = 1 - (mScrollOffset % getWidth()) * 1.0f / getWidth();
                return targetPercent == 1f ? 0f : targetPercent;
            case BOTTOM_TO_TOP:
                return (mScrollOffset % getHeight()) * 1.0f / getHeight();
            case TOP_TO_BOTTOM:
                targetPercent = 1 - (mScrollOffset % getHeight()) * 1.0f / getHeight();
                return targetPercent == 1f ? 0f : targetPercent;
            default:
                return 0f;
        }
    }

    private int calculateCenterPosition(int position) {
        FlingOrientation triggerOrientation = mFlingOrientation;
        mFlingOrientation = FlingOrientation.NONE;

        switch (mScrollOrientation) {
            case RIGHT_TO_LEFT:
                if (triggerOrientation == FlingOrientation.RIGHT_TO_LEFT) {
                    return position + 1;
                } else if (triggerOrientation == FlingOrientation.LEFT_TO_RIGHT) {
                    return position;
                }
                break;
            case LEFT_TO_RIGHT:
                if (triggerOrientation == FlingOrientation.LEFT_TO_RIGHT) {
                    return position + 1;
                } else if (triggerOrientation == FlingOrientation.RIGHT_TO_LEFT) {
                    return position;
                }
                break;
            case BOTTOM_TO_TOP:
                if (triggerOrientation == FlingOrientation.BOTTOM_TO_TOP) {
                    return position + 1;
                } else if (triggerOrientation == FlingOrientation.TOP_TO_BOTTOM) {
                    return position;
                }
                break;
            case TOP_TO_BOTTOM:
                if (triggerOrientation == FlingOrientation.TOP_TO_BOTTOM) {
                    return position + 1;
                } else if (triggerOrientation == FlingOrientation.BOTTOM_TO_TOP) {
                    return position;
                }
                break;
        }

        float movePercent = getFirstVisibleItemMovePercent();
        if (movePercent > 0.5) {
            position++;
        }

        return position;
    }
}
