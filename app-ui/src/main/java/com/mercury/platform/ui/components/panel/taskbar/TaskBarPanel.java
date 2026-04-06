package com.mercury.platform.ui.components.panel.taskbar;

import com.mercury.platform.TranslationKey;
import com.mercury.platform.core.ProdStarter;
import com.mercury.platform.shared.FrameVisibleState;
import com.mercury.platform.shared.IconConst;
import com.mercury.platform.shared.config.Configuration;
import com.mercury.platform.shared.config.configration.PlainConfigurationService;
import com.mercury.platform.shared.config.descriptor.TaskBarDescriptor;
import com.mercury.platform.shared.store.MercuryStoreCore;
import com.mercury.platform.ui.components.ComponentsFactory;
import com.mercury.platform.ui.components.panel.misc.ViewInit;
import com.mercury.platform.ui.frame.movable.TaskBarFrame;
import com.mercury.platform.ui.manager.FramesManager;
import com.mercury.platform.ui.misc.AppThemeColor;
import com.mercury.platform.ui.misc.SwingUiExecutor;
import com.mercury.platform.ui.misc.ToggleAdapter;
import lombok.NonNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class TaskBarPanel extends JPanel implements ViewInit {
    private final ComponentsFactory componentsFactory;
    private final TaskBarLayoutMetrics layoutMetrics;
    private final TaskBarController controller;
    private final MouseListener taskBarFrameMouseListener;
    private PlainConfigurationService<TaskBarDescriptor> taskBarService;
    private JButton toHideout;
    private JButton showHelpIG;

    public TaskBarPanel(@NonNull TaskBarController controller, @NonNull ComponentsFactory factory, MouseListener taskBarFrameMouseListener) {
        this.controller = controller;
        this.componentsFactory = factory;
        this.layoutMetrics = new TaskBarLayoutMetrics(factory);
        this.taskBarFrameMouseListener = taskBarFrameMouseListener;
        this.onViewInit();

        MercuryStoreCore.hotKeySubject.subscribe(SwingUiExecutor.onEdt(hotkeyDescriptor -> {
            if (ProdStarter.APP_STATUS.equals(FrameVisibleState.SHOW)) {
                if (this.taskBarService.get().getHideoutHotkey().equals(hotkeyDescriptor)) {
                    this.toHideout.doClick();
                } else if (this.taskBarService.get().getHelpIGHotkey().equals(hotkeyDescriptor)) {
                    this.showHelpIG.doClick();
                }
            }
        }));
    }

    @Override
    public void onViewInit() {
        this.taskBarService = Configuration.get().taskBarConfiguration();

        this.setBackground(AppThemeColor.FRAME);
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        JButton messageNotificationsHide = componentsFactory.getIconButton(
                IconConst.MESSAGE_NOTIFICATION_ON,
                24,
                AppThemeColor.FRAME,
                TranslationKey.hide_notifications.value());
        ToggleAdapter toggleAdapter = createToggleAdapter(messageNotificationsHide);
        MercuryStoreCore.showMessageHideButton.subscribe(SwingUiExecutor.onEdt(a -> showHideMessageNotificationSubscribe(messageNotificationsHide, toggleAdapter)));
        messageNotificationsHide.addMouseListener(toggleAdapter);
        messageNotificationsHide.addMouseListener(taskBarFrameMouseListener);

        JButton visibleMode = componentsFactory.getIconButton(
                IconConst.VISIBLE_ALWAYS_MODE,
                24,
                AppThemeColor.FRAME,
                TranslationKey.do_not_disturb.value());
        componentsFactory.setUpToggleCallbacks(visibleMode,
                () -> {
                    visibleMode.setIcon(componentsFactory.getIcon(IconConst.VISIBLE_DND_MODE, 24));
                    controller.enableDND();
                },
                () -> {
                    visibleMode.setIcon(componentsFactory.getIcon(IconConst.VISIBLE_ALWAYS_MODE, 24));
                    controller.disableDND();
                },
                true
        );
        visibleMode.addMouseListener(taskBarFrameMouseListener);

        JButton pushbulletNotification = componentsFactory.getIconButton(
                taskBarService.get().isPushbulletOn() ? IconConst.PUSHBULLET_NOTIFICATION : IconConst.PUSHBULLET_NOTIFICATION_OFF,
                24,
                AppThemeColor.FRAME,
                TranslationKey.pushbullet_notification_active.value());
        componentsFactory.setUpToggleCallbacks(pushbulletNotification,
                                               () -> {
                                                   getPushbullet(taskBarService.get().isPushbulletOn(), pushbulletNotification);
                                               },
                                               () -> {
                                                   getPushbullet(taskBarService.get().isPushbulletOn(), pushbulletNotification);
                                               },
                                               true
                                              );
        pushbulletNotification.addMouseListener(taskBarFrameMouseListener);

        JButton itemGrid = componentsFactory.getIconButton(
                IconConst.ITEM_GRID_ENABLE,
                24,
                AppThemeColor.FRAME,
                TranslationKey.item_grid.value());
        itemGrid.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    controller.showITH();
                }
            }
        });
        itemGrid.addMouseListener(taskBarFrameMouseListener);

        this.toHideout = componentsFactory.getIconButton(
                IconConst.HIDEOUT,
                24,
                AppThemeColor.FRAME,
                TranslationKey.travel_hideout.value());
        this.toHideout.addActionListener(action -> {
            this.controller.performHideout();
        });
        this.toHideout.addMouseListener(taskBarFrameMouseListener);

        this.showHelpIG = componentsFactory.getIconButton(
                IconConst.HELP_IG,
                24,
                AppThemeColor.FRAME,
                TranslationKey.helpig.value());
        this.showHelpIG.addActionListener(action -> {
            this.controller.showHelpIG();
        });
        this.showHelpIG.addMouseListener(taskBarFrameMouseListener);

        JButton adr = componentsFactory.getIconButton(
                IconConst.OVERSEER,
                24,
                AppThemeColor.FRAME,
                TranslationKey.adr_settings.value());
        adr.addActionListener(action -> {
            FramesManager.INSTANCE.performAdr();
            TaskBarFrame windowAncestor = (TaskBarFrame) SwingUtilities.getWindowAncestor(TaskBarPanel.this);
            windowAncestor.collapseToMinimumWidth();
        });
        adr.addMouseListener(taskBarFrameMouseListener);

        JButton chatFilter = componentsFactory.getIconButton(
                IconConst.CHAT_FILTER,
                24,
                AppThemeColor.FRAME,
                TranslationKey.chat_filter.value());
        chatFilter.addActionListener(action -> {
            this.controller.showChatFiler();
        });
        chatFilter.addMouseListener(taskBarFrameMouseListener);

        JButton historyButton = componentsFactory.getIconButton(
                "app/history.png",
                20,
                AppThemeColor.FRAME,
                "");
        JButton joinChannelButton = componentsFactory.getIconButton(
                "app/join_channel.png",
                20,
                AppThemeColor.FRAME,
                TranslationKey.join_channel.value() + " " + this.taskBarService.get().getJoinChannelNumber());
        joinChannelButton.addActionListener(action -> {
            this.controller.performJoinChannel();
        });
        joinChannelButton.addMouseListener(taskBarFrameMouseListener);

        JButton pinButton = componentsFactory.getIconButton(
                IconConst.DRAG_AND_DROP,
                24,
                AppThemeColor.FRAME,
                TranslationKey.unlock_panel_tt.value());
        pinButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    controller.openPINSettings();
                }
            }
        });
        pinButton.addMouseListener(taskBarFrameMouseListener);

        JButton scaleButton = componentsFactory.getIconButton(
                IconConst.SCALE_SETTINGS,
                24,
                AppThemeColor.FRAME,
                TranslationKey.scale_settings_tt.value());
        scaleButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    controller.openScaleSettings();
                }
            }
        });
        scaleButton.addMouseListener(taskBarFrameMouseListener);

        JButton settingsButton = componentsFactory.getIconButton(
                IconConst.SETTINGS,
                26,
                AppThemeColor.FRAME,
                TranslationKey.settings.value());
        settingsButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    controller.showSettings();
                    TaskBarFrame windowAncestor = (TaskBarFrame) SwingUtilities.getWindowAncestor(TaskBarPanel.this);
                    windowAncestor.collapseToMinimumWidth();
                }
            }
        });
        settingsButton.addMouseListener(taskBarFrameMouseListener);

        JButton exitButton = componentsFactory.getIconButton(
                IconConst.EXIT,
                24,
                AppThemeColor.FRAME,
                TranslationKey.exit.value());
        exitButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    controller.exit();
                }
            }
        });
        exitButton.addMouseListener(taskBarFrameMouseListener);

        addWithGap(this.toHideout, true);
        addWithGap(adr, true);
        addWithGap(chatFilter, true);
        addWithGap(messageNotificationsHide, false);
        addWithGap(visibleMode, false);
        addWithGap(pushbulletNotification, false);
        addWithGap(joinChannelButton, false);
        addWithGap(this.showHelpIG, false);
        addWithGap(historyButton, true);
        addWithGap(itemGrid, true);
        addWithGap(pinButton, true);
        addWithGap(scaleButton, true);
        addWithGap(settingsButton, true);
        addWithGap(exitButton, true);
    }

    private void getPushbullet(boolean pushbulletEnabled, JButton pushbulletNotification) {
        if (!pushbulletEnabled) {
            pushbulletNotification.setIcon(componentsFactory.getIcon(IconConst.PUSHBULLET_NOTIFICATION, 24));
            controller.enablePushbullet();
        } else {
            pushbulletNotification.setIcon(componentsFactory.getIcon(IconConst.PUSHBULLET_NOTIFICATION_OFF, 24));
            controller.disablePushbullet();
        }
    }

    public int getCollapsedWidth() {
        return this.layoutMetrics.calculateCollapsedWidth(this);
    }

    public Dimension getStripSize() {
        return this.layoutMetrics.calculateStripSize(this);
    }

    private void showHideMessageNotificationSubscribe(JButton messageNotificationsHide, ToggleAdapter toggleAdapter) {
        toggleAdapter.setState(true);
        messageNotificationsHide.setIcon(componentsFactory.getIcon(IconConst.MESSAGE_NOTIFICATION_ON, 24));
    }

    private ToggleAdapter createToggleAdapter(JButton messageNotificationsHide) {
        return componentsFactory.createListenerForToggleCallbacks(messageNotificationsHide,
                () -> {
                    messageNotificationsHide.setIcon(componentsFactory.getIcon(IconConst.MESSAGE_NOTIFICATION_OFF, 24));
                    controller.hideMessageNotifications();
                },
                () -> {
                    messageNotificationsHide.setIcon(componentsFactory.getIcon(IconConst.MESSAGE_NOTIFICATION_ON, 24));
                    controller.showMessageNotifications();
                },
                true
        );
    }

    private void addWithGap(Component component, boolean useMajorGap) {
        this.add(component);
        this.add(useMajorGap ? this.layoutMetrics.createMajorGap() : this.layoutMetrics.createMinorGap());
    }
}
