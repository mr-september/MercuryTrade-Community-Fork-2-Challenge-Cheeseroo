package com.mercury.platform.ui.frame.titled;

import com.mercury.platform.shared.HistoryManager;
import com.mercury.platform.shared.config.descriptor.FrameDescriptor;
import com.mercury.platform.shared.entity.message.NotificationDescriptor;
import com.mercury.platform.shared.entity.message.NotificationType;
import com.mercury.platform.shared.store.MercuryStoreCore;
import com.mercury.platform.ui.components.fields.style.MercuryScrollBarUI;
import com.mercury.platform.ui.components.panel.VerticalScrollContainer;
import com.mercury.platform.ui.components.panel.notification.NotificationPanel;
import com.mercury.platform.ui.components.panel.notification.factory.NotificationPanelFactory;
import com.mercury.platform.ui.misc.AppThemeColor;
import com.mercury.platform.ui.misc.SwingUiExecutor;
import org.apache.commons.lang3.ArrayUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.List;


public class HistoryFrame extends AbstractTitledComponentFrame {
    private static final int INITIAL_HISTORY_BATCH_SIZE = 10;
    private static final int INFINITE_SCROLL_BATCH_SIZE = 5;
    private static final int TRIMMED_COMPONENT_COUNT = 5;
    private static final int MAX_VISIBLE_HISTORY_PANELS = 40;

    private JPanel mainContainer;
    private NotificationPanelFactory factory;
    private List<NotificationDescriptor> currentMessages;

    public HistoryFrame() {
        super();
        FrameDescriptor frameDescriptor = this.framesConfig.get(this.getClass().getSimpleName());
        this.setPreferredSize(frameDescriptor.getFrameSize());
        this.componentsFactory.setScale(this.scaleConfig.get("other"));
    }

    @Override
    public void onViewInit() {
        initHistoryFrame();
    }

    private void initHistoryFrame() {
        this.factory = new NotificationPanelFactory();
        this.currentMessages = new ArrayList<>();
        this.mainContainer = new VerticalScrollContainer();
        this.mainContainer.setBackground(AppThemeColor.FRAME);
        this.mainContainer.setLayout(new BoxLayout(this.mainContainer, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(this.mainContainer);
        scrollPane.setBorder(null);
        scrollPane.setBackground(AppThemeColor.FRAME);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                HistoryFrame.this.repaint();
            }
        });
        JScrollBar vBar = scrollPane.getVerticalScrollBar();
        vBar.setBackground(AppThemeColor.SLIDE_BG);
        vBar.setUI(new MercuryScrollBarUI());
        vBar.setPreferredSize(new Dimension(16, Integer.MAX_VALUE));
        vBar.setUnitIncrement(3);
        vBar.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 2));
        vBar.addAdjustmentListener(e -> repaint());

        this.add(scrollPane, BorderLayout.CENTER);
        mainContainer.getParent().setBackground(AppThemeColor.FRAME);

        this.appendLatestHistory(HistoryManager.INSTANCE.fetchNext(INITIAL_HISTORY_BATCH_SIZE));
        this.miscPanel.add(getClearButton(), 0);
        this.pack();
        vBar.setValue(vBar.getMaximum());
        vBar.addAdjustmentListener((AdjustmentEvent e) -> {
            if (vBar.getValue() < 100) {
                String[] nextMessages = HistoryManager.INSTANCE.fetchNext(INFINITE_SCROLL_BATCH_SIZE);
                if (nextMessages.length > 0) {
                    this.prependOlderHistory(nextMessages);
                    vBar.setValue(vBar.getValue() + 100);
                }
            }
        });
    }

    private void appendLatestHistory(String[] messages) {
        ArrayUtils.reverse(messages);
        for (String message : messages) {
            NotificationDescriptor parsedNotificationDescriptor = parseMessage(message);
            if (parsedNotificationDescriptor != null) {
                addHistoryPanel(parsedNotificationDescriptor, false);
            }
        }
    }

    private void prependOlderHistory(String[] messages) {
        for (String message : messages) {
            NotificationDescriptor parsedNotificationDescriptor = parseMessage(message);
            if (parsedNotificationDescriptor != null) {
                addHistoryPanel(parsedNotificationDescriptor, true);
            }
        }
        this.mainContainer.revalidate();
        this.mainContainer.repaint();
        this.pack();
    }

    private NotificationDescriptor parseMessage(String message) {
        return new com.mercury.platform.shared.messageparser.MessageParser().parse(message);
    }

    private void addHistoryPanel(NotificationDescriptor descriptor, boolean prepend) {
        NotificationPanel panel = this.factory.getProviderFor(NotificationType.HISTORY)
                                              .setData(descriptor)
                                              .setComponentsFactory(this.componentsFactory)
                                              .build();
        if (prepend) {
            this.currentMessages.add(0, descriptor);
            this.mainContainer.add(panel, 0);
        } else {
            this.currentMessages.add(descriptor);
            this.mainContainer.add(panel);
        }
    }

    private JButton getClearButton() {
        JButton clearHistory =
                componentsFactory.getIconButton("app/clear-history.png",
                                                13,
                                                AppThemeColor.HEADER,
                                                "Clear history");
        clearHistory.addActionListener(action -> {
            HistoryManager.INSTANCE.clear();
            this.currentMessages.clear();
            this.mainContainer.removeAll();
            this.mainContainer.revalidate();
            this.mainContainer.repaint();
            this.pack();
        });
        return clearHistory;
    }

    @Override
    protected String getFrameTitle() {
        return "MercuryChat: History";
    }

    @Override
    public void subscribe() {
        MercuryStoreCore.newNotificationSubject.subscribe(SwingUiExecutor.onEdt(message -> {
            if (!currentMessages.contains(message)) {
                HistoryManager.INSTANCE.add(message);
                addHistoryPanel(message, false);
                this.trimContainer();
                this.mainContainer.revalidate();
                this.mainContainer.repaint();
                this.pack();
            }
        }));
    }

    private void trimContainer() {
        if (mainContainer.getComponentCount() > MAX_VISIBLE_HISTORY_PANELS) {
            for (int i = 0; i < TRIMMED_COMPONENT_COUNT && mainContainer.getComponentCount() > 0; i++) {
                mainContainer.remove(0);
                if (!this.currentMessages.isEmpty()) {
                    this.currentMessages.remove(0);
                }
            }
            this.pack();
        }
    }
}
