package com.mercury.platform.ui.manager.routing;

import com.mercury.platform.shared.AsSubscriber;
import com.mercury.platform.shared.config.Configuration;
import com.mercury.platform.shared.store.MercuryStoreCore;
import com.mercury.platform.ui.components.panel.settings.page.*;
import com.mercury.platform.ui.frame.titled.SettingsFrame;
import com.mercury.platform.ui.misc.MercuryStoreUI;
import com.mercury.platform.ui.misc.SwingUiExecutor;

import java.util.EnumMap;
import java.util.Map;

public class SettingsRoutManager implements AsSubscriber {
    private final Map<SettingsPage, SettingsPagePanel> pages = new EnumMap<>(SettingsPage.class);
    private final SettingsFrame settingsFrame;
    private SettingsPage currentPage = SettingsPage.GENERAL_SETTINGS;

    public SettingsRoutManager(SettingsFrame settingsFrame) {
        this.settingsFrame = settingsFrame;

        if (Configuration.get().applicationConfiguration().get().isShowOnStartUp()) {
            Configuration.get().applicationConfiguration().get().setShowOnStartUp(false);
        }

        this.pages.put(SettingsPage.GENERAL_SETTINGS, new GeneralSettingsPagePanel());
        this.pages.put(SettingsPage.SOUND_SETTING, new SoundSettingsPagePanel());
        this.pages.put(SettingsPage.NOTIFICATION_SETTINGS, new NotificationSettingsPagePanel());
        this.pages.put(SettingsPage.TASK_BAR_SETTINGS, new TaskBarSettingsPagePanel());
        this.pages.put(SettingsPage.SUPPORT, new SupportPagePanel());
        this.pages.put(SettingsPage.ABOUT, new AboutPagePanel());

        this.showPage(SettingsPage.GENERAL_SETTINGS);
        this.subscribe();
    }

    private void showPage(SettingsPage page) {
        this.currentPage = page;
        this.settingsFrame.setSelectedPage(page);
        this.settingsFrame.setContentPanel(this.getPage(page));
    }

    private SettingsPagePanel getPage(SettingsPage page) {
        return this.pages.getOrDefault(page, this.pages.get(SettingsPage.GENERAL_SETTINGS));
    }

    private void refreshAllPages() {
        this.pages.values().forEach(SettingsPagePanel::restore);
    }

    private void saveAllPages() {
        this.pages.values().forEach(SettingsPagePanel::onSave);
    }

    private void refreshFrameChrome() {
        this.settingsFrame.setSelectedPage(this.currentPage);
        this.settingsFrame.refreshLayout();
        this.settingsFrame.setContentPanel(this.getPage(this.currentPage));
    }

    private void persistSettingsAsync() {
        Thread saveThread = new Thread(() -> MercuryStoreCore.saveConfigSubject.onNext(true), "settings-save-config");
        saveThread.setDaemon(true);
        saveThread.start();
    }

    @Override
    public void subscribe() {
        MercuryStoreUI.settingsStateSubject.subscribe(SwingUiExecutor.onEdt(this::showPage));
        MercuryStoreUI.settingsRestoreSubject.subscribe(SwingUiExecutor.onEdt(state -> {
            this.refreshAllPages();
            this.refreshFrameChrome();
        }));
        MercuryStoreUI.settingsSaveSubject.subscribe(SwingUiExecutor.onEdt(state -> {
            this.saveAllPages();
            this.refreshAllPages();
            this.refreshFrameChrome();
            MercuryStoreUI.saveScaleSubject.onNext(Configuration.get().scaleConfiguration().getMap());
            this.persistSettingsAsync();
            MercuryStoreUI.settingsPostSubject.onNext(true);
        }));
    }
}
