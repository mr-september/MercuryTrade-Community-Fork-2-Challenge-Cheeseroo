package com.mercury.platform.ui.components.panel.grid;

import com.mercury.platform.shared.config.descriptor.StashTabDescriptor;
import com.mercury.platform.shared.entity.message.ItemTradeNotificationDescriptor;
import com.mercury.platform.ui.components.ComponentsFactory;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ItemGridLayoutMetricsTest {

    @Test
    void shouldScaleGridLayoutMetricsAndToggleControls() {
        ComponentsFactory factory = ComponentsFactory.INSTANCE.copy();
        factory.setScale(2f);
        ItemGridLayoutMetrics metrics = new ItemGridLayoutMetrics(factory);

        assertEquals(new Dimension(36, 1336), metrics.getLiveRightSpacerSize());
        assertEquals(new Dimension(1322, 32), metrics.getLiveBottomSpacerSize());
        assertEquals(new Insets(-20, 0, 0, 0), metrics.getLiveBottomSpacerInsets());
        assertEquals(new Insets(40, 0, 52, 0), metrics.getHeaderTabsInsets());

        ItemInfoPanel itemInfoPanel = new ItemInfoPanel(newMessage(), new ItemCell(1, 1, new JPanel()), new StashTabDescriptor("Example", false, true), factory);
        JPanel nicknamePanel = (JPanel) itemInfoPanel.getComponent(0);
        JPanel itemTabInfoPanel = (JPanel) itemInfoPanel.getComponent(2);

        assertEquals(metrics.getNicknameInsets(), nicknamePanel.getInsets());
        assertEquals(metrics.getTabInfoInsets(), itemTabInfoPanel.getInsets());

        JCheckBox undefinedTabToggle = findCheckBox(itemInfoPanel);
        assertNotNull(undefinedTabToggle);
        assertEquals(metrics.getQuadToggleSize(), undefinedTabToggle.getPreferredSize());
    }

    @Test
    void shouldScaleAuxiliaryGridMetricsFromLogicalDimensions() {
        ComponentsFactory factory = ComponentsFactory.INSTANCE.copy();
        factory.setScale(1.5f);
        ItemGridLayoutMetrics metrics = new ItemGridLayoutMetrics(factory);

        assertEquals(105, metrics.getPinTabTypeWidth());
        assertEquals(new Dimension(75, 84), metrics.getSavedTabsPanelSize());
        assertEquals(new Dimension(Integer.MAX_VALUE, 24), metrics.getHorizontalScrollBarSize());
        assertEquals(new Insets(3, 3, 3, 3), metrics.getHorizontalScrollBarInsets());
    }

    private ItemTradeNotificationDescriptor newMessage() {
        ItemTradeNotificationDescriptor message = new ItemTradeNotificationDescriptor();
        message.setWhisperNickname("Example");
        message.setTabName("Example");
        return message;
    }

    private JCheckBox findCheckBox(Container container) {
        for (Component component : container.getComponents()) {
            if (component instanceof JCheckBox) {
                return (JCheckBox) component;
            }
            if (component instanceof Container) {
                JCheckBox nestedCheckBox = findCheckBox((Container) component);
                if (nestedCheckBox != null) {
                    return nestedCheckBox;
                }
            }
        }
        return null;
    }
}
