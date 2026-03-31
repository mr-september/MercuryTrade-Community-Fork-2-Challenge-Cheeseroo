package com.mercury.platform.ui.components.panel.settings;

import com.mercury.platform.ui.components.ComponentsFactory;
import com.mercury.platform.ui.misc.MercuryStoreUI;
import com.mercury.platform.shared.config.Configuration;
import com.mercury.platform.shared.config.MercuryConfigManager;
import com.mercury.platform.shared.config.MercuryConfigurationSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rx.Subscription;

import javax.swing.*;
import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AutoScalingSettingsPanelTest {

    @BeforeEach
    void setUp() {
        MercuryConfigManager configuration = new MercuryConfigManager(new MercuryConfigurationSource());
        try {
            configuration.load();
        } catch (Exception ignored) {
        }
        Configuration.set(configuration);
    }

    @Test
    void shouldKeepOtherSliderMaximumAtLeastTwoHundredPercent() throws Exception {
        AutoScalingSettingsPanel panel = new AutoScalingSettingsPanel(ComponentsFactory.INSTANCE.copy());

        JSlider otherSlider = getSlider(panel, "otherSlider");

        assertTrue(otherSlider.getMaximum() >= 20);
    }

    @Test
    void shouldNotApplyScaleWhileDraggingOtherSlider() throws Exception {
        AutoScalingSettingsPanel panel = new AutoScalingSettingsPanel(ComponentsFactory.INSTANCE.copy());
        JSlider otherSlider = getSlider(panel, "otherSlider");

        int initialSliderValue = otherSlider.getValue();
        int candidateValue = initialSliderValue < otherSlider.getMaximum()
                ? initialSliderValue + 1
                : initialSliderValue - 1;

        float initialOtherScale = panel.getScaleValue("other");

        AtomicInteger scaleSaveEvents = new AtomicInteger(0);
        Subscription subscription = MercuryStoreUI.saveScaleSubject.subscribe(map -> scaleSaveEvents.incrementAndGet());
        try {
            otherSlider.setValue(candidateValue);

            assertEquals(0, scaleSaveEvents.get());
            assertNotEquals(initialOtherScale, panel.getScaleValue("other"));
        } finally {
            subscription.unsubscribe();
        }
    }

    private JSlider getSlider(AutoScalingSettingsPanel panel, String fieldName) throws Exception {
        Field sliderField = AutoScalingSettingsPanel.class.getDeclaredField(fieldName);
        sliderField.setAccessible(true);
        return (JSlider) sliderField.get(panel);
    }
}
