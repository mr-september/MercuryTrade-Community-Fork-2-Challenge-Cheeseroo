package com.mercury.platform.ui.frame.titled;

import com.mercury.platform.TranslationKey;
import com.mercury.platform.core.utils.FileMonitor;
import com.mercury.platform.shared.store.MercuryStoreCore;
import com.mercury.platform.ui.components.fields.font.FontStyle;
import com.mercury.platform.ui.manager.FramesManager;
import com.mercury.platform.ui.misc.AppThemeColor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class GamePathChooser extends AbstractTitledComponentFrame {
    private final Logger logger = LogManager.getLogger(GamePathChooser.class.getSimpleName());
    private final static String defaultGamePath = "C:\\Program Files (x86)\\Grinding Gear Games\\Path of Exile\\";
    private JLabel statusLabel;
    private String gamePath;
    private boolean readyToStart = false;

    public GamePathChooser() {
        super();
        this.processingHideEvent = false;
        this.processEResize = false;
        this.processSEResize = false;
        this.setAlwaysOnTop(true);
    }

    @Override
    public void onViewInit() {
        this.removeHideButton();
        this.add(getChooserPanel(), BorderLayout.CENTER);
        this.add(getMiscPanel(), BorderLayout.PAGE_END);

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
        this.pack();
        this.setVisible(true);
    }

    private JPanel getChooserPanel() {
        JPanel panel = componentsFactory.getTransparentPanel(new BorderLayout());

        this.statusLabel = componentsFactory.getTextLabel("");
        this.statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.statusLabel.setForeground(AppThemeColor.TEXT_IMPORTANT);
        panel.add(this.statusLabel, BorderLayout.CENTER);

        JPanel chooserPanel = componentsFactory.getTransparentPanel(new FlowLayout(FlowLayout.LEFT));

        JTextField textField = componentsFactory.getTextField(defaultGamePath);
        textField.setPreferredSize(new Dimension(450, 26));
        textField.setMinimumSize(new Dimension(450, 26));
        textField.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                repaint();
            }
        });

        chooserPanel.add(textField);

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        JButton selectButton = componentsFactory.getBorderedButton(TranslationKey.select.value());
        selectButton.addActionListener(e -> {
            int returnVal = fileChooser.showOpenDialog(GamePathChooser.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                this.gamePath = fileChooser.getSelectedFile().getPath();
                textField.setText(gamePath);
                this.repaint();
                this.pack();
            }
        });
        chooserPanel.add(selectButton);
        panel.add(chooserPanel, BorderLayout.PAGE_START);
        return panel;
    }

    private JPanel getMiscPanel() {
        JPanel miscPanel = componentsFactory.getTransparentPanel(new FlowLayout(FlowLayout.CENTER));
        JButton cancelButton = componentsFactory.getButton(
                FontStyle.BOLD,
                AppThemeColor.FRAME,
                BorderFactory.createLineBorder(AppThemeColor.BORDER),
                TranslationKey.cancel.value(),
                16f);
        cancelButton.addActionListener(action -> {
            if (!this.readyToStart) {
                System.exit(0);
            }
        });
        cancelButton.setPreferredSize(new Dimension(120, 26));
        JButton saveButton = componentsFactory.getBorderedButton(TranslationKey.save.value());
        saveButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!readyToStart) {
                    if (gamePath == null) {
                        gamePath = defaultGamePath;
                    }
                    if (isValidGamePath(gamePath)) {
                        readyToStart = true;
                        statusLabel.setText(TranslationKey.success.value());
                        saveButton.setEnabled(false);
                        cancelButton.setEnabled(false);
                        statusLabel.setForeground(AppThemeColor.TEXT_SUCCESS);
                        pack();
                        repaint();
                        Timer timer = new Timer(1000, null);
                        timer.addActionListener(actionEvent -> {
                            timer.stop();
                            applicationConfig.get().setGamePath(gamePath + File.separator);
                            MercuryStoreCore.saveConfigSubject.onNext(true);
                            new FileMonitor().start();
                            FramesManager.INSTANCE.start();
                            setVisible(false);
                        });
                        timer.start();
                        logger.info("UI module was started.");
                    } else {
                        statusLabel.setText(TranslationKey.wrong_game_path.value());
                        pack();
                    }
                }
            }
        });
        saveButton.setPreferredSize(new Dimension(120, 26));
        miscPanel.add(saveButton);
        miscPanel.add(cancelButton);
        return miscPanel;
    }

    private boolean isValidGamePath(String gamePath) {
        File client = new File(gamePath + File.separator + "logs" + File.separator + "Client.txt");
        File kakaoClient = new File(gamePath + File.separator + "logs" + File.separator + "KakaoClient.txt");
        return client.exists() || kakaoClient.exists();
    }

    @Override
    public void subscribe() {

    }

    @Override
    protected String getFrameTitle() {
        return TranslationKey.select_game_path.value();
    }
}
