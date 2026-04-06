package com.mercury.platform.ui.frame.other;

import com.mercury.platform.TranslationKey;
import com.mercury.platform.shared.store.MercuryStoreCore;
import com.mercury.platform.ui.components.fields.font.FontStyle;
import com.mercury.platform.ui.components.fields.font.TextAlignment;
import com.mercury.platform.ui.components.panel.settings.AutoScalingSettingsPanel;
import com.mercury.platform.ui.components.panel.settings.SettingsLayoutMetrics;
import com.mercury.platform.ui.frame.AbstractOverlaidFrame;
import com.mercury.platform.ui.manager.FramesManager;
import com.mercury.platform.ui.misc.AppThemeColor;
import com.mercury.platform.ui.misc.MercuryStoreUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SetUpScaleFrame extends AbstractOverlaidFrame {
    private AutoScalingSettingsPanel scaleSettingsPanel;
    private SettingsLayoutMetrics layoutMetrics;

    public SetUpScaleFrame() {
        super();
    }

    @Override
    protected void initialize() {
        this.componentsFactory.setScale(this.scaleConfig.get("other"));
        this.layoutMetrics = new SettingsLayoutMetrics(this.componentsFactory);
        this.getRootPane().setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppThemeColor.TRANSPARENT, 2),
                BorderFactory.createLineBorder(AppThemeColor.BORDER, 1)));
        this.scaleSettingsPanel = new AutoScalingSettingsPanel(this.componentsFactory);
    }

    @Override
    public void onViewInit() {
        JPanel rootPanel = componentsFactory.getTransparentPanel(new BorderLayout());
        rootPanel.setBorder(BorderFactory.createEmptyBorder(
                this.layoutMetrics.scaleValue(6),
                this.layoutMetrics.scaleValue(6),
                0,
                this.layoutMetrics.scaleValue(6)));

        JPanel header = componentsFactory.getTransparentPanel(new FlowLayout(FlowLayout.CENTER));
        header.add(componentsFactory.getTextLabel(
                FontStyle.REGULAR,
                AppThemeColor.TEXT_DEFAULT,
                TextAlignment.LEFTOP,
                18f,
                TranslationKey.scale_settings.value()));

        JPanel root = componentsFactory.getTransparentPanel(new BorderLayout());
        root.setBorder(BorderFactory.createLineBorder(AppThemeColor.HEADER));
        root.setBackground(AppThemeColor.SLIDE_BG);
        root.add(this.scaleSettingsPanel, BorderLayout.CENTER);

        JPanel miscPanel = componentsFactory.getTransparentPanel(new FlowLayout(FlowLayout.CENTER));
        JButton cancel = componentsFactory.getBorderedButton(TranslationKey.cancel.value());
        cancel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppThemeColor.BORDER),
                BorderFactory.createLineBorder(AppThemeColor.TRANSPARENT, 3)));
        cancel.setBackground(AppThemeColor.FRAME);
        cancel.setPreferredSize(this.layoutMetrics.getSetupDialogButtonSize());
        cancel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                scaleSettingsPanel.restoreConfiguration();
                FramesManager.INSTANCE.disableScale();
            }
        });

        JButton save = componentsFactory.getBorderedButton(TranslationKey.save.value());
        save.setPreferredSize(this.layoutMetrics.getSetupDialogButtonSize());
        save.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                scaleSettingsPanel.applyConfiguration();
                FramesManager.INSTANCE.disableScale();
                MercuryStoreCore.saveConfigSubject.onNext(true);
                MercuryStoreUI.saveScaleSubject.onNext(scaleConfig.getMap());
            }
        });

        miscPanel.add(cancel);
        miscPanel.add(save);
        rootPanel.add(root, BorderLayout.CENTER);
        this.add(header, BorderLayout.PAGE_START);
        this.add(rootPanel, BorderLayout.CENTER);
        this.add(miscPanel, BorderLayout.PAGE_END);
        this.pack();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 4 - this.getSize().height / 2);
    }

    @Override
    public void subscribe() {
    }

    @Override
    protected LayoutManager getFrameLayout() {
        return new BorderLayout();
    }
}
