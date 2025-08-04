package com.mercury.platform.ui.frame.other;

import com.mercury.platform.TranslationKey;
import com.mercury.platform.shared.store.MercuryStoreCore;
import com.mercury.platform.ui.components.fields.font.FontStyle;
import com.mercury.platform.ui.components.fields.font.TextAlignment;
import com.mercury.platform.ui.frame.AbstractOverlaidFrame;
import com.mercury.platform.ui.manager.FramesManager;
import com.mercury.platform.ui.misc.AppThemeColor;
import com.mercury.platform.ui.misc.MercuryStoreUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

public class SetUpScaleFrame extends AbstractOverlaidFrame {
    private Map<String, Float> scaleData;

    private final static int MIN_SCALE = 5;
    private final static int MAX_SCALE = 40; 

    public SetUpScaleFrame() {
        super();
    }

    @Override
    protected void initialize() {
        this.getRootPane().setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppThemeColor.TRANSPARENT, 2),
                BorderFactory.createLineBorder(AppThemeColor.BORDER, 1)));
        this.scaleData = this.scaleConfig.getMap();
    }

    @Override
    public void onViewInit() {
        JPanel rootPanel = componentsFactory.getTransparentPanel(new BorderLayout());
        rootPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 0, 6));

        JPanel header = componentsFactory.getTransparentPanel(new FlowLayout(FlowLayout.CENTER));
        header.add(componentsFactory.getTextLabel(FontStyle.REGULAR, AppThemeColor.TEXT_DEFAULT, TextAlignment.LEFTOP, 18f, TranslationKey.scale_settings.value()));

        JPanel root = componentsFactory.getTransparentPanel(new BorderLayout());
        root.setBorder(BorderFactory.createLineBorder(AppThemeColor.HEADER));
        root.setBackground(AppThemeColor.SLIDE_BG);
        root.add(getScaleSettingsPanel(), BorderLayout.CENTER);

        JPanel miscPanel = componentsFactory.getTransparentPanel(new FlowLayout(FlowLayout.CENTER));
        JButton cancel = componentsFactory.getBorderedButton(TranslationKey.cancel.value());
        cancel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppThemeColor.BORDER),
                BorderFactory.createLineBorder(AppThemeColor.TRANSPARENT, 3)
        ));
        cancel.setBackground(AppThemeColor.FRAME);

        cancel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                FramesManager.INSTANCE.disableScale();
            }
        });
        cancel.setPreferredSize(new Dimension(100, 26));

        JButton save = componentsFactory.getBorderedButton(TranslationKey.save.value());
        save.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                FramesManager.INSTANCE.disableScale();
                MercuryStoreCore.saveConfigSubject.onNext(true);
                MercuryStoreUI.saveScaleSubject.onNext(scaleData);
            }
        });
        save.setPreferredSize(new Dimension(100, 26));

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

    private JPanel getScaleSettingsPanel() {
        JPanel root = componentsFactory.getTransparentPanel(new GridLayout(2, 1));
        JLabel iconLabel = componentsFactory.getTextLabel(
            FontStyle.REGULAR,
            AppThemeColor.TEXT_DEFAULT,
            TextAlignment.LEFTOP,
            17f,
            "Icon scaling: ");
        JSlider iconSlider = componentsFactory.getSlider(MIN_SCALE, MAX_SCALE, (int) (scaleData.getOrDefault("icon", 1.0f) * 10));
        JLabel iconValue = componentsFactory.getTextLabel(
            FontStyle.REGULAR,
            AppThemeColor.TEXT_DEFAULT,
            TextAlignment.LEFTOP,
            17f,
            String.valueOf(iconSlider.getValue() * 10) + "%");
        iconValue.setBorder(null);
        
        iconSlider.addChangeListener(e -> {
            int sliderVal = iconSlider.getValue();
            iconValue.setText(String.valueOf(sliderVal * 10) + "%");
            scaleData.put("icon", sliderVal / 10f);
            MercuryStoreUI.notificationScaleSubject.onNext(sliderVal / 10f);
            MercuryStoreUI.taskBarScaleSubject.onNext(sliderVal / 10f);
            repaint();
        });

        JLabel textLabel = componentsFactory.getTextLabel(
                FontStyle.REGULAR,
                AppThemeColor.TEXT_DEFAULT,
                TextAlignment.LEFTOP,
                17f,
                "Text scaling: ");
        JSlider textSlider = componentsFactory.getSlider(MIN_SCALE, MAX_SCALE, (int) (scaleData.getOrDefault("text", 1.0f) * 10));
        JLabel textValue = componentsFactory.getTextLabel(
                FontStyle.REGULAR,
                AppThemeColor.TEXT_DEFAULT,
                TextAlignment.LEFTOP,
                17f,
                String.valueOf(textSlider.getValue() * 10) + "%");
        textValue.setBorder(null);
        
        textSlider.addChangeListener(e -> {
            int sliderVal = textSlider.getValue();
            textValue.setText(String.valueOf(sliderVal * 10) + "%");
            scaleData.put("text", sliderVal / 10f);
            MercuryStoreUI.textScaleSubject.onNext(sliderVal / 10f);
            repaint();
        });
        
        root.add(componentsFactory.getSliderSettingsPanel(iconLabel, iconValue, iconSlider));
        root.add(componentsFactory.getSliderSettingsPanel(textLabel, textValue, textSlider));
        return root;
    }

    @Override
    public void subscribe() {

    }

    @Override
    protected LayoutManager getFrameLayout() {
        return new BorderLayout();
    }
}