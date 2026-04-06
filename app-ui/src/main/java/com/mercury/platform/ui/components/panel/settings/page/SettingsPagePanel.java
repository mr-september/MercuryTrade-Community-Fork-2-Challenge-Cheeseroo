package com.mercury.platform.ui.components.panel.settings.page;


import com.mercury.platform.shared.config.Configuration;
import com.mercury.platform.ui.adr.components.AdrComponentsFactory;
import com.mercury.platform.ui.components.ComponentsFactory;
import com.mercury.platform.ui.components.panel.VerticalScrollContainer;
import com.mercury.platform.ui.components.panel.misc.ViewInit;
import com.mercury.platform.ui.components.panel.settings.SettingsLayoutMetrics;
import com.mercury.platform.ui.misc.AppThemeColor;

import javax.swing.*;
import java.awt.*;

public abstract class SettingsPagePanel extends JPanel implements ViewInit {
    protected ComponentsFactory componentsFactory = ComponentsFactory.INSTANCE.copy();
    protected AdrComponentsFactory adrComponentsFactory;
    protected SettingsLayoutMetrics layoutMetrics;
    protected JPanel container;

    public SettingsPagePanel() {
        this.setLayout(new BorderLayout());
        this.setBackground(AppThemeColor.FRAME);
        this.initializePage();
    }

    public final void initializePage() {
        this.componentsFactory = ComponentsFactory.INSTANCE.copy();
        this.componentsFactory.setScale(Configuration.get().scaleConfiguration().get("other"));
        this.layoutMetrics = new SettingsLayoutMetrics(this.componentsFactory);
        this.adrComponentsFactory = new AdrComponentsFactory(this.componentsFactory);
        this.removeAll();
        this.onViewInit();
        this.revalidate();
        this.repaint();
    }

    @Override
    public void onViewInit() {
        this.container = new VerticalScrollContainer();
        this.container.setBackground(AppThemeColor.FRAME);
        this.container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        JScrollPane verticalContainer = this.componentsFactory.getVerticalContainer(this.container);
        this.add(verticalContainer, BorderLayout.CENTER);
    }

    protected final JPanel wrapToSettingsSlide(JComponent panel, Color bg, int top, int left, int bottom, int right) {
        return this.componentsFactory.wrapToSlide(panel, bg,
                this.layoutMetrics.scaleValue(top),
                this.layoutMetrics.scaleValue(left),
                this.layoutMetrics.scaleValue(bottom),
                this.layoutMetrics.scaleValue(right));
    }

    public abstract void onSave();

    public abstract void restore();
}
