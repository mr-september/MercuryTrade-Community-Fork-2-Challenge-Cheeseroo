package com.mercury.platform.ui.components.panel.settings.page;

import com.mercury.platform.Languages;
import com.mercury.platform.TranslationKey;
import com.mercury.platform.core.misc.WhisperNotifierStatus;
import com.mercury.platform.shared.CloneHelper;
import com.mercury.platform.shared.PushBulletManager;
import com.mercury.platform.shared.VulkanManager;
import com.mercury.platform.shared.config.Configuration;
import com.mercury.platform.shared.config.configration.PlainConfigurationService;
import com.mercury.platform.shared.config.descriptor.ApplicationDescriptor;
import com.mercury.platform.shared.config.descriptor.VulkanDescriptor;
import com.mercury.platform.ui.components.fields.font.FontStyle;
import com.mercury.platform.ui.dialog.OkDialog;
import com.mercury.platform.ui.manager.HideSettingsManager;
import com.mercury.platform.ui.misc.AppThemeColor;
import com.mercury.platform.ui.components.panel.settings.AutoScalingSettingsPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GeneralSettingsPagePanel extends SettingsPagePanel {
    // Constants for slider configurations
    private static final int FADE_TIME_MIN = 0;
    private static final int FADE_TIME_MAX = 10;
    private static final int OPACITY_MIN = 40;
    private static final int OPACITY_MAX = 100;
    private static final int GRID_COLUMNS = 2;
    private static final int GRID_SPACING = 4;
    private static final float REGULAR_FONT_SIZE = 16f;
    private static final int BORDER_WIDTH = 1;
    private static final int BORDER_PADDING = 2;
    private static final int LAYOUT_SPACING = 4;
    private static final int SLIDER_DECREMENT = 1;
    
    private PlainConfigurationService<ApplicationDescriptor> applicationConfig;
    private PlainConfigurationService<VulkanDescriptor> vulkanConfig;
    private ApplicationDescriptor applicationSnapshot;
    private VulkanDescriptor vulkanSnapshot;

    private JSlider minSlider;
    private JSlider maxSlider;

    @Override
    public void onViewInit() {
        super.onViewInit();
        this.applicationConfig = Configuration.get().applicationConfiguration();
        this.applicationSnapshot = CloneHelper.cloneObject(this.applicationConfig.get());
        this.vulkanConfig = Configuration.get().vulkanConfiguration();
        this.vulkanSnapshot = CloneHelper.cloneObject(this.vulkanConfig.get());
        VulkanManager.INSTANCE.runSupport(vulkanSnapshot);

        JPanel root = componentsFactory.getJPanel(new GridLayout(0, GRID_COLUMNS, GRID_SPACING, GRID_SPACING));
        root.setBorder(BorderFactory.createLineBorder(AppThemeColor.ADR_PANEL_BORDER));
        root.setBackground(AppThemeColor.ADR_BG);

        JCheckBox checkEnable = this.componentsFactory.getCheckBox(this.applicationSnapshot.isCheckOutUpdate());
        checkEnable.addActionListener(action -> {
            this.applicationSnapshot.setCheckOutUpdate(checkEnable.isSelected());
        });

        JCheckBox vulkanEnableCheck = this.componentsFactory.getCheckBox(this.vulkanSnapshot.isVulkanSupportEnabled());
        vulkanEnableCheck.addActionListener(action -> {
            this.vulkanSnapshot.setVulkanSupportEnabled(vulkanEnableCheck.isSelected());
        });

        JCheckBox hideTaskbarUntilHover = this.componentsFactory.getCheckBox(this.applicationSnapshot.isHideTaskbarUntilHover(), TranslationKey.taskbar_hover_tt.value());
        hideTaskbarUntilHover.addActionListener(action -> {
            this.applicationSnapshot.setHideTaskbarUntilHover(hideTaskbarUntilHover.isSelected());
        });

        JCheckBox disableGameToFront = this.componentsFactory.getCheckBox(this.applicationSnapshot.isDisableGameToFront(), TranslationKey.disable_game_to_front_tt.value());
        disableGameToFront.addActionListener(action -> {
            this.applicationSnapshot.setDisableGameToFront(disableGameToFront.isSelected());
        });

//        JCheckBox poe2Support = this.componentsFactory.getCheckBox(this.applicationSnapshot.isPoe2(), TranslationKey.poe2_support_tt.value());
//        poe2Support.addActionListener(action -> {
//            this.applicationSnapshot.setPoe2(poe2Support.isSelected());
//        });

        JSlider fadeTimeSlider = this.componentsFactory.getSlider(FADE_TIME_MIN, FADE_TIME_MAX, this.applicationSnapshot.getFadeTime(), AppThemeColor.SLIDE_BG);
        fadeTimeSlider.addChangeListener(e -> {
            this.applicationSnapshot.setFadeTime(fadeTimeSlider.getValue());
        });

        this.minSlider = this.componentsFactory.getSlider(OPACITY_MIN, OPACITY_MAX, this.applicationSnapshot.getMinOpacity(), AppThemeColor.SLIDE_BG);
        this.minSlider.addChangeListener(e -> {
            if (!(this.minSlider.getValue() > this.maxSlider.getValue())) {
                this.applicationSnapshot.setMinOpacity(this.minSlider.getValue());
            } else {
                minSlider.setValue(minSlider.getValue() - SLIDER_DECREMENT);
            }
        });

        this.maxSlider = this.componentsFactory.getSlider(OPACITY_MIN, OPACITY_MAX, this.applicationSnapshot.getMaxOpacity(), AppThemeColor.SLIDE_BG);
        this.maxSlider.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (minSlider.getValue() > maxSlider.getValue()) {
                    minSlider.setValue(maxSlider.getValue());
                }
                applicationSnapshot.setMaxOpacity(maxSlider.getValue());
            }
        });

        @SuppressWarnings("unchecked")
        JComboBox<String> notifierStatusPicker = (JComboBox<String>) this.componentsFactory.getComboBox(new String[]{TranslationKey.always_play_a_sound.value(), TranslationKey.only_when_tabbed_out.value(), TranslationKey.never.value()});
        notifierStatusPicker.setSelectedItem(this.applicationSnapshot.getNotifierStatus().asPretty());
        notifierStatusPicker.addActionListener(action -> {
            this.applicationSnapshot.setNotifierStatus(WhisperNotifierStatus.valueOfPretty((String) notifierStatusPicker.getSelectedItem()));
        });


        JComboBox<Languages> languagesPicker = this.componentsFactory.getComboBox(Languages.values());
        languagesPicker.setSelectedItem(this.applicationSnapshot.getLanguages() == null ? Languages.en : this.applicationSnapshot.getLanguages());
        languagesPicker.addActionListener(action -> {
            this.applicationSnapshot.setLanguages((Languages) languagesPicker.getSelectedItem());
        });

        JTextField gamePathField = this.componentsFactory.getTextField(this.applicationSnapshot.getGamePath());
        gamePathField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppThemeColor.BORDER, BORDER_WIDTH),
                BorderFactory.createLineBorder(AppThemeColor.TRANSPARENT, BORDER_PADDING)
        ));
        gamePathField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                applicationSnapshot.setGamePath(gamePathField.getText());
            }
        });

        JPanel poeFolderPanel = componentsFactory.getTransparentPanel(new BorderLayout(LAYOUT_SPACING, LAYOUT_SPACING));
        poeFolderPanel.add(gamePathField, BorderLayout.CENTER);
        JButton changeButton = this.componentsFactory.getBorderedButton(TranslationKey.change.value());
        poeFolderPanel.add(changeButton, BorderLayout.LINE_END);

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        changeButton.addActionListener(e -> {
            int returnVal = fileChooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                gamePathField.setText(fileChooser.getSelectedFile().getPath());
                applicationSnapshot.setGamePath(fileChooser.getSelectedFile().getPath());
            }
        });

        JPasswordField pushbulletTextField = this.componentsFactory.getPasswordField(this.applicationSnapshot.getPushbulletAccessToken(), FontStyle.REGULAR, (int) REGULAR_FONT_SIZE);
        pushbulletTextField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppThemeColor.BORDER, BORDER_WIDTH),
                BorderFactory.createLineBorder(AppThemeColor.TRANSPARENT, BORDER_PADDING)
        ));
        pushbulletTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                applicationSnapshot.setPushbulletAccessToken(new String(pushbulletTextField.getPassword()));
                PushBulletManager.INSTANCE.reloadAccessToken();
            }
        });

        JButton testPush = this.componentsFactory.getButton(TranslationKey.test_push.value());
        testPush.addActionListener((actionEvent) -> {
            PushBulletManager.INSTANCE.testPush();
        });

        JPanel pushbulletPanel = componentsFactory.getTransparentPanel(new BorderLayout(LAYOUT_SPACING, LAYOUT_SPACING));
        pushbulletPanel.add(pushbulletTextField, BorderLayout.CENTER);
        pushbulletPanel.add(testPush, BorderLayout.LINE_END);

        root.add(this.componentsFactory.getTextLabel(TranslationKey.choose_language.value(), FontStyle.REGULAR, REGULAR_FONT_SIZE));
        root.add(this.componentsFactory.wrapToSlide(languagesPicker, AppThemeColor.ADR_BG, 0, 0, 0, 2));
        root.add(this.componentsFactory.getTextLabel(TranslationKey.notify_me_when_an_update_is_available.value(), FontStyle.REGULAR, REGULAR_FONT_SIZE));
        root.add(checkEnable);
        root.add(this.componentsFactory.getTextLabel(TranslationKey.vulkan_support_enabled.value(), FontStyle.REGULAR, REGULAR_FONT_SIZE));
        root.add(vulkanEnableCheck);
        
        // Auto-scaling section
        AutoScalingSettingsPanel autoScalingPanel = new AutoScalingSettingsPanel();
        root.add(this.componentsFactory.getTextLabel(TranslationKey.auto_scaling.value(), FontStyle.REGULAR, REGULAR_FONT_SIZE));
        root.add(this.componentsFactory.wrapToSlide(autoScalingPanel, AppThemeColor.ADR_BG, 0, 0, 2, 2));
        
        root.add(this.componentsFactory.getTextLabel(TranslationKey.hide_taskbar.value(), FontStyle.REGULAR, (int) REGULAR_FONT_SIZE));
        root.add(hideTaskbarUntilHover);
//        root.add(this.componentsFactory.getTextLabel(TranslationKey.poe_2_support.value(), FontStyle.REGULAR, 16));
//        root.add(poe2Support);
        root.add(this.componentsFactory.getTextLabel(TranslationKey.disable_game_to_front.value(), FontStyle.REGULAR, (int) REGULAR_FONT_SIZE));
        root.add(disableGameToFront);
        root.add(this.componentsFactory.getTextLabel(TranslationKey.component_fade_out_time.value(": "), FontStyle.REGULAR, (int) REGULAR_FONT_SIZE));
        root.add(fadeTimeSlider);
        root.add(this.componentsFactory.getTextLabel(TranslationKey.min_opacity.value(": "), FontStyle.REGULAR, (int) REGULAR_FONT_SIZE));
        root.add(this.minSlider);
        root.add(this.componentsFactory.getTextLabel(TranslationKey.max_opacity.value(": "), FontStyle.REGULAR, (int) REGULAR_FONT_SIZE));
        root.add(this.maxSlider);
        root.add(this.componentsFactory.getTextLabel(TranslationKey.notification_sound_alerts.value(": "), FontStyle.REGULAR, (int) REGULAR_FONT_SIZE));
        root.add(this.componentsFactory.wrapToSlide(notifierStatusPicker, AppThemeColor.ADR_BG, 0, 0, 0, 2));
        root.add(this.componentsFactory.getTextLabel(TranslationKey.path_of_exile_folder.value(": "), FontStyle.REGULAR, (int) REGULAR_FONT_SIZE));
        root.add(this.componentsFactory.wrapToSlide(poeFolderPanel, AppThemeColor.ADR_BG, 0, 0, 2, 2));
        root.add(this.componentsFactory.getTextLabel(TranslationKey.pushbullet_accesstoken.value(": "), FontStyle.REGULAR, (int) REGULAR_FONT_SIZE));
        root.add(this.componentsFactory.wrapToSlide(pushbulletPanel, AppThemeColor.ADR_BG, 0, 0, 0, 2));

        this.container.add(this.componentsFactory.wrapToSlide(root));
    }

    @Override
    public void onSave() {
        HideSettingsManager.INSTANCE.apply(applicationSnapshot.getFadeTime(), applicationSnapshot.getMinOpacity(), applicationSnapshot.getMaxOpacity());
        if (!this.applicationSnapshot.getLanguages().equals(this.applicationConfig.get().getLanguages())) {
            SwingUtilities.invokeLater(() -> new OkDialog(null, TranslationKey.language_change_requires_application_restart.value(), this).setVisible(true));
        }
        this.applicationConfig.set(CloneHelper.cloneObject(this.applicationSnapshot));
        this.vulkanConfig.set(CloneHelper.cloneObject(this.vulkanSnapshot));
        PushBulletManager.INSTANCE.reloadAccessToken();
    }

    @Override
    public void restore() {
        this.applicationSnapshot = CloneHelper.cloneObject(this.applicationConfig.get());
        this.removeAll();
        this.onViewInit();
    }
}
