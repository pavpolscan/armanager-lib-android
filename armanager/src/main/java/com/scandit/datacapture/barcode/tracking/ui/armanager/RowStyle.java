package com.scandit.datacapture.barcode.tracking.ui.armanager;

import androidx.annotation.ColorInt;

public class RowStyle {
    private int color;
    private int backgroundColor;
    private float alpha;

    public RowStyle(@ColorInt final int color, @ColorInt final int backgroundColor, final float alpha){
        this.setColor(color);
        this.setBackgroundColor(backgroundColor);
        this.setAlpha(alpha);
    }

    public int getColor() {
        return color;
    }

    public void setColor(@ColorInt final int color) {
        this.color = color;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(@ColorInt final int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(final float alpha) {
        this.alpha = alpha;
    }
}
