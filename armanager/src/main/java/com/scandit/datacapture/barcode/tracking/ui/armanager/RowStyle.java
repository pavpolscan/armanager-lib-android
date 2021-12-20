package com.scandit.datacapture.barcode.tracking.ui.armanager;

public class RowStyle {
    private int color;
    private int backgroundColor;
    private float alpha;

    public RowStyle(int color, int backgroundColor, float alpha){
        this.setColor(color);
        this.setBackgroundColor(backgroundColor);
        this.setAlpha(alpha);
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }
}
