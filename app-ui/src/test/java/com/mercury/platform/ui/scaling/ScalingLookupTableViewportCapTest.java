package com.mercury.platform.ui.scaling;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ScalingLookupTableViewportCapTest {

    @Test
    void shouldConstrainOtherScaleByViewportHeightOnCompactDisplay() {
        ScalingLookupTable.DisplayConfig display =
                new ScalingLookupTable.DisplayConfig(1366, 768, 96, 1f, "Compact 1080p-class");

        float cap = ScalingLookupTable.calculateViewportAwareOtherScaleCap(display);

        assertEquals(1.0496f, cap, 0.01f);
    }

    @Test
    void shouldApplyEffectiveViewportWithOsScaling() {
        ScalingLookupTable.DisplayConfig display =
                new ScalingLookupTable.DisplayConfig(3840, 2160, 163, 2f, "4K with 200% scaling");

        float cap = ScalingLookupTable.calculateViewportAwareOtherScaleCap(display);

        assertEquals(1.476f, cap, 0.01f);
    }

    @Test
    void shouldClampToMinimumScaleOnVerySmallViewport() {
        ScalingLookupTable.DisplayConfig display =
                new ScalingLookupTable.DisplayConfig(320, 240, 96, 2f, "Tiny viewport");

        float cap = ScalingLookupTable.calculateViewportAwareOtherScaleCap(display);

        assertEquals(0.5f, cap, 0.0001f);
    }

    @Test
    void shouldClampToMaximumScaleOnVeryLargeViewport() {
        ScalingLookupTable.DisplayConfig display =
                new ScalingLookupTable.DisplayConfig(10000, 8000, 96, 1f, "Very large viewport");

        float cap = ScalingLookupTable.calculateViewportAwareOtherScaleCap(display);

        assertEquals(5f, cap, 0.0001f);
    }
}
