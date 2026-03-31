package com.mercury.platform.ui.frame;

import org.junit.jupiter.api.Test;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AbstractComponentFrameTest {

    @Test
    void computeUsableBounds_subtractsScreenInsets() {
        Rectangle screen = new Rectangle(0, 0, 1920, 1080);
        Insets insets = new Insets(0, 0, 40, 0);

        Rectangle usable = AbstractComponentFrame.computeUsableBounds(screen, insets);

        assertEquals(new Rectangle(0, 0, 1920, 1040), usable);
    }

    @Test
    void clampFrameLocation_keepsFrameInsideHorizontalBounds() {
        Rectangle usable = new Rectangle(0, 0, 1920, 1080);
        Dimension frameSize = new Dimension(1000, 600);
        Point desired = new Point(1500, 100);

        Point clamped = AbstractComponentFrame.clampFrameLocation(desired, frameSize, usable, 24);

        assertEquals(new Point(920, 100), clamped);
    }

    @Test
    void clampFrameLocation_keepsHeaderReachableWhenWindowMovedOffBottom() {
        Rectangle usable = new Rectangle(0, 0, 1920, 1080);
        Dimension frameSize = new Dimension(1100, 900);
        Point desired = new Point(300, 1200);

        Point clamped = AbstractComponentFrame.clampFrameLocation(desired, frameSize, usable, 26);

        assertEquals(new Point(300, 1054), clamped);
    }

    @Test
    void clampFrameLocation_handlesFrameWiderThanUsableArea() {
        Rectangle usable = new Rectangle(0, 0, 1280, 720);
        Dimension frameSize = new Dimension(1600, 500);
        Point desired = new Point(200, 40);

        Point clamped = AbstractComponentFrame.clampFrameLocation(desired, frameSize, usable, 24);

        assertEquals(new Point(0, 40), clamped);
    }
}
