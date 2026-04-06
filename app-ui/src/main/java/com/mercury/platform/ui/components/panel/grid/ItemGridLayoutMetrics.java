package com.mercury.platform.ui.components.panel.grid;

import com.mercury.platform.ui.components.ComponentsFactory;

import java.awt.*;

public class ItemGridLayoutMetrics {
    private static final Dimension LIVE_RIGHT_SPACER_SIZE = new Dimension(18, 668);
    private static final Dimension LIVE_BOTTOM_SPACER_SIZE = new Dimension(661, 16);
    private static final Dimension QUAD_TOGGLE_SIZE = new Dimension(16, 16);
    private static final Dimension SAVED_TABS_PANEL_SIZE = new Dimension(50, 56);

    private final ComponentsFactory componentsFactory;

    public ItemGridLayoutMetrics(ComponentsFactory componentsFactory) {
        this.componentsFactory = componentsFactory;
    }

    public Dimension getLiveRightSpacerSize() {
        return scaleDimension(LIVE_RIGHT_SPACER_SIZE);
    }

    public Dimension getLiveBottomSpacerSize() {
        return scaleDimension(LIVE_BOTTOM_SPACER_SIZE);
    }

    public Insets getLiveBottomSpacerInsets() {
        return scaleInsets(-10, 0, 0, 0);
    }

    public Insets getHeaderTabsInsets() {
        return scaleInsets(20, 0, 26, 0);
    }

    public Insets getNicknameInsets() {
        return scaleInsets(-6, 0, -6, 0);
    }

    public Insets getTabInfoInsets() {
        return scaleInsets(-8, 0, -6, 0);
    }

    public Dimension getQuadToggleSize() {
        return scaleDimension(QUAD_TOGGLE_SIZE);
    }

    public int getPinTabTypeWidth() {
        return scaleValue(70);
    }

    public Dimension getSavedTabsPanelSize() {
        return scaleDimension(SAVED_TABS_PANEL_SIZE);
    }

    public Dimension getHorizontalScrollBarSize() {
        return new Dimension(Integer.MAX_VALUE, scaleValue(16));
    }

    public Insets getHorizontalScrollBarInsets() {
        return scaleInsets(2, 2, 2, 2);
    }

    public int scaleValue(int value) {
        return Math.round(this.componentsFactory.getScale() * value);
    }

    private Dimension scaleDimension(Dimension initialSize) {
        return this.componentsFactory.convertSize(initialSize);
    }

    private Insets scaleInsets(int top, int left, int bottom, int right) {
        return new Insets(scaleValue(top), scaleValue(left), scaleValue(bottom), scaleValue(right));
    }
}
