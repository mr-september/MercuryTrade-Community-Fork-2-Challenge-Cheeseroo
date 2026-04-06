package com.mercury.platform.ui.components.panel.settings;

import com.mercury.platform.ui.components.ComponentsFactory;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SettingsLayoutMetricsTest {

    @Test
    void shouldScaleSharedSettingsSizesFromLogicalValues() {
        ComponentsFactory factory = ComponentsFactory.INSTANCE.copy();
        factory.setScale(2f);
        SettingsLayoutMetrics metrics = new SettingsLayoutMetrics(factory);

        assertEquals(new Dimension(2000, 1200), metrics.getFrameSize());
        assertEquals(new Dimension(200, 52), metrics.getSetupDialogButtonSize());
        assertEquals(new Dimension(220, 52), metrics.getFooterActionButtonSize());
        assertEquals(new Dimension(420, 70), metrics.getOperationButtonSize());
        assertEquals(new Dimension(440, 40), metrics.getMenuPanelSize());
        assertEquals(new Dimension(440, 100), metrics.getMenuEntrySize());
        assertEquals(new Dimension(240, 52), metrics.getResponseTitleSize());
        assertEquals(new Dimension(260, 40), metrics.getResponseHotKeyHeaderSize());
        assertEquals(new Dimension(220, 52), metrics.getHotKeyPanelSize());
        assertEquals(new Dimension(600, 900), metrics.getSupportTextSize());
        assertEquals(new Dimension(28, Integer.MAX_VALUE), metrics.getSupportScrollbarSize());
        assertEquals(new Dimension(60, 52), metrics.getNotificationLimitSize());
        assertEquals(new Dimension(200, 40), metrics.getDonationTrackerSize());
        assertEquals(8, metrics.scaleValue(4));
    }
}
