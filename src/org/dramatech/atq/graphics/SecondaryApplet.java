package org.dramatech.atq.graphics;

import processing.core.PApplet;

public class SecondaryApplet extends PApplet {
    int w, h;

    public SecondaryApplet() {
        super();
    }

    public SecondaryApplet(final int w, final int h) {
        super();
        this.w = w;
        this.h = h;
    }


    public void setup() {
        size(w, h);
        smooth();
    }

    public void draw() {
    }
}
