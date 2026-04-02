package com.mercury.platform.shared.config.configration.impl;

import com.mercury.platform.shared.config.configration.BaseConfigurationService;
import com.mercury.platform.shared.config.configration.FramesConfigurationService;
import com.mercury.platform.shared.config.descriptor.FrameDescriptor;
import com.mercury.platform.shared.config.descriptor.ProfileDescriptor;
import com.mercury.platform.shared.store.MercuryStoreCore;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;


public class FramesConfigurationServiceImpl extends BaseConfigurationService<Map<String, FrameDescriptor>> implements FramesConfigurationService {
    private Map<String, Dimension> minimumFrameSize = new HashMap<>();
    private Map<String, FrameDescriptor> defaultFramesSettings = new HashMap<>();

    public FramesConfigurationServiceImpl(ProfileDescriptor selectedProfile) {
        super(selectedProfile);
        this.initMinimumMap();
        this.initDefaultMap();
    }

    @Override
    public void validate() {
        if (this.selectedProfile.getFrameDescriptorMap() == null) {
            this.selectedProfile.setFrameDescriptorMap(this.getDefault());
        } else {
            Map<String, FrameDescriptor> map = this.selectedProfile.getFrameDescriptorMap();
            this.getDefault().forEach((k, v) -> {
                if (!map.containsKey(k)) {
                    map.put(k, new FrameDescriptor(new Point(v.getFrameLocation()), new Dimension(v.getFrameSize())));
                }
            });
        }
    }

    @Override
    public FrameDescriptor get(String key) {
        Map<String, FrameDescriptor> map = this.selectedProfile.getFrameDescriptorMap();
        if (!map.containsKey(key) || map.get(key) == null) {
            FrameDescriptor defaultValue = this.getDefault().get(key);
            if (defaultValue == null) {
                defaultValue = new FrameDescriptor(new Point(0, 0), new Dimension(600, 400));
            }
            map.put(key, new FrameDescriptor(new Point(defaultValue.getFrameLocation()), new Dimension(defaultValue.getFrameSize())));
            MercuryStoreCore.saveConfigSubject.onNext(true);
        }
        return map.get(key);
    }

    @Override
    public Map<String, FrameDescriptor> getMap() {
        return this.copyMap(this.selectedProfile.getFrameDescriptorMap());
    }

    @Override
    public void set(Map<String, FrameDescriptor> map) {
        this.selectedProfile.setFrameDescriptorMap(this.copyMap(map));
    }

    @Override
    public Map<String, FrameDescriptor> getDefault() {
        return this.copyMap(this.defaultFramesSettings);
    }

    private Map<String, FrameDescriptor> copyMap(Map<String, FrameDescriptor> map) {
        Map<String, FrameDescriptor> copy = new HashMap<>();
        if (map != null) {
            map.forEach((k, v) -> {
                copy.put(k, new FrameDescriptor(new Point(v.getFrameLocation()), new Dimension(v.getFrameSize())));
            });
        }
        return copy;
    }

    @Override
    public void toDefault() {
        this.selectedProfile.setFrameDescriptorMap(this.getDefault());
    }

    @Override
    public Dimension getMinimumSize(String frameClass) {
        return this.minimumFrameSize.get(frameClass);
    }

    private void initMinimumMap() {
        minimumFrameSize.put("TaskBarFrame", new Dimension(109, 20));
        minimumFrameSize.put("NotificationFrame", new Dimension(320, 10));
        minimumFrameSize.put("OutMessageFrame", new Dimension(280, 115));
        minimumFrameSize.put("TestCasesFrame", new Dimension(400, 100));
        minimumFrameSize.put("SettingsFrame", new Dimension(600, 400));
        minimumFrameSize.put("HistoryFrame", new Dimension(280, 400));
        minimumFrameSize.put("TimerFrame", new Dimension(240, 102));
        minimumFrameSize.put("ChatScannerFrame", new Dimension(450, 100));
        minimumFrameSize.put("ItemsGridFrame", new Dimension(150, 150));
        minimumFrameSize.put("NotesFrame", new Dimension(540, 100));
        minimumFrameSize.put("ChatFilterSettingsFrame", new Dimension(300, 200));
        minimumFrameSize.put("GamePathChooser", new Dimension(600, 30));
        minimumFrameSize.put("CurrencySearchFrame", new Dimension(400, 300));
        minimumFrameSize.put("AdrManagerFrame", new Dimension(500, 300));
        minimumFrameSize.put("AdrCellSettingsFrame", new Dimension(300, 210));
    }

    private void initDefaultMap() {
        defaultFramesSettings.put("TaskBarFrame", new FrameDescriptor(new Point(400, 500), new Dimension(109, 20)));
        defaultFramesSettings.put("NotificationFrame", new FrameDescriptor(new Point(700, 600), new Dimension(450, 0)));
        defaultFramesSettings.put("OutMessageFrame", new FrameDescriptor(new Point(200, 500), new Dimension(280, 115)));
        defaultFramesSettings.put("TestCasesFrame", new FrameDescriptor(new Point(1400, 500), new Dimension(400, 100)));
        defaultFramesSettings.put("SettingsFrame", new FrameDescriptor(new Point(600, 600), new Dimension(800, 600)));
        defaultFramesSettings.put("HistoryFrame", new FrameDescriptor(new Point(600, 500), new Dimension(280, 400)));
        defaultFramesSettings.put("TimerFrame", new FrameDescriptor(new Point(400, 600), new Dimension(240, 102)));
        defaultFramesSettings.put("ChatScannerFrame", new FrameDescriptor(new Point(400, 600), new Dimension(550, 220)));
        defaultFramesSettings.put("ItemsGridFrame", new FrameDescriptor(new Point(12, 79), new Dimension(641, 718)));
        defaultFramesSettings.put("NotesFrame", new FrameDescriptor(new Point(400, 600), new Dimension(540, 100)));
        defaultFramesSettings.put("ChatFilterSettingsFrame", new FrameDescriptor(new Point(400, 600), new Dimension(320, 200)));
        defaultFramesSettings.put("GamePathChooser", new FrameDescriptor(new Point(400, 600), new Dimension(520, 30)));
        defaultFramesSettings.put("CurrencySearchFrame", new FrameDescriptor(new Point(400, 600), new Dimension(400, 300)));
        defaultFramesSettings.put("AdrManagerFrame", new FrameDescriptor(new Point(500, 300), new Dimension(700, 500)));
        defaultFramesSettings.put("AdrCellSettingsFrame", new FrameDescriptor(new Point(400, 600), new Dimension(300, 210)));
    }
}
