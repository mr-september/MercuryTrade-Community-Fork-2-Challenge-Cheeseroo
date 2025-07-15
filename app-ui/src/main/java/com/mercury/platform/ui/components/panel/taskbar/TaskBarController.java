package com.mercury.platform.ui.components.panel.taskbar;

/**
 * Created by Константин on 24.03.2017.
 */
public interface TaskBarController {
    void enableDND();

    void disableDND();

    void enablePushbullet();

    void disablePushbullet();

    void showITH();

    void performHideout();

    void showHelpIG();

    void showChatFiler();

    void showHistory();

    void openPINSettings();

    void openScaleSettings();

    void showSettings();

    void exit();

    void hideMessageNotifications();

    void showMessageNotifications();

    void performJoinChannel();
}
