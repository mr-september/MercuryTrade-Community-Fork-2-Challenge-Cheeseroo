package com.mercury.platform.ui.components.panel.notification.factory;
import com.mercury.platform.shared.entity.message.NotificationType;

import java.util.NoSuchElementException;

public class NotificationPanelFactory {
    public NotificationPanelProvider getProviderFor(NotificationType type) {
        switch (type) {
            case INC_ITEM_MESSAGE:
                return new ItemIncPanelProvider();
            case INC_CURRENCY_MESSAGE:
                return new CurrencyIncPanelProvider();
            case OUT_ITEM_MESSAGE:
                return new ItemOutPanelProvider();
            case OUT_CURRENCY_MESSAGE:
                return new CurrencyOutPanelProvider();
            case SCANNER_MESSAGE:
                return new ScannerPanelProvider();
            case HISTORY:
                return new HistoryPanelProvider();
            default:
                throw new NoSuchElementException("Notification panel provider for <" + type + "> doesn't exist.");
        }
    }
}
