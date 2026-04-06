package com.mercury.platform.ui.components.panel.notification;

import com.mercury.platform.shared.entity.message.FlowDirections;
import com.mercury.platform.shared.entity.message.TradeNotificationDescriptor;
import com.mercury.platform.ui.components.ComponentsFactory;
import com.mercury.platform.ui.components.panel.notification.controller.NotificationController;
import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NotificationLayoutMetricsTest {

    @Test
    void shouldScaleNotificationLayoutMetricsFromLogicalValues() {
        ComponentsFactory factory = ComponentsFactory.INSTANCE.copy();
        factory.setScale(1.5f);
        NotificationLayoutMetrics metrics = new NotificationLayoutMetrics(factory);

        assertEquals(new Dimension(15, expectedUpwardsBufferHeight()), metrics.getBufferSize(FlowDirections.UPWARDS));
        assertEquals(new Dimension(600, 138), metrics.getPinPreviewSize());
        assertEquals(new Dimension(15, 90), metrics.getScannerContentSize());
        assertEquals(new Dimension(75, 39), metrics.getTimePanelSize());
        assertEquals(new Dimension(120, 30), metrics.getNicknameCollapsedSize());
        assertEquals(8, metrics.getResponseButtonsHorizontalGap());
        assertEquals(3, metrics.getResponseButtonsVerticalGap());
        assertEquals(6, metrics.getNotificationMiscPanelGap());
        assertEquals(6, metrics.getIncomingInteractionGap());
        assertEquals(5, metrics.getOutgoingInteractionGap());
        assertEquals(5, metrics.getScannerInteractionGap());
        assertEquals(6, metrics.getCurrencyRateGap());
        assertEquals(23, metrics.getAdditionalItemRowHeight());
    }

    @Test
    void shouldWireCollapsedNicknameSizingThroughSharedMetrics() {
        ComponentsFactory factory = ComponentsFactory.INSTANCE.copy();
        factory.setScale(2f);
        TestTradeNotificationPanel panel = new TestTradeNotificationPanel();
        panel.setComponentsFactory(factory);

        JPanel nicknamePanel = panel.exposeNicknamePanel(new JLabel("Example"));

        assertEquals(new NotificationLayoutMetrics(factory).getNicknameCollapsedSize(), nicknamePanel.getPreferredSize());
    }

    private int expectedUpwardsBufferHeight() {
        return SystemUtils.IS_OS_WINDOWS ? 1500 : 0;
    }

    private static class TestTradeNotificationPanel extends TradeNotificationPanel<TradeNotificationDescriptor, NotificationController> {
        @Override
        protected JPanel getHeader() {
            return new JPanel();
        }

        @Override
        protected JPanel getMessagePanel() {
            return new JPanel();
        }

        @Override
        protected void updateHotKeyPool() {
            /* NOP */
        }

        private JPanel exposeNicknamePanel(JLabel nicknameLabel) {
            return this.getNicknamePanel(nicknameLabel);
        }
    }
}
