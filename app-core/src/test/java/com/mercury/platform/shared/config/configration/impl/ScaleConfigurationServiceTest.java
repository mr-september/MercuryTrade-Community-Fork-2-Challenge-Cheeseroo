package com.mercury.platform.shared.config.configration.impl;

import com.mercury.platform.shared.config.descriptor.ProfileDescriptor;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScaleConfigurationServiceTest {

    @Test
    void shouldBackfillMissingDefaultsDuringValidate() {
        ProfileDescriptor profileDescriptor = new ProfileDescriptor();
        Map<String, Float> scaleMap = new HashMap<>();
        scaleMap.put("other", 1.7f);
        profileDescriptor.setScaleDataMap(scaleMap);

        ScaleConfigurationService service = new ScaleConfigurationService(profileDescriptor);
        service.validate();

        Map<String, Float> validated = service.getMap();
        assertEquals(1.7f, validated.get("other"), 0.0001f);
        assertTrue(validated.containsKey("notification"));
        assertTrue(validated.containsKey("taskbar"));
        assertTrue(validated.containsKey("itemcell"));
    }

    @Test
    void shouldFallbackUnknownScaleKeysToOneAndPersistInMap() {
        ProfileDescriptor profileDescriptor = new ProfileDescriptor();
        profileDescriptor.setScaleDataMap(new HashMap<>());

        ScaleConfigurationService service = new ScaleConfigurationService(profileDescriptor);

        Float scale = service.get("brand_new_key");

        assertEquals(1.0f, scale, 0.0001f);
        assertEquals(1.0f, service.getMap().get("brand_new_key"), 0.0001f);
    }

    @Test
    void shouldDefensivelyCopyIncomingMapOnSet() {
        ProfileDescriptor profileDescriptor = new ProfileDescriptor();
        ScaleConfigurationService service = new ScaleConfigurationService(profileDescriptor);

        Map<String, Float> incoming = new HashMap<>();
        incoming.put("other", 2.1f);
        service.set(incoming);

        incoming.put("other", 4.9f);

        assertEquals(2.1f, service.get("other"), 0.0001f);
    }

    @Test
    void shouldReturnIndependentDefaultMaps() {
        ProfileDescriptor profileDescriptor = new ProfileDescriptor();
        ScaleConfigurationService service = new ScaleConfigurationService(profileDescriptor);

        Map<String, Float> defaultsA = service.getDefault();
        Map<String, Float> defaultsB = service.getDefault();

        assertNotSame(defaultsA, defaultsB);
        defaultsA.put("other", 4.0f);
        assertEquals(1.0f, defaultsB.get("other"), 0.0001f);
    }
}
