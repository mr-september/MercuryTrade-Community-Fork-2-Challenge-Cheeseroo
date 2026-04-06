package com.mercury.platform.ui.components.panel.notification;

import com.mercury.platform.shared.entity.message.NotificationType;
import com.mercury.platform.ui.components.ComponentsFactory;
import com.mercury.platform.ui.components.panel.notification.factory.NotificationPanelFactory;
import com.mercury.platform.ui.components.panel.notification.factory.NotificationPanelProvider;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NotificationPanelLifecycleTest {

    @Test
    void factoryShouldReturnFreshProviderInstances() {
        NotificationPanelFactory factory = new NotificationPanelFactory();

        NotificationPanelProvider first = factory.getProviderFor(NotificationType.INC_ITEM_MESSAGE);
        NotificationPanelProvider second = factory.getProviderFor(NotificationType.INC_ITEM_MESSAGE);

        assertNotSame(first, second);
    }

    @Test
    void providerShouldInitializePanelBeforeSubscribingAndClearMutableState() {
        ComponentsFactory factory = ComponentsFactory.INSTANCE.copy();
        TestNotificationPanelProvider provider = new TestNotificationPanelProvider();

        TestNotificationPanel panel = (TestNotificationPanel) provider
                .setData("payload")
                .setController("controller")
                .setComponentsFactory(factory)
                .build();

        assertTrue(panel.onViewInitCalled);
        assertTrue(panel.subscribeCalled);
        assertTrue(panel.onViewInitCalledBeforeSubscribe);
        assertSame(factory, panel.seenFactoryAtInit);
        assertSame(factory, panel.seenFactoryAtSubscribe);
        assertNull(provider.getDataRef());
        assertNull(provider.getControllerRef());
        assertNull(provider.getComponentsFactoryRef());
    }

    @Test
    void destroyingPanelShouldStopItsTimeAgoTimer() throws Exception {
        TestNotificationPanel panel = new TestNotificationPanel();
        panel.setComponentsFactory(ComponentsFactory.INSTANCE.copy());
        panel.onViewInit();
        panel.subscribe();
        panel.createTimePanel();

        Timer timer = readTimer(panel);
        assertTrue(timer.isRunning());

        panel.onViewDestroy();

        assertFalse(timer.isRunning());
    }

    private Timer readTimer(NotificationPanel<?, ?> panel) throws Exception {
        Field field = NotificationPanel.class.getDeclaredField("timeAgoTimer");
        field.setAccessible(true);
        return (Timer) field.get(panel);
    }

    private static final class TestNotificationPanelProvider extends NotificationPanelProvider<String, String> {
        @Override
        public boolean isSuitable(NotificationType type) {
            return false;
        }

        @Override
        protected NotificationPanel<String, String> getPanel() {
            return new TestNotificationPanel();
        }

        private String getDataRef() {
            return this.data;
        }

        private String getControllerRef() {
            return this.controller;
        }

        private ComponentsFactory getComponentsFactoryRef() {
            return this.componentsFactory;
        }
    }

    private static final class TestNotificationPanel extends NotificationPanel<String, String> {
        private boolean onViewInitCalled;
        private boolean subscribeCalled;
        private boolean onViewInitCalledBeforeSubscribe;
        private ComponentsFactory seenFactoryAtInit;
        private ComponentsFactory seenFactoryAtSubscribe;

        @Override
        public void onViewInit() {
            this.onViewInitCalled = true;
            this.seenFactoryAtInit = this.componentsFactory;
        }

        @Override
        public void subscribe() {
            this.onViewInitCalledBeforeSubscribe = this.onViewInitCalled;
            this.seenFactoryAtSubscribe = this.componentsFactory;
            this.subscribeCalled = true;
            super.subscribe();
        }

        @Override
        protected void updateHotKeyPool() {
        }
        JPanel createTimePanel() {
            return this.getTimePanel();
        }
    }
}
