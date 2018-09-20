package com.bharatiscript.bsdatasetcollector;

import android.graphics.Path;

public class FingerPath {

    public int color; //CHECK IF REQUIRED
    public Path path;

    public FingerPath(int color, Path path) {
        this.color = color;
        this.path = path;
    }
}