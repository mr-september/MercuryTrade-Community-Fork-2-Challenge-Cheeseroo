package com.mercury.platform.ui.components.panel.settings;

import com.mercury.platform.ui.components.ComponentsFactory;

import javax.swing.*;
import java.awt.*;

/**
 * Reusable two-column responsive form builder for settings pages.
 * Avoids GridLayout equal-row-height behavior when one row contains a tall control.
 */
public class SettingsFormBuilder {
    private static final int DEFAULT_ROW_INSET = 2;
    private static final int DEFAULT_COLUMN_INSET = 4;
    private static final double DEFAULT_LABEL_WEIGHT = 0.42d;

    private final ComponentsFactory componentsFactory;
    private final SettingsLayoutMetrics layoutMetrics;
    private final JPanel panel;

    private int rowIndex;
    private int rowInset;
    private int columnInset;
    private double labelWeight;
    private boolean built;

    public SettingsFormBuilder(ComponentsFactory componentsFactory, SettingsLayoutMetrics layoutMetrics, Color backgroundColor) {
        this.componentsFactory = componentsFactory;
        this.layoutMetrics = layoutMetrics;
        this.panel = this.componentsFactory.getJPanel(new GridBagLayout(), backgroundColor);
        this.rowInset = this.layoutMetrics.scaleValue(DEFAULT_ROW_INSET);
        this.columnInset = this.layoutMetrics.scaleValue(DEFAULT_COLUMN_INSET);
        this.labelWeight = DEFAULT_LABEL_WEIGHT;
        this.rowIndex = 0;
        this.built = false;
    }

    public SettingsFormBuilder setInsets(int logicalRowInset, int logicalColumnInset) {
        this.rowInset = this.layoutMetrics.scaleValue(logicalRowInset);
        this.columnInset = this.layoutMetrics.scaleValue(logicalColumnInset);
        return this;
    }

    public SettingsFormBuilder setLabelWeight(double labelWeight) {
        this.labelWeight = Math.max(0.1d, Math.min(0.9d, labelWeight));
        return this;
    }

    public SettingsFormBuilder addRow(JComponent label, JComponent value) {
        this.ensureNotBuilt();
        this.addComponent(label, 0, this.labelWeight);
        this.addComponent(value, 1, 1d - this.labelWeight);
        this.rowIndex++;
        return this;
    }

    public JPanel build() {
        if (!this.built) {
            GridBagConstraints spacer = new GridBagConstraints();
            spacer.gridx = 0;
            spacer.gridy = this.rowIndex;
            spacer.gridwidth = 2;
            spacer.weightx = 1d;
            spacer.weighty = 1d;
            spacer.fill = GridBagConstraints.BOTH;
            this.panel.add(Box.createVerticalGlue(), spacer);
            this.built = true;
        }
        return this.panel;
    }

    private void addComponent(JComponent component, int column, double weightX) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = column;
        constraints.gridy = this.rowIndex;
        constraints.insets = new Insets(this.rowInset, this.columnInset, this.rowInset, this.columnInset);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.weightx = weightX;
        this.panel.add(component, constraints);
    }

    private void ensureNotBuilt() {
        if (this.built) {
            throw new IllegalStateException("Cannot add rows after build() has been called.");
        }
    }
}
