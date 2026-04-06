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
 * Auto-scaling settings panel for the reconciled component scale map.
 */
public class AutoScalingSettingsPanel extends JPanel {
    private static final int MIN_SCALE = 5;
    private static final int MAX_STANDARD_SCALE = 40;
    private static final int MAX_OTHER_SCALE = 20;
    private static final float DEFAULT_SCALE = 1.0f;
    private static final int SCALE_MULTIPLIER = 10;
    private static final int PERCENTAGE_MULTIPLIER = 100;
    private static final String NOTIFICATION_KEY = "notification";
    private static final String TASKBAR_KEY = "taskbar";
    private static final String ITEMCELL_KEY = "itemcell";
    private static final String OTHER_KEY = "other";
    private static final int GRID_ROWS = 4;
    private static final int GRID_COLS = 4;
    private static final int GRID_SPACING = 5;
    private static final int RECOMMENDATION_GRID_COLS = 1;
    private static final int RECOMMENDATION_SPACING = 2;
    private static final float REGULAR_FONT_SIZE = 14f;
    private static final float BUTTON_FONT_SIZE = 12f;

    private final ComponentsFactory componentsFactory;
    private final SettingsLayoutMetrics layoutMetrics;
    private final KeyValueConfigurationService<String, Float> scaleConfig;
    private Map<String, Float> scaleData;
    private final ScalingLookupTable.ScalingRecommendation recommendation;

    private JSlider notificationSlider;
    private JSlider taskbarSlider;
    private JSlider itemcellSlider;
    private JSlider otherSlider;

    private JLabel notificationValue;
    private JLabel taskbarValue;
    private JLabel itemcellValue;
    private JLabel otherValue;

    private JLabel notificationRecommendation;
    private JLabel taskbarRecommendation;
    private JLabel itemcellRecommendation;
    private JLabel otherRecommendation;

    private JButton notificationRecommendButton;
    private JButton taskbarRecommendButton;
    private JButton itemcellRecommendButton;
    private JButton otherRecommendButton;

    public AutoScalingSettingsPanel() {
        this(ComponentsFactory.INSTANCE);
    }

    public AutoScalingSettingsPanel(ComponentsFactory componentsFactory) {
        this.componentsFactory = componentsFactory;
        this.layoutMetrics = new SettingsLayoutMetrics(this.componentsFactory);
        this.scaleConfig = Configuration.get().scaleConfiguration();
        this.scaleData = CloneHelper.cloneObject(this.scaleConfig.getMap());
        this.recommendation = this.componentsFactory.getScalingRecommendations();

        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createLineBorder(AppThemeColor.ADR_PANEL_BORDER));
        this.setBackground(AppThemeColor.ADR_BG);

        this.initComponents();
    }

    private void initComponents() {
        int gridSpacing = this.layoutMetrics.scaleValue(GRID_SPACING);
        JPanel contentPanel = this.componentsFactory.getJPanel(new GridLayout(GRID_ROWS, GRID_COLS, gridSpacing, gridSpacing));
        contentPanel.setBackground(AppThemeColor.ADR_BG);

        this.initSlider(NOTIFICATION_KEY);
        this.initSlider(TASKBAR_KEY);
        this.initSlider(ITEMCELL_KEY);
        this.initSlider(OTHER_KEY);

        contentPanel.add(this.componentsFactory.getTextLabel("Notification:", FontStyle.REGULAR, REGULAR_FONT_SIZE));
        contentPanel.add(this.notificationSlider);
        contentPanel.add(this.notificationValue);
        contentPanel.add(this.notificationRecommendButton);

        contentPanel.add(this.componentsFactory.getTextLabel("Taskbar:", FontStyle.REGULAR, REGULAR_FONT_SIZE));
        contentPanel.add(this.taskbarSlider);
        contentPanel.add(this.taskbarValue);
        contentPanel.add(this.taskbarRecommendButton);

        contentPanel.add(this.componentsFactory.getTextLabel("Item Cell:", FontStyle.REGULAR, REGULAR_FONT_SIZE));
        contentPanel.add(this.itemcellSlider);
        contentPanel.add(this.itemcellValue);
        contentPanel.add(this.itemcellRecommendButton);

        contentPanel.add(this.componentsFactory.getTextLabel("Other UI:", FontStyle.REGULAR, REGULAR_FONT_SIZE));
        contentPanel.add(this.otherSlider);
        contentPanel.add(this.otherValue);
        contentPanel.add(this.otherRecommendButton);

        this.add(contentPanel, BorderLayout.CENTER);

        int recommendationSpacing = this.layoutMetrics.scaleValue(RECOMMENDATION_SPACING);
        JPanel recommendationPanel = this.componentsFactory.getJPanel(new GridLayout(
                GRID_ROWS,
                RECOMMENDATION_GRID_COLS,
                recommendationSpacing,
                recommendationSpacing));
        recommendationPanel.setBackground(AppThemeColor.ADR_BG);
        recommendationPanel.add(this.notificationRecommendation);
        recommendationPanel.add(this.taskbarRecommendation);
        recommendationPanel.add(this.itemcellRecommendation);
        recommendationPanel.add(this.otherRecommendation);

        this.add(recommendationPanel, BorderLayout.SOUTH);
        this.updateRecommendations();
    }

    private void initSlider(String componentType) {
        float currentValue = clampScaleValue(componentType, this.scaleData.getOrDefault(componentType, DEFAULT_SCALE));
        int sliderValue = toSliderValue(currentValue);
        int maxSliderValue = getMaximumSliderValue(componentType);
        this.scaleData.put(componentType, currentValue);

        switch (componentType) {
            case NOTIFICATION_KEY:
                this.notificationSlider = this.componentsFactory.getSlider(MIN_SCALE, maxSliderValue, sliderValue);
                this.notificationValue = this.componentsFactory.getTextLabel(formatCurrentValue(sliderValue), FontStyle.REGULAR, REGULAR_FONT_SIZE);
                this.notificationRecommendation = this.componentsFactory.getTextLabel("", FontStyle.REGULAR, REGULAR_FONT_SIZE);
                this.notificationRecommendButton = this.componentsFactory.getBorderedButton(TranslationKey.set_to_recommended.value(), BUTTON_FONT_SIZE);
                this.setupSliderEvents(this.notificationSlider, this.notificationValue, componentType);
                this.setupRecommendButton(this.notificationRecommendButton, this.notificationSlider, this.notificationValue, componentType);
                break;
            case TASKBAR_KEY:
                this.taskbarSlider = this.componentsFactory.getSlider(MIN_SCALE, maxSliderValue, sliderValue);
                this.taskbarValue = this.componentsFactory.getTextLabel(formatCurrentValue(sliderValue), FontStyle.REGULAR, REGULAR_FONT_SIZE);
                this.taskbarRecommendation = this.componentsFactory.getTextLabel("", FontStyle.REGULAR, REGULAR_FONT_SIZE);
                this.taskbarRecommendButton = this.componentsFactory.getBorderedButton(TranslationKey.set_to_recommended.value(), BUTTON_FONT_SIZE);
                this.setupSliderEvents(this.taskbarSlider, this.taskbarValue, componentType);
                this.setupRecommendButton(this.taskbarRecommendButton, this.taskbarSlider, this.taskbarValue, componentType);
                break;
            case ITEMCELL_KEY:
                this.itemcellSlider = this.componentsFactory.getSlider(MIN_SCALE, maxSliderValue, sliderValue);
                this.itemcellValue = this.componentsFactory.getTextLabel(formatCurrentValue(sliderValue), FontStyle.REGULAR, REGULAR_FONT_SIZE);
                this.itemcellRecommendation = this.componentsFactory.getTextLabel("", FontStyle.REGULAR, REGULAR_FONT_SIZE);
                this.itemcellRecommendButton = this.componentsFactory.getBorderedButton(TranslationKey.set_to_recommended.value(), BUTTON_FONT_SIZE);
                this.setupSliderEvents(this.itemcellSlider, this.itemcellValue, componentType);
                this.setupRecommendButton(this.itemcellRecommendButton, this.itemcellSlider, this.itemcellValue, componentType);
                break;
            case OTHER_KEY:
                this.otherSlider = this.componentsFactory.getSlider(MIN_SCALE, maxSliderValue, sliderValue);
                this.otherValue = this.componentsFactory.getTextLabel(formatCurrentValue(sliderValue), FontStyle.REGULAR, REGULAR_FONT_SIZE);
                this.otherRecommendation = this.componentsFactory.getTextLabel("", FontStyle.REGULAR, REGULAR_FONT_SIZE);
                this.otherRecommendButton = this.componentsFactory.getBorderedButton(TranslationKey.set_to_recommended.value(), BUTTON_FONT_SIZE);
                this.setupSliderEvents(this.otherSlider, this.otherValue, componentType);
                this.setupRecommendButton(this.otherRecommendButton, this.otherSlider, this.otherValue, componentType);
                break;
            default:
                break;
        }
    }

    private void setupSliderEvents(JSlider slider, JLabel valueLabel, String componentType) {
        slider.addChangeListener(e -> {
            int value = slider.getValue();
            float scaleValue = clampScaleValue(componentType, toScaleValue(value));
            valueLabel.setText(formatCurrentValue(value));
            this.scaleData.put(componentType, scaleValue);
            this.updateRealTimeScaling(componentType, scaleValue);
        });
    }

    private void setupRecommendButton(JButton button, JSlider slider, JLabel valueLabel, String componentType) {
        button.addActionListener(e -> {
            float recommendedValue = clampScaleValue(componentType, getRecommendedValue(componentType));
            int sliderValue = toSliderValue(recommendedValue);
            slider.setValue(sliderValue);
            valueLabel.setText(formatCurrentValue(sliderValue));
            this.scaleData.put(componentType, recommendedValue);
            this.updateRealTimeScaling(componentType, recommendedValue);
        });
    }

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
            default:
                break;
        }
    }

    private float getRecommendedValue(String componentType) {
        if (this.recommendation == null) {
            return DEFAULT_SCALE;
        }

        switch (componentType) {
            case NOTIFICATION_KEY:
                return this.recommendation.notificationScale;
            case TASKBAR_KEY:
                return this.recommendation.taskbarScale;
            case ITEMCELL_KEY:
                return this.recommendation.itemCellScale;
            case OTHER_KEY:
                return this.recommendation.otherScale;
            default:
                return DEFAULT_SCALE;
        }
    }

    private void updateRecommendations() {
        if (this.recommendation == null) {
            return;
        }

        this.notificationRecommendation.setText(formatRecommendedValue(this.recommendation.notificationScale));
        this.taskbarRecommendation.setText(formatRecommendedValue(this.recommendation.taskbarScale));
        this.itemcellRecommendation.setText(formatRecommendedValue(this.recommendation.itemCellScale));
        this.otherRecommendation.setText(formatRecommendedValue(this.recommendation.otherScale));
    }

    public void applyConfiguration() {
        this.scaleConfig.set(CloneHelper.cloneObject(this.scaleData));
    }

    public void restoreConfiguration() {
        this.scaleData = CloneHelper.cloneObject(this.scaleConfig.getMap());
        if (this.notificationSlider != null) {
            this.notificationSlider.setValue(toSliderValue(clampScaleValue(NOTIFICATION_KEY, this.scaleData.getOrDefault(NOTIFICATION_KEY, DEFAULT_SCALE))));
        }
        if (this.taskbarSlider != null) {
            this.taskbarSlider.setValue(toSliderValue(clampScaleValue(TASKBAR_KEY, this.scaleData.getOrDefault(TASKBAR_KEY, DEFAULT_SCALE))));
        }
        if (this.itemcellSlider != null) {
            this.itemcellSlider.setValue(toSliderValue(clampScaleValue(ITEMCELL_KEY, this.scaleData.getOrDefault(ITEMCELL_KEY, DEFAULT_SCALE))));
        }
        if (this.otherSlider != null) {
            this.otherSlider.setValue(toSliderValue(clampScaleValue(OTHER_KEY, this.scaleData.getOrDefault(OTHER_KEY, DEFAULT_SCALE))));
        }
        this.updateRecommendations();
    }

    private static int getMaximumSliderValue(String componentType) {
        return OTHER_KEY.equals(componentType) ? MAX_OTHER_SCALE : MAX_STANDARD_SCALE;
    }

    private static float clampScaleValue(String componentType, float scaleValue) {
        float maximumScale = OTHER_KEY.equals(componentType) ? toScaleValue(MAX_OTHER_SCALE) : toScaleValue(MAX_STANDARD_SCALE);
        return Math.max(toScaleValue(MIN_SCALE), Math.min(maximumScale, scaleValue));
    }

    private static int toSliderValue(float scaleValue) {
        return Math.round(scaleValue * SCALE_MULTIPLIER);
    }

    private static float toScaleValue(int sliderValue) {
        return sliderValue / (float) SCALE_MULTIPLIER;
    }

    private String formatCurrentValue(int sliderValue) {
        return String.format(TranslationKey.current_value.value(), sliderValue * SCALE_MULTIPLIER + "%");
    }

    private String formatRecommendedValue(float recommendedValue) {
        return String.format(
                TranslationKey.recommended_value.value(),
                Math.round(recommendedValue * PERCENTAGE_MULTIPLIER) + "%");
    }
}
