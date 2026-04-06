package com.mercury.platform.ui.frame.titled;

import com.mercury.platform.shared.IconConst;
import com.mercury.platform.ui.components.ComponentsFactory;
import com.mercury.platform.ui.frame.AbstractComponentFrame;
import com.mercury.platform.ui.misc.AppThemeColor;
import com.mercury.platform.ui.misc.MercuryStoreUI;
import com.mercury.platform.ui.misc.SwingUiExecutor;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public abstract class AbstractTitledComponentFrame extends AbstractComponentFrame {
    protected JPanel miscPanel;
    protected JButton hideButton;
    protected JPanel headerPanel;
    private JLabel frameTitleLabel;
    private float otherScale = 1.0f;

    protected AbstractTitledComponentFrame() {
        super();
        this.configureOtherScaleFactory();
        MercuryStoreUI.saveScaleSubject.subscribe(scaleData -> SwingUiExecutor.run(() -> this.onScaleDataSaved(scaleData)));
    }

    @Override
    protected void initialize() {
        this.configureOtherScaleFactory();
        super.initialize();
        this.initHeaderPanel();
    }

    @Override
    public void showComponent() {
        this.refreshScaleIfNeeded();
        super.showComponent();
    }

    private void initHeaderPanel() {
        if (layout instanceof BorderLayout) {
            this.headerPanel = new JPanel(new BorderLayout());
            this.headerPanel.setBackground(AppThemeColor.HEADER);
            this.headerPanel.setPreferredSize(this.componentsFactory.convertSize(new Dimension(100, 26)));
            this.headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, AppThemeColor.MSG_HEADER_BORDER));

            JLabel appIcon = componentsFactory.getIconLabel(IconConst.APP_ICON, 16);
            this.frameTitleLabel = componentsFactory.getTextLabel(getFrameTitle());
            this.frameTitleLabel.setHorizontalAlignment(SwingConstants.LEFT);
            this.frameTitleLabel.setVerticalAlignment(SwingConstants.CENTER);
            this.frameTitleLabel.addMouseListener(new DraggedFrameMouseListener());
            this.frameTitleLabel.addMouseMotionListener(new DraggedFrameMotionListener());

            appIcon.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
            this.headerPanel.add(appIcon, BorderLayout.LINE_START);
            this.headerPanel.add(this.frameTitleLabel, BorderLayout.CENTER);

            this.miscPanel = this.componentsFactory.getJPanel(new BorderLayout(), AppThemeColor.HEADER);
            this.hideButton = componentsFactory.getIconButton(IconConst.CLOSE, 14, AppThemeColor.HEADER, "");
            this.hideButton.addActionListener(action -> {
                this.hideComponent();
            });
            this.miscPanel.add(hideButton, BorderLayout.LINE_END);
            this.headerPanel.add(miscPanel, BorderLayout.LINE_END);
            this.add(headerPanel, BorderLayout.PAGE_START);
        }
    }

    protected abstract String getFrameTitle();

    public void setFrameTitle(String title) {
        if (this.frameTitleLabel != null) {
            this.frameTitleLabel.setText(title);
        }
    }

    protected void removeHideButton() {
        this.miscPanel.remove(hideButton);
    }

    @Override
    protected LayoutManager getFrameLayout() {
        return new BorderLayout();
    }

    protected boolean handlesVisibleScaleRefresh() {
        return false;
    }

    private void onScaleDataSaved(Map<String, Float> scaleData) {
        if (this.scaleConfig == null || scaleData == null || !scaleData.containsKey("other")) {
            return;
        }
        float updatedScale = scaleData.get("other");
        if (Math.abs(updatedScale - this.otherScale) < 0.001f) {
            return;
        }
        if (this.isVisible()) {
            if (this.handlesVisibleScaleRefresh()) {
                this.otherScale = updatedScale;
                this.configureOtherScaleFactory();
            } else {
                this.rebuildScaledView(updatedScale);
            }
        } else {
            this.otherScale = updatedScale;
        }
    }

    private void refreshScaleIfNeeded() {
        if (this.scaleConfig == null) {
            return;
        }
        float configuredScale = this.scaleConfig.get("other");
        if (Math.abs(configuredScale - this.otherScale) >= 0.001f || this.headerPanel == null) {
            this.rebuildScaledView(configuredScale);
        }
    }

    private void rebuildScaledView(float configuredScale) {
        Point currentLocation = this.getLocation();
        this.getContentPane().removeAll();
        this.headerPanel = null;
        this.miscPanel = null;
        this.hideButton = null;
        this.frameTitleLabel = null;
        this.otherScale = configuredScale;
        this.configureOtherScaleFactory();
        this.initHeaderPanel();
        this.onViewInit();
        this.revalidate();
        this.repaint();
        this.pack();
        if (currentLocation != null) {
            this.setLocation(currentLocation);
        }
    }

    private void configureOtherScaleFactory() {
        if (this.scaleConfig == null) {
            return;
        }
        this.otherScale = this.scaleConfig.get("other");
        this.componentsFactory = ComponentsFactory.INSTANCE.copy();
        this.componentsFactory.setScale(this.otherScale);
    }
}
