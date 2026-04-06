package com.mercury.platform.ui.components.panel.settings.page;

import com.google.gson.Gson;
import com.mercury.platform.TranslationKey;
import com.mercury.platform.core.MercuryConstants;
import com.mercury.platform.core.utils.error.ErrorNotifier;
import com.mercury.platform.patches.Change;
import com.mercury.platform.patches.PatchNotes;
import com.mercury.platform.ui.components.fields.font.FontStyle;
import com.mercury.platform.ui.components.fields.font.TextAlignment;
import com.mercury.platform.ui.misc.AppThemeColor;
import com.mercury.platform.ui.misc.ExternalBrowser;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AboutPagePanel extends SettingsPagePanel {
    private static final String PATCH_NOTES_RESOURCE = "notes/patch/patch-notes-new.json";
    private static final String PATCH_NOTES_LOAD_FAILURE = "Failed to load MercuryChat patch notes.";
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
        titlePanel.add(componentsFactory.getTextLabel("MercuryChat", FontStyle.REGULAR, 15));
        panel.add(titlePanel);
        JPanel versionPanel = componentsFactory.getTransparentPanel(new FlowLayout(FlowLayout.LEFT));
        versionPanel.add(componentsFactory.getTextLabel(TranslationKey.app_version.value(": ") + MercuryConstants.APP_VERSION, FontStyle.REGULAR, 15));
        panel.add(versionPanel);

        JLabel githubButton = componentsFactory.getTextLabel(FontStyle.REGULAR, AppThemeColor.TEXT_MESSAGE, TextAlignment.LEFTOP, 16f, "Github");
        githubButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ExternalBrowser.open(
                        "https://github.com/mr-september/MercuryChat/issues",
                        "Failed to open the MercuryChat issues page.");
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
        feedbackPanel.add(componentsFactory.getTextLabel(TranslationKey.feedback_suggestions.value(": "), FontStyle.REGULAR, 15));
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
        return loadPatchNotes(getPatchNotesStream());
    }

    InputStream getPatchNotesStream() {
        return getClass().getClassLoader().getResourceAsStream(PATCH_NOTES_RESOURCE);
    }

    static List<PatchNotes> loadPatchNotes(InputStream inputStream) {
        try (InputStream patchNotesStream = inputStream) {
            if (patchNotesStream == null) {
                ErrorNotifier.notify(
                        PATCH_NOTES_LOAD_FAILURE,
                        new IOException("Patch notes resource is missing: " + PATCH_NOTES_RESOURCE));
                return Collections.emptyList();
            }

            byte[] patchNotesBytes = com.google.common.io.ByteStreams.toByteArray(patchNotesStream);
            PatchNotes[] patchNotes = gson.fromJson(new String(patchNotesBytes, StandardCharsets.UTF_8), PatchNotes[].class);
            if (patchNotes == null || patchNotes.length == 0) {
                return Collections.emptyList();
            }
            return Arrays.asList(patchNotes);
        } catch (IOException | RuntimeException e) {
            ErrorNotifier.notify(PATCH_NOTES_LOAD_FAILURE, e);
            return Collections.emptyList();
        }
    }

    @Override
    public void onSave() {
    }

    @Override
    public void restore() {
        this.initializePage();
    }
}
