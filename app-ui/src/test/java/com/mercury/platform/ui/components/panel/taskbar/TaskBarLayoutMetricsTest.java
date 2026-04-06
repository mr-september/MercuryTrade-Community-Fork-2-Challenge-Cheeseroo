package com.mercury.platform.ui.components.panel.taskbar;

import com.mercury.platform.ui.components.ComponentsFactory;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskBarLayoutMetricsTest {

    @Test
    void shouldMeasureCollapsedAndExpandedStripWidthsFromActualComponents() {
        ComponentsFactory componentsFactory = ComponentsFactory.INSTANCE.copy();
        componentsFactory.setScale(1.0f);
        TaskBarLayoutMetrics metrics = new TaskBarLayoutMetrics(componentsFactory);
        JPanel panel = new JPanel();

        panel.add(sizedButton(40, 20));
        panel.add(sizedButton(42, 20));
        panel.add(sizedButton(44, 20));
        panel.add(sizedButton(46, 20));
        panel.add(sizedButton(48, 20));

        assertEquals(4, metrics.getCollapsedButtonCount());
        assertEquals(40 + 42 + 44 + 46, metrics.calculateCollapsedWidth(panel));
        assertEquals(40 + 42 + 44 + 46 + 48, metrics.calculateVisibleWidth(panel, 5));
        assertEquals(new Dimension(40 + 42 + 44 + 46 + 48, 20), metrics.calculateStripSize(panel));
    }

    private JButton sizedButton(int width, int height) {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(width, height));
        return button;
    }
}
