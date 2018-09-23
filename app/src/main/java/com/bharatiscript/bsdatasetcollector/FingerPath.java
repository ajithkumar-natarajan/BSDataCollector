package com.bharatiscript.bsdatasetcollector;

import android.graphics.Path;

public class FingerPath {

    public int color;
    public Path path;
    public int strokeWidth;

    public FingerPath(int color, int strokeWidth, Path path) {
        this.color = color;
        this.strokeWidth = strokeWidth;
        this.path = path;


    }
}