package com.mercury.platform.ui.components.panel.settings;

import com.mercury.platform.TranslationKey;
import com.mercury.platform.shared.CloneHelper;
import com.mercury.platform.shared.config.Configuration;
import com.mercury.platform.shared.config.configration.KeyValueConfigurationService;
import com.mercury.platform.ui.components.ComponentsFactory;
import com.mercury.platform.ui.components.fields.font.FontStyle;
import com.mercury.platform.ui.misc.AppThemeColor;
import com.mercury.platform.ui.misc.MercuryStoreUI;
import com.mercury.platform.ui.scaling.ScalingLookupTable;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * Simplified auto-scaling settings panel that shows recommended scaling values
 * and allows users to easily apply them.
 */
public class AutoScalingSettingsPanel extends JPanel {
    // Constants for better maintainability
    private static final int MIN_SCALE = 5;
    private static final int MAX_SCALE = 100;
    private static final float DEFAULT_SCALE = 1.0f;
    private static final int SCALE_MULTIPLIER = 10;
    private static final int PERCENTAGE_MULTIPLIER = 100;
    private static final String NOTIFICATION_KEY = "notification";
    private static final String TASKBAR_KEY = "taskbar";
    private static final String ITEMCELL_KEY = "itemcell";
    private static final int GRID_ROWS = 3;
    private static final int GRID_COLS = 4;
    private static final int GRID_SPACING = 5;
    private static final int RECOMMENDATION_GRID_COLS = 1;
    private static final int RECOMMENDATION_SPACING = 2;
    private static final float REGULAR_FONT_SIZE = 14f;
    private static final float BUTTON_FONT_SIZE = 12f;

    private final ComponentsFactory componentsFactory;
    private final KeyValueConfigurationService<String, Float> scaleConfig;
    private Map<String, Float> scaleData;
    private ScalingLookupTable.ScalingRecommendation recommendation;

    // UI Components
    private JSlider notificationSlider;
    private JSlider taskbarSlider;
    private JSlider itemcellSlider;
    
    private JLabel notificationValue;
    private JLabel taskbarValue; 
    private JLabel itemcellValue;
    
    private JLabel notificationRecommendation;
    private JLabel taskbarRecommendation;
    private JLabel itemcellRecommendation;
    
    private JButton notificationRecommendButton;
    private JButton taskbarRecommendButton;
    private JButton itemcellRecommendButton;
    
    public AutoScalingSettingsPanel() {
        this.componentsFactory = ComponentsFactory.INSTANCE;
        this.scaleConfig = Configuration.get().scaleConfiguration();
        this.scaleData = CloneHelper.cloneObject(this.scaleConfig.getMap());
        this.recommendation = this.componentsFactory.getScalingRecommendations();
        
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(AppThemeColor.ADR_PANEL_BORDER));
        setBackground(AppThemeColor.ADR_BG);
        
        initComponents();
    }
    
    private void initComponents() {
        JPanel contentPanel = componentsFactory.getJPanel(new GridLayout(GRID_ROWS, GRID_COLS, GRID_SPACING, GRID_SPACING));
        contentPanel.setBackground(AppThemeColor.ADR_BG);
        
        // Initialize sliders for each component type
        initSlider(NOTIFICATION_KEY);
        initSlider(TASKBAR_KEY);
        initSlider(ITEMCELL_KEY);
        
        // Add components to grid: Label | Slider | Current Value | Recommended Button
        contentPanel.add(componentsFactory.getTextLabel("Notification:", FontStyle.REGULAR, REGULAR_FONT_SIZE));
        contentPanel.add(notificationSlider);
        contentPanel.add(notificationValue);
        contentPanel.add(notificationRecommendButton);
        
        contentPanel.add(componentsFactory.getTextLabel("Taskbar:", FontStyle.REGULAR, REGULAR_FONT_SIZE));
        contentPanel.add(taskbarSlider);
        contentPanel.add(taskbarValue);
        contentPanel.add(taskbarRecommendButton);
        
        contentPanel.add(componentsFactory.getTextLabel("Item Cell:", FontStyle.REGULAR, REGULAR_FONT_SIZE));
        contentPanel.add(itemcellSlider);
        contentPanel.add(itemcellValue);
        contentPanel.add(itemcellRecommendButton);
        
        add(contentPanel, BorderLayout.CENTER);
        
        // Add recommendation info at the bottom
        JPanel recommendationPanel = componentsFactory.getJPanel(new GridLayout(GRID_ROWS, RECOMMENDATION_GRID_COLS, RECOMMENDATION_SPACING, RECOMMENDATION_SPACING));
        recommendationPanel.setBackground(AppThemeColor.ADR_BG);
        recommendationPanel.add(notificationRecommendation);
        recommendationPanel.add(taskbarRecommendation);
        recommendationPanel.add(itemcellRecommendation);
        
        add(recommendationPanel, BorderLayout.SOUTH);
        
        updateRecommendations();
    }
    
    private void initSlider(String componentType) {
        float currentValue = scaleData.getOrDefault(componentType, DEFAULT_SCALE);
        int sliderValue = Math.round(currentValue * SCALE_MULTIPLIER);
        
        switch (componentType) {
            case NOTIFICATION_KEY:
                notificationSlider = componentsFactory.getSlider(MIN_SCALE, MAX_SCALE, sliderValue);
                notificationValue = componentsFactory.getTextLabel(
                    formatCurrentValue(sliderValue),
                    FontStyle.REGULAR, REGULAR_FONT_SIZE);
                notificationRecommendation = componentsFactory.getTextLabel("", FontStyle.REGULAR, REGULAR_FONT_SIZE);
                notificationRecommendButton = componentsFactory.getBorderedButton(
                    TranslationKey.set_to_recommended.value(), BUTTON_FONT_SIZE);
                setupSliderEvents(notificationSlider, notificationValue, componentType);
                setupRecommendButton(notificationRecommendButton, notificationSlider, notificationValue, componentType);
                break;
                
            case TASKBAR_KEY:
                taskbarSlider = componentsFactory.getSlider(MIN_SCALE, MAX_SCALE, sliderValue);
                taskbarValue = componentsFactory.getTextLabel(
                    formatCurrentValue(sliderValue),
                    FontStyle.REGULAR, REGULAR_FONT_SIZE);
                taskbarRecommendation = componentsFactory.getTextLabel("", FontStyle.REGULAR, REGULAR_FONT_SIZE);
                taskbarRecommendButton = componentsFactory.getBorderedButton(
                    TranslationKey.set_to_recommended.value(), BUTTON_FONT_SIZE);
                setupSliderEvents(taskbarSlider, taskbarValue, componentType);
                setupRecommendButton(taskbarRecommendButton, taskbarSlider, taskbarValue, componentType);
                break;
                
            case ITEMCELL_KEY:
                itemcellSlider = componentsFactory.getSlider(MIN_SCALE, MAX_SCALE, sliderValue);
                itemcellValue = componentsFactory.getTextLabel(
                    formatCurrentValue(sliderValue),
                    FontStyle.REGULAR, REGULAR_FONT_SIZE);
                itemcellRecommendation = componentsFactory.getTextLabel("", FontStyle.REGULAR, REGULAR_FONT_SIZE);
                itemcellRecommendButton = componentsFactory.getBorderedButton(
                    TranslationKey.set_to_recommended.value(), BUTTON_FONT_SIZE);
                setupSliderEvents(itemcellSlider, itemcellValue, componentType);
                setupRecommendButton(itemcellRecommendButton, itemcellSlider, itemcellValue, componentType);
                break;
        }
    }
    
    private void setupSliderEvents(JSlider slider, JLabel valueLabel, String componentType) {
        slider.addChangeListener(e -> {
            int value = slider.getValue();
            float scaleValue = value / (float) SCALE_MULTIPLIER;
            valueLabel.setText(formatCurrentValue(value));
            scaleData.put(componentType, scaleValue);
            
            // Update the actual scaling in real-time using appropriate subjects
            updateRealTimeScaling(componentType, scaleValue);
        });
    }
    
    private void setupRecommendButton(JButton button, JSlider slider, JLabel valueLabel, String componentType) {
        button.addActionListener(e -> {
            float recommendedValue = getRecommendedValue(componentType);
            int sliderValue = Math.round(recommendedValue * SCALE_MULTIPLIER);
            slider.setValue(sliderValue);
            valueLabel.setText(formatCurrentValue(sliderValue));
            scaleData.put(componentType, recommendedValue);
            
            // Update the actual scaling in real-time using appropriate subjects
            updateRealTimeScaling(componentType, recommendedValue);
        });
    }
    
    /**
     * Update real-time scaling using the appropriate subject for each component type
     */
    private void updateRealTimeScaling(String componentType, float scaleValue) {
        switch (componentType) {
            case NOTIFICATION_KEY:
                MercuryStoreUI.notificationScaleSubject.onNext(scaleValue);
                break;
            case TASKBAR_KEY:
                MercuryStoreUI.taskBarScaleSubject.onNext(scaleValue);
                break;
            case ITEMCELL_KEY:
                MercuryStoreUI.itemPanelScaleSubject.onNext(scaleValue);
                break;
        }
    }
    
    private float getRecommendedValue(String componentType) {
        if (recommendation == null) {
            return DEFAULT_SCALE;
        }
        
        switch (componentType) {
            case NOTIFICATION_KEY:
                return recommendation.notificationScale;
            case TASKBAR_KEY:
                return recommendation.taskbarScale;
            case ITEMCELL_KEY:
                return recommendation.itemCellScale;
            default:
                return DEFAULT_SCALE;
        }
    }
    
    private void updateRecommendations() {
        if (recommendation == null) {
            return;
        }
        
        float notificationRec = recommendation.notificationScale;
        float taskbarRec = recommendation.taskbarScale;
        float itemcellRec = recommendation.itemCellScale;
        
        notificationRecommendation.setText(formatRecommendedValue(notificationRec));
        taskbarRecommendation.setText(formatRecommendedValue(taskbarRec));
        itemcellRecommendation.setText(formatRecommendedValue(itemcellRec));
    }
    
    /**
     * Format current value for display
     */
    private String formatCurrentValue(int sliderValue) {
        return String.format(TranslationKey.current_value.value(), sliderValue * SCALE_MULTIPLIER + "%");
    }
    
    /**
     * Format recommended value for display
     */
    private String formatRecommendedValue(float recommendedValue) {
        return String.format(TranslationKey.recommended_value.value(), 
            Math.round(recommendedValue * PERCENTAGE_MULTIPLIER) + "%");
    }

    /**
     * Apply the current configuration to persistent storage
     */
    public void applyConfiguration() {
        scaleConfig.set(scaleData);
    }

    /**
     * Restore configuration from persistent storage
     */
    public void restoreConfiguration() {
        this.scaleData = CloneHelper.cloneObject(this.scaleConfig.getMap());
        
        // Update UI components with restored values
        if (notificationSlider != null) {
            notificationSlider.setValue(Math.round(scaleData.getOrDefault(NOTIFICATION_KEY, DEFAULT_SCALE) * SCALE_MULTIPLIER));
        }
        if (taskbarSlider != null) {
            taskbarSlider.setValue(Math.round(scaleData.getOrDefault(TASKBAR_KEY, DEFAULT_SCALE) * SCALE_MULTIPLIER));
        }
        if (itemcellSlider != null) {
            itemcellSlider.setValue(Math.round(scaleData.getOrDefault(ITEMCELL_KEY, DEFAULT_SCALE) * SCALE_MULTIPLIER));
        }
        
        updateRecommendations();
    }
}
