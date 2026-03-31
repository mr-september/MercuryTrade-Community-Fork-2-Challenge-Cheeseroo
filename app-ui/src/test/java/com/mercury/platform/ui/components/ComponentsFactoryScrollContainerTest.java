package com.mercury.platform.ui.components;

import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ComponentsFactoryScrollContainerTest {

    @Test
    void shouldClampHorizontalViewportPositionToZero() {
        ComponentsFactory factory = ComponentsFactory.INSTANCE.copy();
        JPanel content = new JPanel();
        content.setPreferredSize(new Dimension(1200, 800));

        JScrollPane scrollPane = factory.getVerticalContainer(content);
        JViewport viewport = scrollPane.getViewport();
        viewport.setViewSize(content.getPreferredSize());
        viewport.setExtentSize(new Dimension(320, 240));

        viewport.setViewPosition(new Point(120, 80));

        Point viewPosition = viewport.getViewPosition();
        assertEquals(0, viewPosition.x);
        assertEquals(80, viewPosition.y);
    }
}
