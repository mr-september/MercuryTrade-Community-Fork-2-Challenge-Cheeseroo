package com.mercury.platform.ui.components.panel.settings;

import com.mercury.platform.TranslationKey;
import com.mercury.platform.shared.IconConst;
import com.mercury.platform.ui.components.ComponentsFactory;
import com.mercury.platform.ui.components.fields.font.FontStyle;
import com.mercury.platform.ui.components.panel.misc.ViewInit;
import com.mercury.platform.ui.manager.routing.SettingsPage;
import com.mercury.platform.ui.misc.AppThemeColor;
import com.mercury.platform.ui.misc.MercuryStoreUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MenuPanel extends JPanel implements ViewInit {
    private final ComponentsFactory componentsFactory;
    private final SettingsLayoutMetrics layoutMetrics;
    private final SettingsPage selectedPage;

    public MenuPanel() {
        this(ComponentsFactory.INSTANCE, SettingsPage.GENERAL_SETTINGS);
    }

    public MenuPanel(ComponentsFactory componentsFactory, SettingsPage selectedPage) {
        super(new BorderLayout());
        this.componentsFactory = componentsFactory;
        this.layoutMetrics = new SettingsLayoutMetrics(this.componentsFactory);
        this.selectedPage = selectedPage;
        this.setBackground(AppThemeColor.FRAME);
        this.setPreferredSize(this.layoutMetrics.getMenuPanelSize());
        this.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, AppThemeColor.ADR_PANEL_BORDER));
        this.onViewInit();
    }

    @Override
    public void onViewInit() {
        JList<MenuEntry> list = new JList<>(getEntries());
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                list.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                list.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        list.setCellRenderer(new MenuListRenderer(this.componentsFactory));
        list.setBackground(AppThemeColor.FRAME);
        list.setSelectedIndex(getSelectedIndex());
        list.addListSelectionListener(e ->
                list.getSelectedValue().getAction().onClick());

        JLabel appIcon = this.componentsFactory.getTextLabel("MercuryChat", FontStyle.BOLD, 22);
        appIcon.setIcon(this.componentsFactory.getIcon(IconConst.APP_ICON, 50));
        appIcon.setBorder(BorderFactory.createEmptyBorder(
                this.layoutMetrics.scaleValue(15),
                this.layoutMetrics.scaleValue(15),
                this.layoutMetrics.scaleValue(15),
                this.layoutMetrics.scaleValue(15)));
        appIcon.setBackground(AppThemeColor.FRAME);
        this.add(appIcon, BorderLayout.PAGE_START);
        this.add(list, BorderLayout.CENTER);
    }

    private int getSelectedIndex() {
        switch (this.selectedPage) {
            case SOUND_SETTING:
                return 1;
            case NOTIFICATION_SETTINGS:
                return 2;
            case TASK_BAR_SETTINGS:
                return 3;
            case SUPPORT:
                return 4;
            case ABOUT:
                return 5;
            case GENERAL_SETTINGS:
            default:
                return 0;
        }
    }

    @SuppressWarnings("all")
    private MenuEntry[] getEntries() {
        return new MenuEntry[]{
                new MenuEntry(TranslationKey.general.value(), () -> {
                    MercuryStoreUI.settingsStateSubject.onNext(SettingsPage.GENERAL_SETTINGS);
                }, this.componentsFactory.getIcon("app/general_settings.png", 22)),
                new MenuEntry(TranslationKey.sound.value(), () -> {
                    MercuryStoreUI.settingsStateSubject.onNext(SettingsPage.SOUND_SETTING);
                }, this.componentsFactory.getIcon("app/sound_settings.png", 22)),
                new MenuEntry(TranslationKey.notification_panel.value(), () -> {
                    MercuryStoreUI.settingsStateSubject.onNext(SettingsPage.NOTIFICATION_SETTINGS);
                }, this.componentsFactory.getIcon("app/notification_panel_settings.png", 22)),
                new MenuEntry(TranslationKey.task_bar.value(), () -> {
                    MercuryStoreUI.settingsStateSubject.onNext(SettingsPage.TASK_BAR_SETTINGS);
                }, this.componentsFactory.getIcon("app/task_bar_settings.png", 22)),
                new MenuEntry(TranslationKey.support.value(), () -> {
                    MercuryStoreUI.settingsStateSubject.onNext(SettingsPage.SUPPORT);
                }, this.componentsFactory.getIcon("app/support_settings.png", 22)),
                new MenuEntry(TranslationKey.about.value(), () -> {
                    MercuryStoreUI.settingsStateSubject.onNext(SettingsPage.ABOUT);
                }, this.componentsFactory.getIcon("app/app-icon_sepia.png", 22)),
        };
    }
}
