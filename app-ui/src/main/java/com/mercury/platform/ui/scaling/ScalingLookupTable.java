package com.mercury.platform.ui.scaling;

import java.util.HashMap;
import java.util.Map;

/**
 * Lookup table for optimal UI scaling based on display characteristics.
 * This class provides recommendations for UI scaling based on:
 * - Monitor resolution
 * - Physical screen size (DPI)
 * - OS-level scaling settings
 * - Display type (standard vs high-DPI)
 */
public class ScalingLookupTable {
    
    /**
     * Display configuration for scaling recommendations
     */
    public static class DisplayConfig {
        public final int width;
        public final int height;
        public final int dpi;
        public final float osScale;
        public final String description;
        
        public DisplayConfig(int width, int height, int dpi, float osScale, String description) {
            this.width = width;
            this.height = height;
            this.dpi = dpi;
            this.osScale = osScale;
            this.description = description;
        }
        
        /**
         * Calculate diagonal screen size in inches
         */
        public double getDiagonalInches() {
            double widthInches = width / (double) dpi;
            double heightInches = height / (double) dpi;
            return Math.sqrt(widthInches * widthInches + heightInches * heightInches);
        }
        
        @Override
        public String toString() {
            return String.format("%dx%d @ %d DPI (%.1f\" diagonal, %.0f%% OS scale): %s",
                width, height, dpi, getDiagonalInches(), osScale * 100, description);
        }
    }
    
    /**
     * Scaling recommendation for different UI components
     */
    public static class ScalingRecommendation {
        public final float baseScale;
        public final float notificationScale;
        public final float taskbarScale;
        public final float itemCellScale;
        public final float otherScale;
        public final String reasoning;
        
        public ScalingRecommendation(float baseScale, float notificationScale, 
                                   float taskbarScale, float itemCellScale, 
                                   float otherScale, String reasoning) {
            this.baseScale = baseScale;
            this.notificationScale = notificationScale;
            this.taskbarScale = taskbarScale;
            this.itemCellScale = itemCellScale;
            this.otherScale = otherScale;
            this.reasoning = reasoning;
        }
        
        public Map<String, Float> toScaleMap() {
            Map<String, Float> scaleMap = new HashMap<>();
            scaleMap.put("notification", notificationScale);
            scaleMap.put("taskbar", taskbarScale);
            scaleMap.put("itemcell", itemCellScale);
            scaleMap.put("other", otherScale);
            return scaleMap;
        }
    }
    
    // Common display configurations and their recommended scaling
    private static final Map<String, ScalingRecommendation> DISPLAY_RECOMMENDATIONS = new HashMap<>();
    
    static {
        // Standard 1080p displays (96 DPI)
        DISPLAY_RECOMMENDATIONS.put("1920x1080_96dpi", new ScalingRecommendation(
            1.0f, 1.0f, 1.0f, 1.0f, 1.0f,
            "Standard 1080p display - baseline scaling"
        ));
        
        // 1440p displays (109 DPI on 27\")
        DISPLAY_RECOMMENDATIONS.put("2560x1440_109dpi", new ScalingRecommendation(
            1.1f, 1.0f, 1.1f, 1.1f, 1.1f,
            "1440p 27\" display - slightly larger for readability"
        ));
        
        // 4K displays on large monitors (163 DPI on 27\")
        DISPLAY_RECOMMENDATIONS.put("3840x2160_163dpi", new ScalingRecommendation(
            1.5f, 1.3f, 1.5f, 1.4f, 1.5f,
            "4K 27\" display - significant scaling needed"
        ));
        
        // 4K displays on 32\" monitors (138 DPI)
        DISPLAY_RECOMMENDATIONS.put("3840x2160_138dpi", new ScalingRecommendation(
            1.3f, 1.2f, 1.3f, 1.25f, 1.3f,
            "4K 32\" display - moderate scaling"
        ));
        
        // Ultrawide 1440p (109 DPI on 34\")
        DISPLAY_RECOMMENDATIONS.put("3440x1440_109dpi", new ScalingRecommendation(
            1.1f, 1.0f, 1.1f, 1.1f, 1.1f,
            "Ultrawide 1440p - similar to standard 1440p"
        ));
        
        // 8K displays (324 DPI on 27\")
        DISPLAY_RECOMMENDATIONS.put("7680x4320_324dpi", new ScalingRecommendation(
            2.0f, 1.8f, 2.0f, 1.9f, 2.0f,
            "8K display - double scaling required"
        ));
        
        // High-DPI laptop displays
        DISPLAY_RECOMMENDATIONS.put("2880x1800_220dpi", new ScalingRecommendation(
            1.8f, 1.6f, 1.8f, 1.7f, 1.8f,
            "High-DPI laptop (15\" MacBook Pro style)"
        ));
        
        DISPLAY_RECOMMENDATIONS.put("3200x1800_235dpi", new ScalingRecommendation(
            1.9f, 1.7f, 1.9f, 1.8f, 1.9f,
            "High-DPI laptop (13.3\" QHD+)"
        ));
        
        // Common laptop resolutions
        DISPLAY_RECOMMENDATIONS.put("1366x768_112dpi", new ScalingRecommendation(
            0.9f, 0.9f, 0.9f, 0.9f, 0.9f,
            "Standard laptop - smaller scaling for space"
        ));
        
        DISPLAY_RECOMMENDATIONS.put("1600x900_130dpi", new ScalingRecommendation(
            1.0f, 1.0f, 1.0f, 1.0f, 1.0f,
            "HD+ laptop - standard scaling"
        ));
        
        // Multi-monitor configurations
        DISPLAY_RECOMMENDATIONS.put("5760x1080_96dpi", new ScalingRecommendation(
            1.0f, 0.9f, 1.0f, 1.0f, 1.0f,
            "Triple 1080p monitors - standard with compact notifications"
        ));
        
        DISPLAY_RECOMMENDATIONS.put("7680x1440_109dpi", new ScalingRecommendation(
            1.1f, 1.0f, 1.1f, 1.1f, 1.1f,
            "Triple 1440p monitors - slightly larger"
        ));
    }
    
    /**
     * Calculate scaling recommendation for a display configuration
     */
    public static ScalingRecommendation calculateRecommendation(DisplayConfig display) {
        // Input validation
        if (display == null) {
            throw new IllegalArgumentException("Display configuration cannot be null");
        }
        if (display.width <= 0 || display.height <= 0) {
            throw new IllegalArgumentException("Display dimensions must be positive");
        }
        if (display.dpi <= 0) {
            throw new IllegalArgumentException("DPI must be positive");
        }
        
        // First, try to find exact match
        String exactKey = String.format("%dx%d_%ddpi", display.width, display.height, display.dpi);
        if (DISPLAY_RECOMMENDATIONS.containsKey(exactKey)) {
            return DISPLAY_RECOMMENDATIONS.get(exactKey);
        }
        
        // Try with OS scaling
        String osScaleKey = String.format("%dx%d_%ddpi_%.0f%%", 
            display.width, display.height, display.dpi, display.osScale * 100);
        if (DISPLAY_RECOMMENDATIONS.containsKey(osScaleKey)) {
            return DISPLAY_RECOMMENDATIONS.get(osScaleKey);
        }
        
        // Calculate based on DPI and resolution
        return calculateDynamicRecommendation(display);
    }
    
    /**
     * Calculate scaling recommendation dynamically based on display properties
     */
    private static ScalingRecommendation calculateDynamicRecommendation(DisplayConfig display) {
        // Base scaling calculation
        float baseScale = calculateBaseScale(display);
        
        // Component-specific adjustments
        float notificationScale = baseScale * 0.9f; // Notifications slightly smaller
        float taskbarScale = baseScale; // Taskbar follows base scale
        float itemCellScale = baseScale * 0.95f; // Items slightly smaller for density
        float otherScale = baseScale;
        
        // Apply OS scaling factor
        baseScale *= display.osScale;
        notificationScale *= display.osScale;
        taskbarScale *= display.osScale;
        itemCellScale *= display.osScale;
        otherScale *= display.osScale;
        
        // Clamp values to reasonable ranges
        baseScale = Math.max(0.5f, Math.min(5.0f, baseScale));
        notificationScale = Math.max(0.5f, Math.min(5.0f, notificationScale));
        taskbarScale = Math.max(0.5f, Math.min(5.0f, taskbarScale));
        itemCellScale = Math.max(0.5f, Math.min(5.0f, itemCellScale));
        otherScale = Math.max(0.5f, Math.min(5.0f, otherScale));
        
        String reasoning = String.format(
            "Dynamic calculation: %dx%d @ %d DPI (%.1f\" diagonal) with %.0f%% OS scaling",
            display.width, display.height, display.dpi, display.getDiagonalInches(), display.osScale * 100
        );
        
        return new ScalingRecommendation(baseScale, notificationScale, taskbarScale, itemCellScale, otherScale, reasoning);
    }
    
    /**
     * Calculate base scaling factor based on DPI and screen size
     */
    private static float calculateBaseScale(DisplayConfig display) {
        // Reference: 96 DPI at 1920x1080 = 1.0x scale
        float dpiScale = display.dpi / 96.0f;
        
        // Adjust for physical screen size
        double diagonalInches = display.getDiagonalInches();
        float sizeAdjustment = 1.0f;
        
        if (diagonalInches < 15) {
            // Small screens need larger UI elements
            sizeAdjustment = 1.2f;
        } else if (diagonalInches > 30) {
            // Very large screens can use slightly smaller elements
            sizeAdjustment = 0.9f;
        }
        
        return dpiScale * sizeAdjustment;
    }
    
    /**
     * Get all available recommendations
     */
    public static Map<String, ScalingRecommendation> getAllRecommendations() {
        return new HashMap<>(DISPLAY_RECOMMENDATIONS);
    }
    
    /**
     * Get recommendation by key
     */
    public static ScalingRecommendation getRecommendation(String key) {
        return DISPLAY_RECOMMENDATIONS.get(key);
    }
    
    /**
     * Check if configuration exists for given parameters
     */
    public static boolean hasRecommendation(int width, int height, int dpi) {
        String key = String.format("%dx%d_%ddpi", width, height, dpi);
        return DISPLAY_RECOMMENDATIONS.containsKey(key);
    }
}
