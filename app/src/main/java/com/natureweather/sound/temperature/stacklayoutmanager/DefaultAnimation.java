package com.natureweather.sound.temperature.stacklayoutmanager;

import android.view.View;

public class DefaultAnimation extends StackAnimation {

    private float mScale = 0.95f;
    private float mOutScale = 0.8f;
    private int mOutRotation;

    public DefaultAnimation(StackLayoutManager.ScrollOrientation scrollOrientation, int visibleCount) {
        super(scrollOrientation, visibleCount);

        mOutRotation = (scrollOrientation == StackLayoutManager.ScrollOrientation.LEFT_TO_RIGHT ||
                scrollOrientation == StackLayoutManager.ScrollOrientation.RIGHT_TO_LEFT) ? 10 : 0;
    }

    /**
     * Set the item scale rate.
     *
     * @param scale Scale rate, default is 0.95f.
     */
    public void setItemScaleRate(float scale) {
        mScale = scale;
    }

    /**
     * Get the item scale rate.
     *
     * @return The item scale rate, default is 0.95f.
     */
    public float getItemScaleRate() {
        return mScale;
    }

    /**
     * Set the out scale rate for the item when leaving the screen.
     *
     * @param scale Out scale rate, default is 0.8f.
     */
    public void setOutScale(float scale) {
        mOutScale = scale;
    }

    /**
     * Get the out scale rate for the item when leaving the screen.
     *
     * @return The out scale rate, default is 0.8f.
     */
    public float getOutScale() {
        return mOutScale;
    }

    /**
     * Set the out rotation angle for the item when leaving the screen.
     *
     * @param rotation Out rotation angle, default is 30.
     */
    public void setOutRotation(int rotation) {
        mOutRotation = rotation;
    }

    /**
     * Get the out rotation angle for the item when leaving the screen.
     *
     * @return The out rotation angle, default is 30.
     */
    public int getOutRotation() {
        return mOutRotation;
    }

    @Override
    public void doAnimation(float firstMovePercent, View itemView, int position) {
        float scale;
        float alpha = 1.0f;
        float rotation;
        if (position == 0) {
            scale = 1 - ((1 - mOutScale) * firstMovePercent);
            rotation = mOutRotation * firstMovePercent;
        } else {
            float minScale = (float) Math.pow(mScale, position);
            float maxScale = (float) Math.pow(mScale, position - 1);
            scale = minScale + (maxScale - minScale) * firstMovePercent;
            // Only the last item should change alpha
            if (position == mVisibleCount) {
                alpha = firstMovePercent;
            }
            rotation = 0f;
        }

        setItemPivotXY(mScrollOrientation, itemView);
        rotationFirstVisibleItem(mScrollOrientation, itemView, rotation);
        itemView.setScaleX(scale);
        itemView.setScaleY(scale);
        itemView.setAlpha(alpha);
    }

    private void setItemPivotXY(StackLayoutManager.ScrollOrientation scrollOrientation, View view) {
        switch (scrollOrientation) {
            case RIGHT_TO_LEFT:
                view.setPivotX(view.getMeasuredWidth());
                view.setPivotY(view.getMeasuredHeight() / 2f);
                break;
            case LEFT_TO_RIGHT:
                view.setPivotX(0f);
                view.setPivotY(view.getMeasuredHeight() / 2f);
                break;
            case BOTTOM_TO_TOP:
                view.setPivotX(view.getMeasuredWidth() / 2f);
                view.setPivotY(view.getMeasuredHeight());
                break;
            case TOP_TO_BOTTOM:
                view.setPivotX(view.getMeasuredWidth() / 2f);
                view.setPivotY(0f);
                break;
        }
    }

    private void rotationFirstVisibleItem(StackLayoutManager.ScrollOrientation scrollOrientation, View view, float rotation) {
        switch (scrollOrientation) {
            case RIGHT_TO_LEFT:
                view.setRotationY(rotation);
                break;
            case LEFT_TO_RIGHT:
                view.setRotationY(-rotation);
                break;
            case BOTTOM_TO_TOP:
                view.setRotationX(-rotation);
                break;
            case TOP_TO_BOTTOM:
                view.setRotationX(rotation);
                break;
        }
    }
}
