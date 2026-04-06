package com.mercury.platform.ui.components.panel.taskbar;

import com.mercury.platform.ui.components.ComponentsFactory;
import lombok.NonNull;

import javax.swing.*;
import java.awt.*;

public class TaskBarLayoutMetrics {
    private static final int COLLAPSED_BUTTON_COUNT = 4;
    private static final int MAJOR_GAP_WIDTH = 3;
    private static final int MINOR_GAP_WIDTH = 2;
    private static final int GAP_HEIGHT = 4;

    private final ComponentsFactory componentsFactory;

    public TaskBarLayoutMetrics(@NonNull ComponentsFactory componentsFactory) {
        this.componentsFactory = componentsFactory;
    }

    public int getCollapsedButtonCount() {
        return COLLAPSED_BUTTON_COUNT;
    }

    public int scaleValue(int logicalValue) {
        return Math.round(this.componentsFactory.getScale() * logicalValue);
    }

    public Dimension getMajorGapSize() {
        return new Dimension(scaleValue(MAJOR_GAP_WIDTH), scaleValue(GAP_HEIGHT));
    }

    public Dimension getMinorGapSize() {
        return new Dimension(scaleValue(MINOR_GAP_WIDTH), scaleValue(GAP_HEIGHT));
    }

    public Component createMajorGap() {
        return Box.createRigidArea(getMajorGapSize());
    }

    public Component createMinorGap() {
        return Box.createRigidArea(getMinorGapSize());
    }

    public int calculateCollapsedWidth(Container container) {
        return calculateVisibleWidth(container, getCollapsedButtonCount());
    }

    public int calculateVisibleWidth(Container container, int visibleButtons) {
        int width = 0;
        int buttonsCounted = 0;

        for (Component component : container.getComponents()) {
            Dimension preferredSize = component.getPreferredSize();
            if (preferredSize == null) {
                continue;
            }
            width += preferredSize.width;
            if (component instanceof AbstractButton) {
                buttonsCounted++;
                if (buttonsCounted >= visibleButtons) {
                    break;
                }
            }
        }

        return width;
    }

    public Dimension calculateStripSize(Container container) {
        int width = 0;
        int height = 0;

        for (Component component : container.getComponents()) {
            Dimension preferredSize = component.getPreferredSize();
            if (preferredSize == null) {
                continue;
            }
            width += preferredSize.width;
            height = Math.max(height, preferredSize.height);
        }

        return new Dimension(width, height);
    }
}
