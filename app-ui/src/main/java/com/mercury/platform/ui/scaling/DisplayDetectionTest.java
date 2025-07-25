package com.mercury.platform.ui.scaling;

import com.mercury.platform.ui.components.ComponentsFactory;

/**
 * Test utility to verify display detection and scaling recommendations.
 * This is a simple test class to validate the detection logic implementation.
 */
public class DisplayDetectionTest {
    
    public static void main(String[] args) {
        System.out.println("=== Mercury Trade Display Detection Test ===\n");
        
        try {
            // Test basic display detection
            testDisplayDetection();
            
            // Test scaling recommendations
            testScalingRecommendations();
            
            // Test multi-monitor detection
            testMultiMonitorDetection();
            
            // Test ComponentsFactory integration
            testComponentsFactoryIntegration();
            
            System.out.println("=== All tests completed successfully ===");
            
        } catch (Exception e) {
            System.err.println("Test failed with error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testDisplayDetection() {
        System.out.println("1. Testing basic display detection...");
        
        DisplayDetector.DisplayInfo primaryDisplay = DisplayDetector.detectPrimaryDisplay();
        System.out.println("Primary Display: " + primaryDisplay);
        System.out.println("Effective OS Scale: " + (primaryDisplay.getEffectiveScale() * 100) + "%");
        System.out.println();
    }
    
    private static void testScalingRecommendations() {
        System.out.println("2. Testing scaling recommendations...");
        
        DisplayDetector.DisplayInfo display = DisplayDetector.detectPrimaryDisplay();
        ScalingLookupTable.DisplayConfig config = display.toDisplayConfig();
        ScalingLookupTable.ScalingRecommendation recommendation = 
            ScalingLookupTable.calculateRecommendation(config);
        
        System.out.println("Display Config: " + config);
        System.out.println("Base Scale: " + (recommendation.baseScale * 100) + "%");
        System.out.println("Notification Scale: " + (recommendation.notificationScale * 100) + "%");
        System.out.println("Taskbar Scale: " + (recommendation.taskbarScale * 100) + "%");
        System.out.println("Reasoning: " + recommendation.reasoning);
        System.out.println();
    }
    
    private static void testMultiMonitorDetection() {
        System.out.println("3. Testing multi-monitor detection...");
        
        DisplayDetector.DisplayInfo[] allDisplays = DisplayDetector.detectAllDisplays();
        System.out.println("Detected " + allDisplays.length + " display(s):");
        
        for (int i = 0; i < allDisplays.length; i++) {
            System.out.println("  Display " + (i + 1) + ": " + allDisplays[i]);
        }
        System.out.println();
    }
    
    private static void testComponentsFactoryIntegration() {
        System.out.println("4. Testing ComponentsFactory integration...");
        
        ComponentsFactory factory = ComponentsFactory.INSTANCE;
        
        // Test display configuration info
        System.out.println("Display Configuration Info:");
        System.out.println(factory.getDisplayConfigurationInfo());
        
        // Test optimal scaling check
        boolean isOptimal = factory.isCurrentScalingOptimal();
        System.out.println("Is current scaling optimal? " + isOptimal);
        
        // Test recommendations access
        ScalingLookupTable.ScalingRecommendation recommendations = factory.getScalingRecommendations();
        System.out.println("Current recommendations - Base: " + (recommendations.baseScale * 100) + "%");
        System.out.println();
    }
}
