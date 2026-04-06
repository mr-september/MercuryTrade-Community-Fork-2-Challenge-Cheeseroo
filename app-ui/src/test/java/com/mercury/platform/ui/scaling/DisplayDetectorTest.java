package com.mercury.platform.ui.scaling;

import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DisplayDetectorTest {

    @Test
    void shouldParseLegacyJavaVersionFormat() {
        assertEquals(8, DisplayDetector.parseJavaMajorVersion("1.8.0_482"));
    }

    @Test
    void shouldParseModernJavaVersionFormat() {
        assertEquals(17, DisplayDetector.parseJavaMajorVersion("17.0.11"));
    }

    @Test
    void shouldRejectBlankJavaVersion() {
        assertThrows(IllegalArgumentException.class, () -> DisplayDetector.parseJavaMajorVersion("   "));
    }

    @Test
    void shouldClampNonOverlappingRectanglesToZeroArea() {
        Rectangle first = new Rectangle(0, 0, 100, 100);
        Rectangle second = new Rectangle(200, 200, 50, 50);

        assertEquals(0, DisplayDetector.calculateOverlapArea(first, second));
    }

    @Test
    void shouldCalculateOverlapAreaForIntersectingRectangles() {
        Rectangle first = new Rectangle(0, 0, 100, 100);
        Rectangle second = new Rectangle(50, 40, 100, 100);

        assertEquals(3000, DisplayDetector.calculateOverlapArea(first, second));
    }
}
