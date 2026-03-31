package com.mercury.platform.ui.scaling;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ScalingLookupTableTest {

    @Test
    void displayConfig_getDiagonalInches_calculatesCorrectly() {
        // 1920x1080 at 96 DPI: sqrt((1920/96)^2 + (1080/96)^2) = sqrt(400 + 126.5625) = sqrt(526.5625) ~22.95"
        ScalingLookupTable.DisplayConfig config = new ScalingLookupTable.DisplayConfig(
                1920, 1080, 96, 1.0f, "Test display"
        );
        double diagonal = config.getDiagonalInches();
        assertEquals(22.95, diagonal, 0.1);
    }

    @Test
    void displayConfig_getDiagonalInches_handles4kDisplay() {
        // 3840x2160 at 163 DPI: ~27.0" diagonal
        ScalingLookupTable.DisplayConfig config = new ScalingLookupTable.DisplayConfig(
                3840, 2160, 163, 1.0f, "4K display"
        );
        double diagonal = config.getDiagonalInches();
        assertEquals(27.0, diagonal, 0.5);
    }

    @Test
    void calculateRecommendation_returnsExactMatchForKnownConfig() {
        ScalingLookupTable.DisplayConfig config = new ScalingLookupTable.DisplayConfig(
                1920, 1080, 96, 1.0f, "Standard 1080p"
        );

        ScalingLookupTable.ScalingRecommendation result = ScalingLookupTable.calculateRecommendation(config);

        assertNotNull(result);
        assertEquals(1.0f, result.baseScale, 0.01f);
        assertEquals(1.0f, result.notificationScale, 0.01f);
        assertEquals(1.0f, result.taskbarScale, 0.01f);
        assertEquals(1.0f, result.itemCellScale, 0.01f);
        assertEquals(1.0f, result.otherScale, 0.01f);
    }

    @Test
    void calculateRecommendation_returns4kScalingForKnownConfig() {
        ScalingLookupTable.DisplayConfig config = new ScalingLookupTable.DisplayConfig(
                3840, 2160, 163, 1.0f, "4K 27\""
        );

        ScalingLookupTable.ScalingRecommendation result = ScalingLookupTable.calculateRecommendation(config);

        assertNotNull(result);
        assertEquals(1.5f, result.baseScale, 0.01f);
        assertEquals(1.3f, result.notificationScale, 0.01f);
    }

    @Test
    void calculateRecommendation_throwsForNullDisplay() {
        assertThrows(IllegalArgumentException.class,
                () -> ScalingLookupTable.calculateRecommendation(null));
    }

    @Test
    void calculateRecommendation_throwsForInvalidDimensions() {
        ScalingLookupTable.DisplayConfig zeroWidth = new ScalingLookupTable.DisplayConfig(
                0, 1080, 96, 1.0f, "Invalid"
        );
        assertThrows(IllegalArgumentException.class,
                () -> ScalingLookupTable.calculateRecommendation(zeroWidth));

        ScalingLookupTable.DisplayConfig negativeHeight = new ScalingLookupTable.DisplayConfig(
                1920, -1, 96, 1.0f, "Invalid"
        );
        assertThrows(IllegalArgumentException.class,
                () -> ScalingLookupTable.calculateRecommendation(negativeHeight));
    }

    @Test
    void calculateRecommendation_throwsForInvalidDpi() {
        ScalingLookupTable.DisplayConfig config = new ScalingLookupTable.DisplayConfig(
                1920, 1080, 0, 1.0f, "Invalid DPI"
        );
        assertThrows(IllegalArgumentException.class,
                () -> ScalingLookupTable.calculateRecommendation(config));
    }

    @Test
    void calculateRecommendation_producesDynamicResultForUnknownConfig() {
        // Unknown resolution that won't match any preset
        ScalingLookupTable.DisplayConfig config = new ScalingLookupTable.DisplayConfig(
                1024, 768, 72, 1.0f, "Custom display"
        );

        ScalingLookupTable.ScalingRecommendation result = ScalingLookupTable.calculateRecommendation(config);

        assertNotNull(result);
        assertTrue(result.baseScale > 0);
        assertTrue(result.notificationScale > 0);
        assertTrue(result.taskbarScale > 0);
        assertTrue(result.itemCellScale > 0);
        assertTrue(result.otherScale > 0);
        assertNotNull(result.reasoning);
    }

    @Test
    void calculateRecommendation_clampsScaleValues() {
        // Very high DPI should be clamped
        ScalingLookupTable.DisplayConfig config = new ScalingLookupTable.DisplayConfig(
                1920, 1080, 500, 5.0f, "Extreme scaling"
        );

        ScalingLookupTable.ScalingRecommendation result = ScalingLookupTable.calculateRecommendation(config);

        assertTrue(result.baseScale >= 0.5f && result.baseScale <= 5.0f);
        assertTrue(result.notificationScale >= 0.5f && result.notificationScale <= 5.0f);
        assertTrue(result.taskbarScale >= 0.5f && result.taskbarScale <= 5.0f);
        assertTrue(result.itemCellScale >= 0.5f && result.itemCellScale <= 5.0f);
        assertTrue(result.otherScale >= 0.5f && result.otherScale <= 5.0f);
    }

    @Test
    void calculateRecommendation_capsOtherScaleToViewportBudget() {
        ScalingLookupTable.DisplayConfig config = new ScalingLookupTable.DisplayConfig(
                2560, 1440, 220, 2.0f, "High DPI + high OS scale"
        );

        ScalingLookupTable.ScalingRecommendation result = ScalingLookupTable.calculateRecommendation(config);
        float expectedCap = ScalingLookupTable.calculateViewportAwareOtherScaleCap(config);

        assertEquals(expectedCap, result.otherScale, 0.01f);
        assertTrue(result.reasoning.contains("Other UI capped"));
    }

    @Test
    void calculateOsScaleAdjustment_temperedForHighScaleValues() {
        assertEquals(1.0f, ScalingLookupTable.calculateOsScaleAdjustment(1.0f), 0.001f);
        assertEquals(1.175f, ScalingLookupTable.calculateOsScaleAdjustment(1.5f), 0.001f);
        assertTrue(ScalingLookupTable.calculateOsScaleAdjustment(2.0f) < 2.0f);
        assertTrue(ScalingLookupTable.calculateOsScaleAdjustment(3.0f) <= 1.7f);
    }

    @Test
    void calculateRecommendation_appliesTemperedOsAdjustmentToDynamicScale() {
        ScalingLookupTable.DisplayConfig config = new ScalingLookupTable.DisplayConfig(
                2560, 1440, 144, 1.5f, "Tempered dynamic case"
        );

        ScalingLookupTable.ScalingRecommendation result = ScalingLookupTable.calculateRecommendation(config);

        // Legacy behavior could reach 225% for base scale in this scenario; keep dynamic output below that.
        assertTrue(result.baseScale < 2.25f);
    }

    @Test
    void scalingRecommendation_toScaleMap_containsAllKeys() {
        ScalingLookupTable.DisplayConfig config = new ScalingLookupTable.DisplayConfig(
                1920, 1080, 96, 1.0f, "Standard 1080p"
        );

        ScalingLookupTable.ScalingRecommendation recommendation = ScalingLookupTable.calculateRecommendation(config);
        Map<String, Float> scaleMap = recommendation.toScaleMap();

        assertNotNull(scaleMap);
        assertEquals(4, scaleMap.size());
        assertTrue(scaleMap.containsKey("notification"));
        assertTrue(scaleMap.containsKey("taskbar"));
        assertTrue(scaleMap.containsKey("itemcell"));
        assertTrue(scaleMap.containsKey("other"));
    }

    @Test
    void hasRecommendation_returnsTrueForKnownConfig() {
        assertTrue(ScalingLookupTable.hasRecommendation(1920, 1080, 96));
        assertTrue(ScalingLookupTable.hasRecommendation(3840, 2160, 163));
    }

    @Test
    void hasRecommendation_returnsFalseForUnknownConfig() {
        assertFalse(ScalingLookupTable.hasRecommendation(1024, 768, 72));
    }

    @Test
    void getRecommendation_returnsNullForUnknownKey() {
        assertNull(ScalingLookupTable.getRecommendation("nonexistent_config"));
    }

    @Test
    void getRecommendation_returnsNonNullForKnownKey() {
        assertNotNull(ScalingLookupTable.getRecommendation("1920x1080_96dpi"));
    }

    @Test
    void getAllRecommendations_returnsCopy() {
        Map<String, ScalingLookupTable.ScalingRecommendation> all = ScalingLookupTable.getAllRecommendations();
        assertNotNull(all);
        assertTrue(all.size() > 0);

        // Modifying returned map should not affect the internal state
        all.clear();
        Map<String, ScalingLookupTable.ScalingRecommendation> allAgain = ScalingLookupTable.getAllRecommendations();
        assertTrue(allAgain.size() > 0);
    }
}