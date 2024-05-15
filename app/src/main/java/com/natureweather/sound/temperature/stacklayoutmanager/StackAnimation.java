package com.natureweather.sound.temperature.stacklayoutmanager;

import android.view.View;

public abstract class StackAnimation {

    protected StackLayoutManager.ScrollOrientation mScrollOrientation;
    protected int mVisibleCount;

    public StackAnimation(StackLayoutManager.ScrollOrientation scrollOrientation, int visibleCount) {
        mScrollOrientation = scrollOrientation;
        mVisibleCount = visibleCount;
    }

    void setVisibleCount(int visibleCount) {
        mVisibleCount = visibleCount;
    }

    /**
     * External callback to perform animations.
     *
     * @param firstMovePercent The percentage of the first visible item moved. When it's about to move out of the screen, firstMovePercent approaches 1.
     * @param itemView        The current itemView.
     * @param position        The position of the current itemView, where position = 0 until visibleCount.
     */
    public abstract void doAnimation(float firstMovePercent, View itemView, int position);
}
