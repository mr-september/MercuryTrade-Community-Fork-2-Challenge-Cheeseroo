package com.mercury.platform.shared.config.configration.impl;

import com.mercury.platform.shared.config.descriptor.FrameDescriptor;
import com.mercury.platform.shared.config.descriptor.ProfileDescriptor;
import org.junit.jupiter.api.Test;

import java.awt.Dimension;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FramesConfigurationServiceImplTest {

    @Test
    void shouldProvideIndependentDefaultMapInstances() {
        ProfileDescriptor profileDescriptor = new ProfileDescriptor();
        FramesConfigurationServiceImpl service = new FramesConfigurationServiceImpl(profileDescriptor);

        Map<String, FrameDescriptor> firstDefaults = service.getDefault();
        Map<String, FrameDescriptor> secondDefaults = service.getDefault();

        assertNotSame(firstDefaults, secondDefaults);
        assertNotSame(firstDefaults.get("SettingsFrame"), secondDefaults.get("SettingsFrame"));
    }

    @Test
    void shouldNotMutateDefaultsWhenProfileEntriesChange() {
        ProfileDescriptor profileDescriptor = new ProfileDescriptor();
        FramesConfigurationServiceImpl service = new FramesConfigurationServiceImpl(profileDescriptor);
        service.validate();

        service.getMap().get("SettingsFrame").setFrameSize(new Dimension(333, 222));

        FrameDescriptor defaults = service.getDefault().get("SettingsFrame");
        assertEquals(new Dimension(800, 600), defaults.getFrameSize());
    }

    @Test
    void shouldCreateSafeFallbackForUnknownFrameKey() {
        ProfileDescriptor profileDescriptor = new ProfileDescriptor();
        FramesConfigurationServiceImpl service = new FramesConfigurationServiceImpl(profileDescriptor);
        service.validate();

        FrameDescriptor descriptor = service.get("UnknownFrame");

        assertNotNull(descriptor);
        assertNotNull(descriptor.getFrameLocation());
        assertNotNull(descriptor.getFrameSize());
        assertTrue(descriptor.getFrameSize().width >= 1);
        assertTrue(descriptor.getFrameSize().height >= 1);
        assertEquals(new Point(0, 0), descriptor.getFrameLocation());
    }

    @Test
    void shouldDefensivelyCopyIncomingMapOnSet() {
        ProfileDescriptor profileDescriptor = new ProfileDescriptor();
        FramesConfigurationServiceImpl service = new FramesConfigurationServiceImpl(profileDescriptor);

        Map<String, FrameDescriptor> incomingMap = new HashMap<>();
        incomingMap.put("SettingsFrame", new FrameDescriptor(new Point(10, 20), new Dimension(700, 500)));

        service.set(incomingMap);
        incomingMap.get("SettingsFrame").setFrameSize(new Dimension(1, 1));

        FrameDescriptor storedDescriptor = service.get("SettingsFrame");
        assertEquals(new Dimension(700, 500), storedDescriptor.getFrameSize());
    }
}
