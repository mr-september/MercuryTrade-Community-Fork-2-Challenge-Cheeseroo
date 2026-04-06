package com.mercury.platform.ui.frame.titled;

import com.mercury.platform.TranslationKey;
import com.mercury.platform.shared.config.descriptor.FrameDescriptor;
import com.mercury.platform.shared.config.descriptor.adr.AdrComponentType;
import com.mercury.platform.shared.config.descriptor.adr.AdrDurationComponentDescriptor;
import com.mercury.platform.shared.config.descriptor.adr.AdrProgressBarDescriptor;
import com.mercury.platform.shared.store.MercuryStoreCore;
import com.mercury.platform.ui.components.ComponentsFactory;
import com.mercury.platform.ui.components.fields.font.FontStyle;
import com.mercury.platform.ui.components.panel.settings.MenuPanel;
import com.mercury.platform.ui.components.panel.settings.SettingsLayoutMetrics;
import com.mercury.platform.ui.components.panel.settings.page.GlobalHotkeyGroup;
import com.mercury.platform.ui.manager.FramesManager;
import com.mercury.platform.ui.manager.routing.SettingsPage;
import com.mercury.platform.ui.misc.AppThemeColor;
import com.mercury.platform.ui.misc.ExternalBrowser;
import com.mercury.platform.ui.misc.MercuryStoreUI;
import com.mercury.platform.ui.misc.SwingUiExecutor;
import com.mercury.platform.ui.misc.UpdateCheck;
import com.mercury.platform.ui.misc.note.Note;
import com.mercury.platform.ui.misc.note.NotesLoader;
import org.apache.commons.lang3.SystemUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SettingsFrame extends AbstractTitledComponentFrame {
    private JPanel currentPanel;
    private JPanel root;
    private JPanel navigationPanel;
    private JPanel footerPanel;
    private SettingsLayoutMetrics layoutMetrics;
    private SettingsPage selectedPage = SettingsPage.GENERAL_SETTINGS;

    public SettingsFrame() {
        super();
        this.componentsFactory = ComponentsFactory.INSTANCE.copy();
        this.setAlwaysOnTop(false);
        this.processingHideEvent = false;
        this.processHideEffect = false;
        this.setFocusable(true);
        this.setFocusableWindowState(true);
        this.refreshScaleMetrics();
        this.setPreferredSize(this.layoutMetrics.getFrameSize());
    }

    @Override
    protected void initialize() {
        super.initialize();
        this.refreshScaleMetrics();
        this.applyScaledFrameSize();
    }

    @Override
    public void onViewInit() {
        this.refreshLayout();
    }

    public void setSelectedPage(SettingsPage selectedPage) {
        this.selectedPage = selectedPage;
    }

    public void refreshLayout() {
        this.refreshScaleMetrics();
        this.ensureLayoutContainers();
        this.setBackground(AppThemeColor.FRAME);
        this.navigationPanel.removeAll();
        MenuPanel menuPanel = new MenuPanel(this.componentsFactory, this.selectedPage);
        this.navigationPanel.add(menuPanel, BorderLayout.CENTER);
        this.navigationPanel.add(this.getOperationsButtons(), BorderLayout.PAGE_END);

        this.footerPanel.removeAll();
        this.footerPanel.add(this.getBottomPanel(), BorderLayout.CENTER);

        if (this.currentPanel != null) {
            this.root.removeAll();
            this.root.add(this.currentPanel, BorderLayout.CENTER);
        }

        this.refreshDisplayedLayout();
    }

    @Override
    public void onSizeChange() {
        super.onSizeChange();
        FrameDescriptor frameDescriptor = this.framesConfig.get(this.getClass().getSimpleName());
        Dimension scaledMinimum = this.layoutMetrics.getFrameSize();
        Dimension storedSize = frameDescriptor.getFrameSize();
        this.setPreferredSize(new Dimension(
                Math.max(storedSize.width, scaledMinimum.width),
                Math.max(storedSize.height, scaledMinimum.height)));
    }

    public void setContentPanel(JPanel panel) {
        if (this.root == null) {
            this.refreshLayout();
        }
        this.root.removeAll();
        this.root.add(panel, BorderLayout.CENTER);
        if (!SystemUtils.IS_OS_WINDOWS) {
            this.getContentPane().setBackground(AppThemeColor.SETTINGS_BG);
            this.setBackground(AppThemeColor.SETTINGS_BG);
        }
        this.currentPanel = panel;
        this.refreshDisplayedLayout();
    }

    private JPanel getBottomPanel() {
        JPanel root = this.componentsFactory.getJPanel(new BorderLayout());
        root.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, AppThemeColor.MSG_HEADER_BORDER));
        root.setBackground(AppThemeColor.ADR_FOOTER_BG);

        AdrDurationComponentDescriptor donateDescriptor = new AdrProgressBarDescriptor();
        donateDescriptor.setIconEnable(false);
        donateDescriptor.setDuration(50d);
        donateDescriptor.setSize(this.layoutMetrics.getDonationTrackerSize());
        donateDescriptor.setType(AdrComponentType.PROGRESS_BAR);
        donateDescriptor.setCustomTextEnable(true);
        donateDescriptor.setCustomText("0$");
        donateDescriptor.setFontSize(21);
        donateDescriptor.setLowValueTextColor(AppThemeColor.TEXT_DEFAULT);
        donateDescriptor.setMediumValueTextColor(AppThemeColor.TEXT_DEFAULT);
        donateDescriptor.setDefaultValueTextColor(AppThemeColor.TEXT_DEFAULT);
        donateDescriptor.setBorderColor(AppThemeColor.ADR_DEFAULT_BORDER);
        donateDescriptor.setBackgroundColor(AppThemeColor.FRAME);
        donateDescriptor.setForegroundColor(AppThemeColor.BUTTON);
//        MercuryTracker tracker = new MercuryTracker(donateDescriptor);
//        tracker.setValue(1000);
//        tracker.setPreferredSize(donateDescriptor.getSize());
//        root.add(this.componentsFactory.getTextLabel("Monthly donations:", FontStyle.BOLD, 16), BorderLayout.LINE_START);
//        root.add(this.componentsFactory.wrapToSlide(tracker, AppThemeColor.ADR_FOOTER_BG, 2, 2, 2, 1), BorderLayout.CENTER);
        root.add(this.getSaveButtonPanel(), BorderLayout.LINE_END);
        return root;
    }

    private JPanel getSaveButtonPanel() {
        JPanel root = this.componentsFactory.getTransparentPanel(new GridLayout(1, 0));
        JButton saveButton = this.componentsFactory.getBorderedButton(TranslationKey.save.value(), 16);
        saveButton.addActionListener(e -> {
            MercuryStoreUI.settingsSaveSubject.onNext(true);
            MercuryStoreCore.showingDelaySubject.onNext(true);
            this.hideComponent();
        });
        JButton cancelButton = this.componentsFactory.getButton(
                FontStyle.BOLD,
                AppThemeColor.FRAME,
                BorderFactory.createLineBorder(AppThemeColor.BORDER),
                TranslationKey.cancel.value(),
                16f);
        cancelButton.addActionListener(e -> {
            GlobalHotkeyGroup.INSTANCE.clear();
            MercuryStoreCore.showingDelaySubject.onNext(true);
            this.hideComponent();
            MercuryStoreUI.settingsRestoreSubject.onNext(true);
        });

        JButton donate = this.componentsFactory.getIconButton("app/paypal.png", 70f, AppThemeColor.ADR_FOOTER_BG, "Donate");
        donate.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                ExternalBrowser.open(
                        "https://www.paypal.me/Morph21MT",
                        "Failed to open the MercuryChat donation page.");
            }
        });

        saveButton.setPreferredSize(this.layoutMetrics.getFooterActionButtonSize());
        cancelButton.setPreferredSize(this.layoutMetrics.getFooterActionButtonSize());
        root.add(this.componentsFactory.wrapToSlide(donate, AppThemeColor.HEADER, 2, 2, 2, 2));
        root.add(this.componentsFactory.wrapToSlide(cancelButton, AppThemeColor.HEADER, 2, 2, 2, 2));
        root.add(this.componentsFactory.wrapToSlide(saveButton, AppThemeColor.HEADER, 2, 2, 2, 2));
        return root;
    }

    private JPanel getOperationsButtons() {
        JPanel root = this.componentsFactory.getTransparentPanel(new GridLayout(0, 1,
                this.layoutMetrics.scaleValue(4),
                this.layoutMetrics.scaleValue(2)));
        root.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, AppThemeColor.BORDER));
        JButton openTutorial = this.getOperationButton(TranslationKey.open_tutorial.value(), "app/tutorial.png");
        openTutorial.addActionListener(action -> {
            FramesManager.INSTANCE.hideFrame(SettingsFrame.class);
            FramesManager.INSTANCE.showFrame(NotesFrame.class);
        });
        JButton checkUpdates = this.getOperationButton(TranslationKey.check_for_updates.value(), "app/check-update.png");

        checkUpdates.addActionListener(action -> {
            checkUpdates.setText(TranslationKey.hamsters_are_running.value());
            checkUpdates.setEnabled(false);
            repaint();
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() {
                    UpdateCheck.checkForUpdates(false);
                    return null;
                }

                @Override
                protected void done() {
                    checkUpdates.setText(TranslationKey.check_for_updates.value());
                    checkUpdates.setEnabled(true);
                    SettingsFrame.this.repaint();
                }
            };
            worker.execute();
        });
        JButton openTests = this.getOperationButton(TranslationKey.open_tests.value(), "app/open-tests.png");
        openTests.addActionListener(action -> {
            FramesManager.INSTANCE.hideFrame(SettingsFrame.class);
            FramesManager.INSTANCE.showFrame(TestCasesFrame.class);
//            FramesManager.INSTANCE.preShowFrame(TestCasesFrame.class);
        });
        root.add(this.componentsFactory.wrapToSlide(openTutorial));
        root.add(this.componentsFactory.wrapToSlide(checkUpdates));
        root.add(this.componentsFactory.wrapToSlide(openTests));

        JButton patchNotes = this.componentsFactory.getBorderedButton(TranslationKey.open_patch_notes.value());
        patchNotes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    NotesLoader notesLoader = new NotesLoader();
                    java.util.List<Note> patchNotes = notesLoader.getPatchNotes();
                    if (patchNotes.size() != 0) {
                        NotesFrame patchNotesFrame = new NotesFrame(patchNotes, NotesFrame.NotesType.PATCH);
                        patchNotesFrame.init();
                        patchNotesFrame.showComponent();
                    }
                }
            }
        });
        return root;
    }

    private JButton getOperationButton(String title, String iconPath) {
        JButton button = this.componentsFactory.getButton(title);
        button.setPreferredSize(this.layoutMetrics.getOperationButtonSize());
        button.setForeground(AppThemeColor.TEXT_DEFAULT);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBackground(AppThemeColor.ADR_BG);
        button.setFont(this.componentsFactory.getFont(FontStyle.BOLD, 16f));
        button.setIcon(this.componentsFactory.getIcon(iconPath, 22));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, AppThemeColor.ADR_PANEL_BORDER),
                BorderFactory.createEmptyBorder(
                        this.layoutMetrics.scaleValue(2),
                        this.layoutMetrics.scaleValue(10),
                        this.layoutMetrics.scaleValue(2),
                        this.layoutMetrics.scaleValue(2))));
        return button;
    }

    @Override
    public void subscribe() {
        MercuryStoreUI.settingsRepaintSubject.subscribe(SwingUiExecutor.onEdt(state -> this.refreshDisplayedLayout()));
        MercuryStoreUI.settingsPackSubject.subscribe(SwingUiExecutor.onEdt(state -> this.refreshDisplayedLayout()));
        MercuryStoreUI.adrManagerRepaint.subscribe(SwingUiExecutor.onEdt(state -> this.refreshDisplayedLayout()));
    }

    @Override
    protected String getFrameTitle() {
        return TranslationKey.settings.value();
    }

    @Override
    public void hideComponent() {
        super.hideComponent();
        MercuryStoreCore.showingDelaySubject.onNext(true);
    }

    private void refreshScaleMetrics() {
        this.componentsFactory.setScale(this.scaleConfig.get("other"));
        this.layoutMetrics = new SettingsLayoutMetrics(this.componentsFactory);
    }

    private void applyScaledFrameSize() {
        FrameDescriptor frameDescriptor = this.framesConfig.get(this.getClass().getSimpleName());
        Dimension scaledMinimum = this.layoutMetrics.getFrameSize();
        Dimension storedSize = frameDescriptor == null ? scaledMinimum : frameDescriptor.getFrameSize();
        Dimension effectiveSize = new Dimension(
                Math.max(storedSize.width, scaledMinimum.width),
                Math.max(storedSize.height, scaledMinimum.height));
        this.setPreferredSize(effectiveSize);
        this.setMinimumSize(effectiveSize);
        this.setMaximumSize(effectiveSize);
        this.setSize(effectiveSize);
    }

    private void ensureLayoutContainers() {
        if (this.navigationPanel == null) {
            this.navigationPanel = this.componentsFactory.getJPanel(new BorderLayout());
        }
        if (this.navigationPanel.getParent() != this) {
            this.add(this.navigationPanel, BorderLayout.LINE_START);
        }
        if (this.root == null) {
            this.root = this.componentsFactory.getJPanel(new BorderLayout());
        }
        if (this.root.getParent() != this) {
            this.add(this.root, BorderLayout.CENTER);
        }
        if (this.footerPanel == null) {
            this.footerPanel = this.componentsFactory.getJPanel(new BorderLayout());
        }
        if (this.footerPanel.getParent() != this) {
            this.add(this.footerPanel, BorderLayout.PAGE_END);
        }
    }

    private void refreshDisplayedLayout() {
        this.applyScaledFrameSize();
        this.invalidate();
        this.getContentPane().revalidate();
        this.getContentPane().repaint();
        SwingUtilities.invokeLater(() -> {
            this.getContentPane().revalidate();
            this.getContentPane().repaint();
        });
    }

}
