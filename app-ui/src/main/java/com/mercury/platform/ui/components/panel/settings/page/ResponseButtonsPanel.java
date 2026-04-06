package com.mercury.platform.ui.components.panel.settings.page;

import com.mercury.platform.TranslationKey;
import com.mercury.platform.shared.IconConst;
import com.mercury.platform.shared.config.descriptor.ResponseButtonDescriptor;
import com.mercury.platform.ui.components.ComponentsFactory;
import com.mercury.platform.ui.components.fields.font.FontStyle;
import com.mercury.platform.ui.components.panel.misc.ViewInit;
import com.mercury.platform.ui.components.panel.settings.SettingsLayoutMetrics;
import com.mercury.platform.ui.misc.AppThemeColor;
import com.mercury.platform.ui.misc.MercuryStoreUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.List;


public class ResponseButtonsPanel extends JPanel implements ViewInit {
    private List<ResponseButtonDescriptor> buttons;
    private final ComponentsFactory componentsFactory;
    private final HotKeyGroup hotKeyGroup;
    private final SettingsLayoutMetrics layoutMetrics;

    public ResponseButtonsPanel(List<ResponseButtonDescriptor> buttons, HotKeyGroup hotKeyGroup) {
        this(buttons, hotKeyGroup, ComponentsFactory.INSTANCE);
    }

    public ResponseButtonsPanel(List<ResponseButtonDescriptor> buttons, HotKeyGroup hotKeyGroup, ComponentsFactory componentsFactory) {
        super(new BorderLayout(new SettingsLayoutMetrics(componentsFactory).scaleValue(4),
                new SettingsLayoutMetrics(componentsFactory).scaleValue(4)));
        this.hotKeyGroup = hotKeyGroup;
        this.buttons = buttons;
        this.componentsFactory = componentsFactory;
        this.layoutMetrics = new SettingsLayoutMetrics(this.componentsFactory);
    }

    @Override
    public void onViewInit() {
        this.setBackground(AppThemeColor.SETTINGS_BG);
        int sectionGap = this.layoutMetrics.scaleValue(4);
        JPanel buttonsTable = this.componentsFactory.getJPanel(new GridLayout(0, 1, sectionGap, sectionGap), AppThemeColor.SETTINGS_BG);
        buttonsTable.setBorder(BorderFactory.createLineBorder(AppThemeColor.ADR_DEFAULT_BORDER));

        JPanel headerPanel = this.componentsFactory.getJPanel(new BorderLayout(sectionGap, sectionGap), AppThemeColor.SETTINGS_BG);

        JLabel titleLabel = componentsFactory.getTextLabel(FontStyle.REGULAR, AppThemeColor.TEXT_DEFAULT, null, 15f, TranslationKey.label.value());
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setPreferredSize(this.layoutMetrics.getResponseTitleSize());
        JLabel valueLabel = componentsFactory.getTextLabel(FontStyle.REGULAR, AppThemeColor.TEXT_DEFAULT, null, 15f, TranslationKey.response_text.value());
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel hotKeyLabel = componentsFactory.getTextLabel(FontStyle.REGULAR, AppThemeColor.TEXT_DEFAULT, null, 15f, TranslationKey.hot_key.value());
        hotKeyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        hotKeyLabel.setPreferredSize(this.layoutMetrics.getResponseHotKeyHeaderSize());

        JLabel closeLabel = componentsFactory.getTextLabel(FontStyle.REGULAR, AppThemeColor.TEXT_DEFAULT, null, 15f, "");
        closeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        headerPanel.add(titleLabel, BorderLayout.LINE_START);
        headerPanel.add(valueLabel, BorderLayout.CENTER);

        int actionGap = this.layoutMetrics.scaleValue(10);
        JPanel actionsPanel = this.componentsFactory.getJPanel(new BorderLayout(actionGap, actionGap), AppThemeColor.SETTINGS_BG);
        actionsPanel.add(this.componentsFactory.getIconLabel(IconConst.CLOSE, 15), BorderLayout.CENTER);
//        actionsPanel.add(this.componentsFactory.getIconLabel(IconConst.KICK, 15), BorderLayout.LINE_END);

        JPanel miscPanel = this.componentsFactory.getJPanel(new BorderLayout(sectionGap, sectionGap), AppThemeColor.SETTINGS_BG);
        miscPanel.add(actionsPanel, BorderLayout.LINE_START);
        miscPanel.add(hotKeyLabel, BorderLayout.CENTER);
        miscPanel.add(closeLabel, BorderLayout.LINE_END);
        headerPanel.add(miscPanel, BorderLayout.LINE_END);

        buttonsTable.add(headerPanel);

        buttons.forEach(it -> {
            buttonsTable.add(this.getResponseRow(it));
        });
        this.add(buttonsTable, BorderLayout.CENTER);
        JButton addButton = this.componentsFactory.getIconButton("app/add_button.png", 22, AppThemeColor.HEADER, TranslationKey.add_button.value());
        addButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppThemeColor.BORDER),
                BorderFactory.createEmptyBorder(
                        this.layoutMetrics.scaleValue(3),
                        this.layoutMetrics.scaleValue(3),
                        this.layoutMetrics.scaleValue(3),
                        this.layoutMetrics.scaleValue(3))));
        addButton.addActionListener(action -> {
            ResponseButtonDescriptor descriptor = new ResponseButtonDescriptor();
            int size = buttons.size();
            descriptor.setId(++size);
            buttons.add(descriptor);
            buttonsTable.add(this.getResponseRow(descriptor));
            MercuryStoreUI.settingsRepaintSubject.onNext(true);
            MercuryStoreUI.settingsPackSubject.onNext(true);
        });
        this.add(buttonsTable, BorderLayout.CENTER);
        this.add(addButton, BorderLayout.PAGE_END);
    }

    private JPanel getResponseRow(ResponseButtonDescriptor descriptor) {
        int sectionGap = this.layoutMetrics.scaleValue(4);
        JPanel root = this.componentsFactory.getJPanel(new BorderLayout(sectionGap, sectionGap), AppThemeColor.SETTINGS_BG);
        JTextField titleField = this.componentsFactory.getTextField(descriptor.getTitle(), FontStyle.REGULAR, 15f);
        titleField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                descriptor.setTitle(titleField.getText());
            }
        });
        titleField.setPreferredSize(this.layoutMetrics.getResponseTitleSize());
        JTextField responseField = this.componentsFactory.getTextField(descriptor.getResponseText(), FontStyle.REGULAR, 15f);
        responseField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                descriptor.setResponseText(responseField.getText());
            }
        });
        root.add(this.componentsFactory.wrapToSlide(titleField, AppThemeColor.SETTINGS_BG,
                0,
                this.layoutMetrics.scaleValue(2),
                this.layoutMetrics.scaleValue(2),
                0), BorderLayout.LINE_START);
        root.add(this.componentsFactory.wrapToSlide(responseField, AppThemeColor.SETTINGS_BG,
                0,
                0,
                this.layoutMetrics.scaleValue(2),
                0), BorderLayout.CENTER);

        JPanel miscPanel = this.componentsFactory.getJPanel(new BorderLayout(sectionGap, sectionGap), AppThemeColor.SETTINGS_BG);

        JPanel checkboxPanel = this.componentsFactory.getJPanel(new BorderLayout(sectionGap, sectionGap), AppThemeColor.SETTINGS_BG);

        JCheckBox checkBoxClose = this.componentsFactory.getCheckBox(descriptor.isClose(), TranslationKey.close_notif_panel_after_click.value());
        checkBoxClose.addActionListener(action -> {
            descriptor.setClose(checkBoxClose.isSelected());
        });
        checkboxPanel.add(checkBoxClose, BorderLayout.LINE_START);


//        JCheckBox checkBoxKick = this.componentsFactory.getCheckBox(descriptor.isKickLeave(), "Kick/Leave after click?");
//        checkBoxKick.addActionListener(action -> {
//            descriptor.setKickLeave(checkBoxKick.isSelected());
//        });
//        checkboxPanel.add(checkBoxKick, BorderLayout.LINE_END);

        miscPanel.add(checkboxPanel, BorderLayout.LINE_START);

        HotKeyPanel hotKeyPanel = new HotKeyPanel(descriptor.getHotKeyDescriptor(), this.componentsFactory);
        this.hotKeyGroup.registerHotkey(hotKeyPanel);
        miscPanel.add(this.componentsFactory.wrapToSlide(hotKeyPanel, AppThemeColor.SETTINGS_BG,
                0,
                0,
                this.layoutMetrics.scaleValue(2),
                0), BorderLayout.CENTER);

        JButton removeButton = this.componentsFactory.getIconButton("app/adr/remove_node.png", 17, AppThemeColor.SETTINGS_BG, TranslationKey.remove_button.value());
        removeButton.addActionListener(action -> {
            root.getParent().remove(root);
            this.buttons.remove(descriptor);
            MercuryStoreUI.settingsPackSubject.onNext(true);
            MercuryStoreUI.settingsRepaintSubject.onNext(true);
        });
        miscPanel.add(removeButton, BorderLayout.LINE_END);

        root.add(miscPanel, BorderLayout.LINE_END);
        return root;
    }
}
