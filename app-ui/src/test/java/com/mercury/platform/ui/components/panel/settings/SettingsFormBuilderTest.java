package com.mercury.platform.ui.components.panel.settings;

import com.mercury.platform.ui.components.ComponentsFactory;
import com.mercury.platform.ui.misc.AppThemeColor;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SettingsFormBuilderTest {

    @Test
    void shouldBuildResponsiveGridBagFormWithSpacer() {
        ComponentsFactory factory = ComponentsFactory.INSTANCE.copy();
        factory.setScale(1f);
        SettingsLayoutMetrics metrics = new SettingsLayoutMetrics(factory);

        SettingsFormBuilder builder = new SettingsFormBuilder(factory, metrics, AppThemeColor.ADR_BG);
        builder.addRow(new JLabel("Label 1"), new JTextField("Value 1"));
        builder.addRow(new JLabel("Label 2"), new JCheckBox());

        JPanel panel = builder.build();

        assertTrue(panel.getLayout() instanceof GridBagLayout);
        assertEquals(5, panel.getComponentCount());
    }

    @Test
    void shouldRejectAddingRowsAfterBuild() {
        ComponentsFactory factory = ComponentsFactory.INSTANCE.copy();
        SettingsLayoutMetrics metrics = new SettingsLayoutMetrics(factory);

        SettingsFormBuilder builder = new SettingsFormBuilder(factory, metrics, AppThemeColor.ADR_BG);
        builder.addRow(new JLabel("Label"), new JTextField("Value"));
        builder.build();

        assertThrows(IllegalStateException.class, () ->
                builder.addRow(new JLabel("Another Label"), new JTextField("Another Value"))
        );
    }
}
