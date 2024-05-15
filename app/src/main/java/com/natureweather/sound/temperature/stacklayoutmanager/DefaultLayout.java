package com.natureweather.sound.temperature.stacklayoutmanager;

import android.view.View;

public class DefaultLayout extends StackLayout {

    private boolean mHasMeasureItemSize = false;
    private int mWidthSpace = 0;
    private int mHeightSpace = 0;
    private int mStartMargin = 0;

    private int mWidth = 0;
    private int mHeight = 0;
    private int mScrollOffset = 0;

    public DefaultLayout(StackLayoutManager.ScrollOrientation scrollOrientation, int visibleCount, int perItemOffset) {
        super(scrollOrientation, visibleCount, perItemOffset);
    }

    @Override
    public void doLayout(StackLayoutManager stackLayoutManager, int scrollOffset, float firstMovePercent, View itemView, int position) {
        mWidth = stackLayoutManager.getWidth();
        mHeight = stackLayoutManager.getHeight();
        mScrollOffset = scrollOffset;
        if (!mHasMeasureItemSize) {
            mWidthSpace = mWidth - stackLayoutManager.getDecoratedMeasuredWidth(itemView);
            mHeightSpace = mHeight - stackLayoutManager.getDecoratedMeasuredHeight(itemView);
            mStartMargin = getStartMargin();
            mHasMeasureItemSize = true;
        }
        int left;
        int top;
        if (position == 0) {
            left = getFirstVisibleItemLeft();
            top = getFirstVisibleItemTop();
        } else {
            left = getAfterFirstVisibleItemLeft(position, firstMovePercent);
            top = getAfterFirstVisibleItemTop(position, firstMovePercent);
        }

        int right = left + stackLayoutManager.getDecoratedMeasuredWidth(itemView);
        int bottom = top + stackLayoutManager.getDecoratedMeasuredHeight(itemView);

        stackLayoutManager.layoutDecorated(itemView, left, top, right, bottom);
    }

    @Override
    public void requestLayout() {
        mHasMeasureItemSize = false; // Indicates that the size may have changed
    }

    private int getFirstVisibleItemLeft() {
        switch (mScrollOrientation) {
            case RIGHT_TO_LEFT:
                return mStartMargin - mScrollOffset % mWidth;
            case LEFT_TO_RIGHT:
                return (mScrollOffset % mWidth == 0) ? mStartMargin : mStartMargin + (mWidth - mScrollOffset % mWidth);
            default:
                return mWidthSpace / 2;
        }
    }

    private int getFirstVisibleItemTop() {
        switch (mScrollOrientation) {
            case BOTTOM_TO_TOP:
                return mStartMargin - mScrollOffset % mHeight;
            case TOP_TO_BOTTOM:
                return (mScrollOffset % mHeight == 0) ? mStartMargin : mStartMargin + (mHeight - mScrollOffset % mHeight);
            default:
                return mHeightSpace / 2;
        }
    }

    private int getAfterFirstVisibleItemLeft(int visiblePosition, float movePercent) {
        switch (mScrollOrientation) {
            case RIGHT_TO_LEFT:
                return (int) (mStartMargin + mPerItemOffset * (visiblePosition - movePercent));
            case LEFT_TO_RIGHT:
                return (int) (mStartMargin - mPerItemOffset * (visiblePosition - movePercent));
            default:
                return mWidthSpace / 2;
        }
    }

    private int getAfterFirstVisibleItemTop(int visiblePosition, float movePercent) {
        switch (mScrollOrientation) {
            case BOTTOM_TO_TOP:
                return (int) (mStartMargin + mPerItemOffset * (visiblePosition - movePercent));
            case TOP_TO_BOTTOM:
                return (int) (mStartMargin - mPerItemOffset * (visiblePosition - movePercent));
            default:
                return mHeightSpace / 2;
        }
    }

    private int getStartMargin() {
        switch (mScrollOrientation) {
            case RIGHT_TO_LEFT:
            case LEFT_TO_RIGHT:
                return mWidthSpace / 2;
            default:
                return mHeightSpace / 2;
        }
    }
}
