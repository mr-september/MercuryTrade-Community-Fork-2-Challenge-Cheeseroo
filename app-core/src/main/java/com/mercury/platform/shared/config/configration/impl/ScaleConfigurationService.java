package com.mercury.platform.shared.config.configration.impl;

import com.mercury.platform.shared.config.configration.BaseConfigurationService;
import com.mercury.platform.shared.config.configration.KeyValueConfigurationService;
import com.mercury.platform.shared.config.descriptor.ProfileDescriptor;
import com.mercury.platform.shared.store.MercuryStoreCore;

import java.util.HashMap;
import java.util.Map;


public class ScaleConfigurationService extends BaseConfigurationService<Map<String, Float>> implements KeyValueConfigurationService<String, Float> {
    public ScaleConfigurationService(ProfileDescriptor selectedProfile) {
        super(selectedProfile);
    }

    @Override
    public void validate() {
        if (this.selectedProfile.getScaleDataMap() == null) {
            this.selectedProfile.setScaleDataMap(this.getDefault());
        }
    }

    @Override
    public Float get(String key) {
        return this.selectedProfile.getScaleDataMap().computeIfAbsent(key, k -> {
            this.selectedProfile.getScaleDataMap().put(key, this.getDefault().get(key));
            MercuryStoreCore.saveConfigSubject.onNext(true);
            return this.getDefault().get(key);
        });
    }

    @Override
    public Map<String, Float> getDefault() {
        Map<String, Float> scaleData = new HashMap<>();
        
        // Re-evaluated default scaling values based on typical usage patterns
        // These values provide better balance for different UI components
        scaleData.put("notification", 1.0f);    // Notifications: baseline scale
        scaleData.put("taskbar", 1.0f);         // Taskbar: baseline scale  
        scaleData.put("itemcell", 1.0f);        // Item cells: baseline scale
        scaleData.put("other", 1.0f);           // Other UI elements: baseline scale
        
        // Auto-scaling configuration
        scaleData.put("auto_scale_enabled", 0f);      // 0f = disabled, 1f = enabled
        scaleData.put("auto_scale_detection_mode", 1f); // 1f = basic, 2f = advanced, 3f = per-monitor
        scaleData.put("auto_scale_minimum", 0.5f);     // Minimum allowed auto-scale (50%)
        scaleData.put("auto_scale_maximum", 5.0f);     // Maximum allowed auto-scale (500%)
        
        // Component-specific auto-scaling adjustments (multipliers applied to base auto-scale)
        scaleData.put("auto_notification_factor", 0.9f);  // Notifications 10% smaller than base
        scaleData.put("auto_taskbar_factor", 1.0f);       // Taskbar matches base
        scaleData.put("auto_itemcell_factor", 0.95f);     // Item cells 5% smaller for density
        scaleData.put("auto_other_factor", 1.0f);         // Other elements match base
        
        return scaleData;
    }

    @Override
    public void toDefault() {
        this.selectedProfile.setScaleDataMap(this.getDefault());
    }


    @Override
    public Map<String, Float> getMap() {
        return this.selectedProfile.getScaleDataMap();
    }

    @Override
    public void set(Map<String, Float> map) {
        this.selectedProfile.setScaleDataMap(map);
    }
}
