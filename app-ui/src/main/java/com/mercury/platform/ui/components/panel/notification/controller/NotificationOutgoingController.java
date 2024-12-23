package com.mercury.platform.ui.components.panel.notification.controller;


import com.mercury.platform.shared.entity.message.NotificationDescriptor;
import com.mercury.platform.shared.store.MercuryStoreCore;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;

public class NotificationOutgoingController implements OutgoingPanelController {
    private NotificationDescriptor notificationDescriptor;

    public NotificationOutgoingController(NotificationDescriptor notificationDescriptor) {
        this.notificationDescriptor = notificationDescriptor;
    }

    @Override
    public void visitHideout() {
        MercuryStoreCore.chatCommandSubject.onNext("/hideout " + StringUtils.trim(notificationDescriptor.getWhisperNickname()));
    }

    @Override
    public void performWhoIs() {
        MercuryStoreCore.chatCommandSubject.onNext("/whois " + StringUtils.trim(notificationDescriptor.getWhisperNickname()));
    }

    @Override
    public void performLeave(String nickName) {
        MercuryStoreCore.chatCommandSubject.onNext("/leave");
    }

    @Override
    public void performOfferTrade() {
        MercuryStoreCore.chatCommandSubject.onNext("/tradewith " + StringUtils.trim(notificationDescriptor.getWhisperNickname()));
    }

    @Override
    public void performHide() {
        this.closeMessagePanel();
    }

    @Override
    public void performKickLeave(String nickName) {
        MercuryStoreCore.chatCommandSubject.onNext("/kick " + StringUtils.trim(nickName));
    }

    @Override
    public void performOpenChat() {
        MercuryStoreCore.openChatSubject.onNext(StringUtils.trim(notificationDescriptor.getWhisperNickname()));
    }

    @Override
    public void performResponse(String response) {
        MercuryStoreCore.chatCommandSubject.onNext("@" + StringUtils.trim(notificationDescriptor.getWhisperNickname()) + " " + response);
    }

    private void closeMessagePanel() {
        Timer timer = new Timer(30, action -> {
            MercuryStoreCore.removeNotificationSubject.onNext(notificationDescriptor);
        });
        timer.setRepeats(false);
        timer.start();
    }
}
