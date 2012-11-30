package org.dramatech.atq.graphics;

import java.awt.Frame;

public class PFrame extends Frame {
    public SecondaryApplet s;
    public int w, h;

    public PFrame() {
        w = 800;
        h = 200;
        setBounds(0, 0, w, h);

        s = new SecondaryApplet(w, h);
        add(s);
        s.init();

        setVisible(true);
    }

    public PFrame(final String title) {
        this();
        this.setTitle(title);
    }
}
