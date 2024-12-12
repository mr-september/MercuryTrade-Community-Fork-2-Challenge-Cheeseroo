package com.mercury.platform.ui.components.panel.settings.page;


import com.google.common.io.ByteSource;
import com.google.gson.Gson;
import com.mercury.platform.TranslationKey;
import com.mercury.platform.core.MercuryConstants;
import com.mercury.platform.patches.Change;
import com.mercury.platform.patches.PatchNotes;
import com.mercury.platform.ui.components.fields.font.FontStyle;
import com.mercury.platform.ui.components.fields.font.TextAlignment;
import com.mercury.platform.ui.misc.AppThemeColor;
import org.apache.commons.io.Charsets;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

public class AboutPagePanel extends SettingsPagePanel {
    private final static Logger logger = LogManager.getLogger(AboutPagePanel.class);
    private final static Gson gson = new Gson();

    @Override
    public void onViewInit() {
        super.onViewInit();
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.container.add(this.componentsFactory.wrapToSlide(getInfoPanel()));
        this.container.add(this.componentsFactory.wrapToSlide(getAboutPanel()));
    }

    private JPanel getInfoPanel() {
        JPanel panel = componentsFactory.getTransparentPanel();
        panel.setBackground(AppThemeColor.ADR_BG);
        panel.setBorder(BorderFactory.createLineBorder(AppThemeColor.ADR_PANEL_BORDER));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel titlePanel = componentsFactory.getTransparentPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.add(componentsFactory.getTextLabel("MercuryTrade", FontStyle.REGULAR, 15));
        panel.add(titlePanel);
        JPanel versionPanel = componentsFactory.getTransparentPanel(new FlowLayout(FlowLayout.LEFT));
        versionPanel.add(componentsFactory.getTextLabel(TranslationKey.app_version.value(": ") + MercuryConstants.APP_VERSION, FontStyle.REGULAR, 15));
        panel.add(versionPanel);

        JLabel githubButton = componentsFactory.getTextLabel(FontStyle.REGULAR, AppThemeColor.TEXT_MESSAGE, TextAlignment.LEFTOP, 16f, "Github");
        githubButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("https://github.com/Morph21/MercuryTrade-Community-Fork/issues"));
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        JPanel feedbackPanel = componentsFactory.getTransparentPanel(new FlowLayout(FlowLayout.LEFT));
        feedbackPanel.add(componentsFactory.getTextLabel("Feedback & Suggestions: ", FontStyle.REGULAR, 15));
        feedbackPanel.add(githubButton);

        panel.add(feedbackPanel);
        return panel;
    }

    private JPanel getAboutPanel() {
        JPanel mainPanel = componentsFactory.getTransparentPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        List<PatchNotes> patchNotes = getPatchNotes();
        if (patchNotes == null || patchNotes.isEmpty()) {
            return mainPanel;
        }

        for (PatchNotes item: patchNotes) {
            mainPanel.add(getPatchNotesPanel(item));
        }

        return mainPanel;
    }

    private JPanel getPatchNotesPanel(PatchNotes patchNotes) {
        JPanel panel = componentsFactory.getTransparentPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JTextArea version = componentsFactory.getSimpleTextArea(patchNotes.getVersion(), FontStyle.BOLD, 30);
        version.setForeground(AppThemeColor.TEXT_IMPORTANT);
        panel.add(version);
        panel.add(getChangePanel(patchNotes.getFeatures(), "Features", AppThemeColor.INC_PANEL_ARROW));
        panel.add(getChangePanel(patchNotes.getMinorChanges(), "Minor changes", AppThemeColor.OUT_PANEL_ARROW));
        panel.add(getChangePanel(patchNotes.getFix(), "Fixed", AppThemeColor.TEXT_NICKNAME));
        panel.add(componentsFactory.getSeparator());
        return panel;
    }

    private JPanel getChangePanel(List<Change> changes, String title, Color titleColor) {
        JPanel panel = componentsFactory.getTransparentPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        if (changes == null || changes.isEmpty()) {
            return panel;
        }
        if (StringUtils.isNotEmpty(title)) {
            JTextArea titleArea = componentsFactory.getSimpleTextArea(title, FontStyle.BOLD, 21);
            titleArea.setForeground(titleColor);
            panel.add(titleArea);
        }

        for (Change item : changes) {
            panel.add(componentsFactory.getSimpleTextArea(" * " + item.getChanged(), FontStyle.REGULAR, 16));
        }
        return panel;
    }

    private List<PatchNotes> getPatchNotes() {
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("notes/patch/patch-notes-new.json");
            ByteSource byteSource = new ByteSource() {
                @Override
                public InputStream openStream() throws IOException {
                    return inputStream;
                }
            };

            String text = byteSource.asCharSource(Charsets.UTF_8).read();
            PatchNotes[] patchNotes = gson.fromJson(text, PatchNotes[].class);
            return Arrays.asList(patchNotes);

        } catch (Exception e) {
            logger.error(e);
            return null;
        }
    }

    @Override
    public void onSave() {
    }

    @Override
    public void restore() {
    }
}
