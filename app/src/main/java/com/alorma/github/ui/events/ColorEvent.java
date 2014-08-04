package com.alorma.github.ui.events;

/**
 * Created by Bernat on 04/08/2014.
 */
public class ColorEvent {
    private int rgb;

    public ColorEvent(int rgb) {

        this.rgb = rgb;
    }

    public int getRgb() {
        return rgb;
    }

    public void setRgb(int rgb) {
        this.rgb = rgb;
    }
}
