package com.mercury.platform.ui.scaling;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * Utility class for detecting display characteristics including:
 * - Monitor resolution
 * - DPI (dots per inch)
 * - OS-level scaling settings
 * - Multi-monitor configurations
 */
public class DisplayDetector {
    
    private static final Logger log = LogManager.getLogger(DisplayDetector.class);
    
    /**
     * Detected display information
     */
    public static class DisplayInfo {
        public final int width;
        public final int height;
        public final int dpi;
        public final float osScaleX;
        public final float osScaleY;
        public final String deviceName;
        public final boolean isPrimary;
        public final Rectangle bounds;
        
        public DisplayInfo(int width, int height, int dpi, float osScaleX, float osScaleY, 
                          String deviceName, boolean isPrimary, Rectangle bounds) {
            this.width = width;
            this.height = height;
            this.dpi = dpi;
            this.osScaleX = osScaleX;
            this.osScaleY = osScaleY;
            this.deviceName = deviceName;
            this.isPrimary = isPrimary;
            this.bounds = bounds;
        }
        
        public float getEffectiveScale() {
            return Math.max(osScaleX, osScaleY);
        }
        
        public ScalingLookupTable.DisplayConfig toDisplayConfig() {
            return new ScalingLookupTable.DisplayConfig(
                width, height, dpi, getEffectiveScale(),
                String.format("%s (%dx%d @ %d DPI, %.0f%% scale)", 
                    deviceName, width, height, dpi, getEffectiveScale() * 100)
            );
        }
        
        @Override
        public String toString() {
            return String.format("Display[%s: %dx%d @ %d DPI, OS Scale: %.0f%%, Primary: %s]",
                deviceName, width, height, dpi, getEffectiveScale() * 100, isPrimary);
        }
    }
    
    /**
     * Detect the primary display configuration
     */
    public static DisplayInfo detectPrimaryDisplay() {
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice primaryDevice = ge.getDefaultScreenDevice();
            return detectDisplay(primaryDevice, true);
        } catch (Exception e) {
            log.warn("Failed to detect primary display, using fallback", e);
            return createFallbackDisplay();
        }
    }
    
    /**
     * Detect all available displays
     */
    public static DisplayInfo[] detectAllDisplays() {
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice[] devices = ge.getScreenDevices();
            GraphicsDevice primaryDevice = ge.getDefaultScreenDevice();
            
            DisplayInfo[] displays = new DisplayInfo[devices.length];
            
            for (int i = 0; i < devices.length; i++) {
                boolean isPrimary = devices[i].equals(primaryDevice);
                displays[i] = detectDisplay(devices[i], isPrimary);
            }
            
            return displays;
        } catch (Exception e) {
            log.warn("Failed to detect all displays, returning primary only", e);
            return new DisplayInfo[] { detectPrimaryDisplay() };
        }
    }
    
    /**
     * Detect configuration for a specific graphics device
     */
    private static DisplayInfo detectDisplay(GraphicsDevice device, boolean isPrimary) {
        try {
            GraphicsConfiguration config = device.getDefaultConfiguration();
            Rectangle bounds = config.getBounds();
            
            // Get display dimensions
            int width = bounds.width;
            int height = bounds.height;
            
            // Detect OS scaling
            AffineTransform transform = config.getDefaultTransform();
            float osScaleX = (float) transform.getScaleX();
            float osScaleY = (float) transform.getScaleY();
            
            // Calculate DPI
            int dpi = calculateDPI(device, config);
            
            // Get device identifier
            String deviceName = device.getIDstring();
            if (deviceName == null || deviceName.trim().isEmpty()) {
                deviceName = isPrimary ? "Primary Display" : "Secondary Display";
            }
            
            log.info("Detected display: {} ({}x{} @ {} DPI, Scale: {}x{})", 
                    deviceName, width, height, dpi, osScaleX, osScaleY);
            
            return new DisplayInfo(width, height, dpi, osScaleX, osScaleY, deviceName, isPrimary, bounds);
            
        } catch (Exception e) {
            log.warn("Failed to detect display configuration for device: " + device, e);
            return createFallbackDisplay();
        }
    }
    
    /**
     * Calculate DPI for a graphics device
     */
    private static int calculateDPI(GraphicsDevice device, GraphicsConfiguration config) {
        try {
            // Get base DPI from toolkit
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            int screenDPI = toolkit.getScreenResolution();
            
            // Note: AffineTransform already accounts for OS scaling,
            // so we don't need to multiply DPI by scale factor again.
            // The physical DPI remains constant; OS scaling is handled separately.
            
            // Validate DPI is reasonable (between 72 and 600)
            if (screenDPI < 72) {
                log.warn("Detected DPI too low ({}), using 96 DPI", screenDPI);
                screenDPI = 96;
            } else if (screenDPI > 600) {
                log.warn("Detected DPI too high ({}), capping at 600 DPI", screenDPI);
                screenDPI = 600;
            }
            
            return screenDPI;
            
        } catch (Exception e) {
            log.warn("Failed to calculate DPI, using default 96 DPI", e);
            return 96; // Standard DPI fallback
        }
    }
    
    /**
     * Create a fallback display configuration for error cases
     */
    private static DisplayInfo createFallbackDisplay() {
        log.info("Using fallback display configuration: 1920x1080 @ 96 DPI");
        Rectangle bounds = new Rectangle(0, 0, 1920, 1080);
        return new DisplayInfo(1920, 1080, 96, 1.0f, 1.0f, "Fallback Display", true, bounds);
    }
    
    /**
     * Detect the display that contains most of a given window
     */
    public static DisplayInfo detectDisplayForWindow(Window window) {
        if (window == null) {
            return detectPrimaryDisplay();
        }
        
        try {
            Rectangle windowBounds = window.getBounds();
            DisplayInfo[] displays = detectAllDisplays();
            
            DisplayInfo bestMatch = displays[0]; // Default to first display
            int maxOverlap = 0;
            
            for (DisplayInfo display : displays) {
                Rectangle intersection = windowBounds.intersection(display.bounds);
                int overlapArea = intersection.width * intersection.height;
                
                if (overlapArea > maxOverlap) {
                    maxOverlap = overlapArea;
                    bestMatch = display;
                }
            }
            
            return bestMatch;
            
        } catch (Exception e) {
            log.warn("Failed to detect display for window, using primary", e);
            return detectPrimaryDisplay();
        }
    }
    
    /**
     * Check if system supports high DPI
     */
    public static boolean isHighDPISupported() {
        try {
            // Check if Java supports high DPI scaling
            String javaVersion = System.getProperty("java.version");
            log.debug("Java version: {}", javaVersion);
            
            // Java 9+ has better high DPI support
            String[] versionParts = javaVersion.split("\\.");
            int majorVersion = Integer.parseInt(versionParts[0]);
            
            return majorVersion >= 9;
            
        } catch (Exception e) {
            log.warn("Failed to determine Java version for high DPI support", e);
            return false;
        }
    }
    
    /**
     * Get system-specific scaling information
     */
    public static String getSystemScalingInfo() {
        try {
            StringBuilder info = new StringBuilder();
            
            // OS information
            String osName = System.getProperty("os.name");
            String osVersion = System.getProperty("os.version");
            info.append(String.format("OS: %s %s\n", osName, osVersion));
            
            // Java information
            String javaVersion = System.getProperty("java.version");
            info.append(String.format("Java: %s\n", javaVersion));
            
            // Display information
            DisplayInfo[] displays = detectAllDisplays();
            info.append(String.format("Displays: %d detected\n", displays.length));
            
            for (int i = 0; i < displays.length; i++) {
                DisplayInfo display = displays[i];
                info.append(String.format("  %d: %s\n", i + 1, display.toString()));
            }
            
            // High DPI support
            info.append(String.format("High DPI Support: %s\n", isHighDPISupported()));
            
            // System properties related to scaling
            String[] scalingProperties = {
                "sun.java2d.uiScale",
                "sun.java2d.win.uiScaleX", 
                "sun.java2d.win.uiScaleY",
                "sun.java2d.dpiaware"
            };
            
            info.append("System Properties:\n");
            for (String prop : scalingProperties) {
                String value = System.getProperty(prop);
                if (value != null) {
                    info.append(String.format("  %s = %s\n", prop, value));
                }
            }
            
            return info.toString();
            
        } catch (Exception e) {
            log.warn("Failed to get system scaling info", e);
            return "Failed to retrieve system scaling information: " + e.getMessage();
        }
    }
}
