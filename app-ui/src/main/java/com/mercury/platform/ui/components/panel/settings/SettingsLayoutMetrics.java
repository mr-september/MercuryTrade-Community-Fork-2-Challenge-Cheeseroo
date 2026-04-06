package com.mercury.platform.ui.components.panel.settings;

import com.mercury.platform.ui.components.ComponentsFactory;

import java.awt.*;

/**
 * Provides DPI-aware scaling for settings layout values.
 */
public class SettingsLayoutMetrics {
    private static final Dimension FRAME_SIZE = new Dimension(1000, 600);
    private static final Dimension SETUP_DIALOG_BUTTON_SIZE = new Dimension(100, 26);
    private static final Dimension FOOTER_ACTION_BUTTON_SIZE = new Dimension(110, 26);
    private static final Dimension OPERATION_BUTTON_SIZE = new Dimension(210, 35);
    private static final Dimension MENU_PANEL_SIZE = new Dimension(220, 20);
    private static final Dimension MENU_ENTRY_SIZE = new Dimension(220, 50);
    private static final Dimension RESPONSE_TITLE_SIZE = new Dimension(120, 26);
    private static final Dimension RESPONSE_HOTKEY_HEADER_SIZE = new Dimension(130, 20);
    private static final Dimension HOTKEY_PANEL_SIZE = new Dimension(110, 26);
    private static final Dimension SUPPORT_TEXT_SIZE = new Dimension(300, 450);
    private static final Dimension SUPPORT_SCROLLBAR_SIZE = new Dimension(14, Integer.MAX_VALUE);
    private static final Dimension NOTIFICATION_LIMIT_SIZE = new Dimension(30, 26);
    private static final Dimension DONATION_TRACKER_SIZE = new Dimension(100, 20);

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

    public Dimension getFrameSize() {
        return scaleDimension(FRAME_SIZE);
    }

    public Dimension getSetupDialogButtonSize() {
        return scaleDimension(SETUP_DIALOG_BUTTON_SIZE);
    }

    public Dimension getFooterActionButtonSize() {
        return scaleDimension(FOOTER_ACTION_BUTTON_SIZE);
    }

    public Dimension getOperationButtonSize() {
        return scaleDimension(OPERATION_BUTTON_SIZE);
    }

    public Dimension getMenuPanelSize() {
        return scaleDimension(MENU_PANEL_SIZE);
    }

    public Dimension getMenuEntrySize() {
        return scaleDimension(MENU_ENTRY_SIZE);
    }

    public Dimension getResponseTitleSize() {
        return scaleDimension(RESPONSE_TITLE_SIZE);
    }

    public Dimension getResponseHotKeyHeaderSize() {
        return scaleDimension(RESPONSE_HOTKEY_HEADER_SIZE);
    }

    public Dimension getHotKeyPanelSize() {
        return scaleDimension(HOTKEY_PANEL_SIZE);
    }

    public Dimension getSupportTextSize() {
        return scaleDimension(SUPPORT_TEXT_SIZE);
    }

    public Dimension getSupportScrollbarSize() {
        return scaleDimension(SUPPORT_SCROLLBAR_SIZE);
    }

    public Dimension getNotificationLimitSize() {
        return scaleDimension(NOTIFICATION_LIMIT_SIZE);
    }

    public Dimension getDonationTrackerSize() {
        return scaleDimension(DONATION_TRACKER_SIZE);
    }

    private Dimension scaleDimension(Dimension logicalSize) {
        int width = logicalSize.width == Integer.MAX_VALUE
                ? Integer.MAX_VALUE
                : this.componentsFactory.convertSize(new Dimension(logicalSize.width, 1)).width;
        int height = logicalSize.height == Integer.MAX_VALUE
                ? Integer.MAX_VALUE
                : this.componentsFactory.convertSize(new Dimension(1, logicalSize.height)).height;
        return new Dimension(width, height);
    }
}
