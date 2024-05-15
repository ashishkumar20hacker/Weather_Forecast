package com.natureweather.sound.temperature.stacklayoutmanager;

import android.view.View;

public abstract class StackLayout {

    protected StackLayoutManager.ScrollOrientation mScrollOrientation;
    protected int mVisibleCount;
    protected int mPerItemOffset;

    public StackLayout(StackLayoutManager.ScrollOrientation scrollOrientation, int visibleCount, int perItemOffset) {
        mScrollOrientation = scrollOrientation;
        mVisibleCount = visibleCount;
        mPerItemOffset = perItemOffset;
    }

    void setItemOffset(int offset) {
        mPerItemOffset = offset;
    }

    int getItemOffset() {
        return mPerItemOffset;
    }

    /**
     * External callback to perform layout.
     *
     * @param stackLayoutManager The StackLayoutManager instance.
     * @param scrollOffset       The current scroll offset.
     * @param firstMovePercent   The percentage of the first visible item moved. When it's about to move out of the screen, firstMovePercent approaches 1.
     * @param itemView           The current itemView.
     * @param position           The position of the current itemView, where position = 0 until visibleCount.
     */
    public abstract void doLayout(StackLayoutManager stackLayoutManager, int scrollOffset, float firstMovePercent, View itemView, int position);

    public abstract void requestLayout();
}
