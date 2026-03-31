package com.mercury.platform.ui.components.panel.settings;

import com.mercury.platform.ui.components.ComponentsFactory;

/**
 * Provides DPI-aware scaling for settings layout values.
 */
public class SettingsLayoutMetrics {
    private final ComponentsFactory componentsFactory;

    public SettingsLayoutMetrics(ComponentsFactory componentsFactory) {
        this.componentsFactory = componentsFactory;
    }

    /**
     * Scales a logical value by the current UI scale factor.
     *
     * @param value the logical (unscaled) value
     * @return the scaled pixel value
     */
    public int scaleValue(int value) {
        return Math.round(value * componentsFactory.getScale());
    }
}