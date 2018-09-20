package com.bharatiscript.bsdatasetcollector;

import android.graphics.Path;

public class FingerPath {

    public int color;
    public Path path;

    public FingerPath(int color, boolean emboss, boolean blur, int strokeWidth, Path path) {
        this.color = color;
        this.path = path;
    }
}