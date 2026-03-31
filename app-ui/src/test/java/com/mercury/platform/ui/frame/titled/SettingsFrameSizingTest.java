package com.mercury.platform.ui.frame.titled;

import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SettingsFrameSizingTest {

    @Test
    void shouldClampRequestedSizeWhenItExceedsUsableBounds() {
        Dimension clamped = SettingsFrame.clampToUsableBounds(
                new Dimension(2400, 1400),
                new Rectangle(0, 0, 1920, 1080)
        );

        assertEquals(new Dimension(1920, 1080), clamped);
    }

    @Test
    void shouldKeepRequestedSizeWhenItFitsUsableBounds() {
        Dimension clamped = SettingsFrame.clampToUsableBounds(
                new Dimension(1000, 600),
                new Rectangle(0, 0, 1920, 1080)
        );

        assertEquals(new Dimension(1000, 600), clamped);
    }

    @Test
    void shouldReturnSafeFallbackWhenRequestedSizeIsNull() {
        Dimension clamped = SettingsFrame.clampToUsableBounds(null, new Rectangle(0, 0, 1920, 1080));

        assertEquals(new Dimension(1, 1), clamped);
    }
}
