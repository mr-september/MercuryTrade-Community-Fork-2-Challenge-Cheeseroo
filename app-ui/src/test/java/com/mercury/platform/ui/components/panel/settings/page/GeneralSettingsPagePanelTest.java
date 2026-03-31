package com.mercury.platform.ui.components.panel.settings.page;

import com.mercury.platform.shared.config.Configuration;
import com.mercury.platform.shared.config.MercuryConfigManager;
import com.mercury.platform.shared.config.MercuryConfigurationSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeneralSettingsPagePanelTest {

    @BeforeEach
    void setUp() {
        MercuryConfigManager configuration = new MercuryConfigManager(new MercuryConfigurationSource());
        try {
            configuration.load();
        } catch (Exception ignored) {
        }
        Configuration.set(configuration);
    }

    @Test
    void shouldBuildGeneralSettingsAsResponsiveTwoColumnForm() {
        GeneralSettingsPagePanel panel = new GeneralSettingsPagePanel();
        panel.initializePage();

        JPanel formPanel = extractFormPanel(panel);

        assertTrue(formPanel.getLayout() instanceof GridBagLayout);
        assertFalse(formPanel.getLayout() instanceof GridLayout);
    }

    private JPanel extractFormPanel(GeneralSettingsPagePanel panel) {
        JScrollPane scrollPane = (JScrollPane) panel.getComponent(0);
        JPanel container = (JPanel) scrollPane.getViewport().getView();
        JPanel wrapper = (JPanel) container.getComponent(0);
        Component form = ((BorderLayout) wrapper.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        assertTrue(form instanceof JPanel);
        return (JPanel) form;
    }
}
