package com.mercury.platform.ui.frame.titled;

import com.mercury.platform.TranslationKey;
import com.mercury.platform.core.misc.SoundType;
import com.mercury.platform.core.utils.interceptor.MessageInterceptor;
import com.mercury.platform.core.utils.interceptor.filter.MessageMatcher;
import com.mercury.platform.shared.config.Configuration;
import com.mercury.platform.shared.config.configration.PlainConfigurationService;
import com.mercury.platform.shared.config.descriptor.HotKeyType;
import com.mercury.platform.shared.config.descriptor.NotificationSettingsDescriptor;
import com.mercury.platform.shared.config.descriptor.ScannerDescriptor;
import com.mercury.platform.shared.entity.message.PlainMessageDescriptor;
import com.mercury.platform.shared.store.MercuryStoreCore;
import com.mercury.platform.ui.components.fields.font.FontStyle;
import com.mercury.platform.ui.components.fields.font.TextAlignment;
import com.mercury.platform.ui.components.panel.chat.HtmlMessageBuilder;
import com.mercury.platform.ui.misc.AppThemeColor;
import net.jodah.expiringmap.ExpiringMap;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatScannerFrame extends AbstractTitledComponentFrame {
    private static final int FRAME_WIDTH = 350;
    private static final int FRAME_HEIGHT = 300;
    private static final int PANEL_PADDING = 4;
    private static final int TITLE_TOP_PADDING = 2;
    private static final int TITLE_BOTTOM_PADDING = 6;
    private static final int BUTTON_WIDTH = 90;
    private static final int BUTTON_HEIGHT = 24;
    private static final int HEADER_BUTTON_WIDTH = 80;
    private static final int HEADER_BUTTON_HEIGHT = 20;

    private static final String TRADE_CHAT_SEPARATOR = "] $";
    private static final String GLOBAL_CHAT_SEPARATOR = "] #";
    private static final String GENERIC_CHAT_SEPARATOR = "] ";
    private static final Pattern SCANNER_MESSAGE_PATTERN = Pattern.compile("^(\\<.+?\\>)?\\s?(.+?):(.+)$");
    private static final Pattern PLUS_TEXT_PATTERN = Pattern.compile("\\+\\w+");

    private PlainConfigurationService<ScannerDescriptor> scannerService;
    private PlainConfigurationService<NotificationSettingsDescriptor> notificationConfig;
    private MessageInterceptor currentInterceptor;
    private Map<String, String> expiresMessages;
    private HtmlMessageBuilder messageBuilder;
    private boolean running;

    public ChatScannerFrame() {
        super();
        this.processingHideEvent = false;
        this.setFocusableWindowState(true);
        this.setFocusable(true);
        this.setAlwaysOnTop(false);
    }

    @Override
    public void onViewInit() {
        this.scannerService = Configuration.get().scannerConfiguration();
        this.notificationConfig = Configuration.get().notificationConfiguration();
        this.expiresMessages = ExpiringMap.builder()
                .expiration(10, TimeUnit.SECONDS)
                .build();
        this.messageBuilder = new HtmlMessageBuilder();
        this.initHeaderBar();
        JPanel root = componentsFactory.getTransparentPanel(new BorderLayout());
        JPanel setupArea = componentsFactory.getTransparentPanel(new BorderLayout());
        setupArea.setBorder(BorderFactory.createEmptyBorder(
                scaleValue(PANEL_PADDING),
                scaleValue(PANEL_PADDING),
                scaleValue(PANEL_PADDING),
                scaleValue(PANEL_PADDING)));

        JLabel title = componentsFactory.getTextLabel(
                FontStyle.REGULAR,
                AppThemeColor.TEXT_DEFAULT,
                TextAlignment.LEFTOP,
                15f,
                TranslationKey.show_messages_containing_the_following_words.value(":"));
        title.setBorder(BorderFactory.createEmptyBorder(
                scaleValue(TITLE_TOP_PADDING),
                0,
                scaleValue(TITLE_BOTTOM_PADDING),
                0));
        JTextArea words = componentsFactory.getSimpleTextArea(this.scannerService.get().getWords());
        words.setEditable(true);
        words.setCaretColor(AppThemeColor.TEXT_DEFAULT);
        words.setBorder(BorderFactory.createLineBorder(AppThemeColor.HEADER));
        words.setBackground(AppThemeColor.SLIDE_BG);

        JPanel navBar = componentsFactory.getJPanel(
                new FlowLayout(FlowLayout.CENTER, scaleValue(5), scaleValue(5)),
                AppThemeColor.FRAME);
        Dimension buttonSize = this.scaleSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        JButton save = componentsFactory.getBorderedButton(TranslationKey.save.value());
        save.addActionListener(action -> {
            this.scannerService.get().setWords(words.getText());
            MercuryStoreCore.saveConfigSubject.onNext(true);

            String[] split = words.getText().split(",");
            this.performNewStrings(split);
            this.hideComponent();
        });
        JButton cancel = componentsFactory.getBorderedButton(TranslationKey.cancel.value());
        cancel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppThemeColor.BORDER),
                BorderFactory.createLineBorder(AppThemeColor.TRANSPARENT, 3)
        ));
        cancel.setBackground(AppThemeColor.FRAME);
        cancel.addActionListener(action -> {
            hideComponent();
        });
        save.setPreferredSize(buttonSize);
        cancel.setPreferredSize(buttonSize);
        navBar.add(cancel);
        navBar.add(save);

        setupArea.add(title, BorderLayout.PAGE_START);
        setupArea.add(words, BorderLayout.CENTER);

        root.add(setupArea, BorderLayout.CENTER);
        root.add(getMemo(), BorderLayout.LINE_END);

        JPanel propertiesPanel = this.componentsFactory.getJPanel(new BorderLayout(), AppThemeColor.FRAME);

        JLabel quickResponseLabel = this.componentsFactory.getIconLabel(HotKeyType.N_QUICK_RESPONSE.getIconPath(), 18);
        quickResponseLabel.setFont(this.componentsFactory.getFont(FontStyle.REGULAR, 16));
        quickResponseLabel.setForeground(AppThemeColor.TEXT_DEFAULT);
        quickResponseLabel.setBorder(BorderFactory.createEmptyBorder(0, scaleValue(4), 0, 0));
        quickResponseLabel.setText(TranslationKey.response_message.value(": "));
        propertiesPanel.add(quickResponseLabel, BorderLayout.LINE_START);
        JTextField quickResponseField = this.componentsFactory.getTextField(this.scannerService.get().getResponseMessage(), FontStyle.BOLD, 15f);
        quickResponseField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                scannerService.get().setResponseMessage(quickResponseField.getText());
            }
        });
        propertiesPanel.add(this.componentsFactory.wrapToSlide(quickResponseField, AppThemeColor.FRAME, 0, 4, 0, 4), BorderLayout.CENTER);

        root.add(propertiesPanel, BorderLayout.PAGE_END);
        this.add(root, BorderLayout.CENTER);
        this.add(navBar, BorderLayout.PAGE_END);
        this.pack();
    }

    private void initHeaderBar() {
        JPanel root = this.componentsFactory.getJPanel(
                new GridLayout(1, 0, scaleValue(4), 0),
                AppThemeColor.HEADER);
        JLabel statusLabel = componentsFactory.getTextLabel(
                FontStyle.BOLD,
                AppThemeColor.TEXT_DEFAULT,
                TextAlignment.LEFTOP,
                16f,
                this.running ? TranslationKey.status_running.value() : TranslationKey.status_stopped.value());

        JButton processButton = componentsFactory.getBorderedButton(
                this.running ? TranslationKey.stop.value() : TranslationKey.start.value());
        processButton.setFont(this.componentsFactory.getFont(FontStyle.BOLD, 16f));
        processButton.setPreferredSize(this.scaleSize(HEADER_BUTTON_WIDTH, HEADER_BUTTON_HEIGHT));
        processButton.addActionListener(action -> {
            if (this.running) {
                this.running = false;
                processButton.setText(TranslationKey.start.value());
                statusLabel.setText(TranslationKey.status_stopped.value());
                if (this.currentInterceptor != null) {
                    MercuryStoreCore.removeInterceptorSubject.onNext(this.currentInterceptor);
                }
            } else {
                this.running = true;
                processButton.setText(TranslationKey.stop.value());
                statusLabel.setText(TranslationKey.status_running.value());
                this.performNewStrings(this.scannerService.get().getWords().split(","));
            }
        });
        root.add(statusLabel);
        root.add(processButton);
        this.miscPanel.add(root, BorderLayout.CENTER);
    }

    private void performNewStrings(String[] strings) {
        if (this.running) {
            List<String> contains = new ArrayList<>();
            List<String> notContains = new ArrayList<>();
            List<String> startExclusions = new ArrayList<>();

            Arrays.stream(strings).forEach(str -> {
                str = str.toLowerCase().trim();
                if (!str.isEmpty()) {
                    if (str.startsWith("^")) {
                        startExclusions.add(str.substring(1));
                    } else if (str.contains("!")) {
                        notContains.add(str.replace("!", ""));
                    } else {
                        contains.add(str);
                    }
                }
            });
            if (this.currentInterceptor != null) {
                MercuryStoreCore.removeInterceptorSubject.onNext(this.currentInterceptor);
            }
            this.currentInterceptor = new MessageInterceptor() {
                @Override
                protected void process(String stubMessage) {
                    messageBuilder.setChunkStrings(contains);
                    String message = extractScannerPayload(stubMessage);
                    if (!message.isEmpty() && !expiresMessages.containsValue(message)) {
                        String[] parsedMessage = parseScannerMessage(message);
                        if (parsedMessage != null) {
                            PlainMessageDescriptor descriptor = new PlainMessageDescriptor();
                            descriptor.setNickName(parsedMessage[0]);
                            descriptor.setMessage(messageBuilder.build(parsedMessage[1]));
                            populatePlusText(descriptor, parsedMessage[1]);

                            expiresMessages.put(descriptor.getNickName(), message);
                            if (notificationConfig.get().isScannerNotificationEnable()) {
                                MercuryStoreCore.newScannerMessageSubject.onNext(descriptor);
                            }
                            MercuryStoreCore.soundSubject.onNext(SoundType.CHAT_SCANNER);
                        }
                    }
                }

                @Override
                protected MessageMatcher match() {
                    return stubMessage -> {
                        String[] parsedMessage = parseScannerMessage(extractScannerPayload(stubMessage));
                        if (parsedMessage == null || parsedMessage.length < 2) {
                            return false;
                        }
                        String message = StringUtils.defaultString(parsedMessage[1]).trim().toLowerCase();
                        if (message.isEmpty()) {
                            return false;
                        }

                        // Check for start-of-message exclusions
                        for (String startExclusion : startExclusions) {
                            if (message.startsWith(startExclusion)) {
                                return false;
                            }
                        }
                        
                        return notContains.stream().noneMatch(message::contains)
                                && contains.stream().anyMatch(message::contains);
                    };
                }
            };
            MercuryStoreCore.addInterceptorSubject.onNext(this.currentInterceptor);
        }
    }

    @Override
    protected void initialize() {
        super.initialize();
        this.setPreferredSize(this.scaleSize(FRAME_WIDTH, FRAME_HEIGHT));
    }

    private JPanel getMemo() {
        JPanel root = componentsFactory.getTransparentPanel(new BorderLayout());
        JLabel title = componentsFactory.getTextLabel(
                "Memo:",
                FontStyle.REGULAR);
        title.setBorder(BorderFactory.createEmptyBorder(scaleValue(6), 0, scaleValue(2), 0));


        JPanel itemsPanel = componentsFactory.getTransparentPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));

        itemsPanel.add(componentsFactory.getTextLabel(TranslationKey.not_case_sensitive.value(), FontStyle.REGULAR, 17));
        itemsPanel.add(componentsFactory.getTextLabel("! - NOT (!wtb,!wts)", FontStyle.REGULAR, 17));
        itemsPanel.add(componentsFactory.getTextLabel("^ - NOT START (^+,^-)", FontStyle.REGULAR, 17));
        itemsPanel.add(componentsFactory.getTextLabel(", - separator", FontStyle.REGULAR, 17));
        root.add(title, BorderLayout.PAGE_START);
        root.add(itemsPanel, BorderLayout.CENTER);
        return root;
    }

    @Override
    protected String getFrameTitle() {
        return TranslationKey.chat_scanner.value();
    }

    @Override
    public void subscribe() {

    }

    @Override
    protected LayoutManager getFrameLayout() {
        return new BorderLayout();
    }

    static String extractScannerPayload(String stubMessage) {
        if (StringUtils.isBlank(stubMessage)) {
            return "";
        }
        String message = extractAfterSeparator(stubMessage, TRADE_CHAT_SEPARATOR);
        if (message.isEmpty()) {
            message = extractAfterSeparator(stubMessage, GLOBAL_CHAT_SEPARATOR);
        }
        if (message.isEmpty()) {
            message = extractAfterSeparator(stubMessage, GENERIC_CHAT_SEPARATOR);
        }
        return message.isEmpty() ? StringUtils.stripStart(stubMessage, null) : message;
    }

    static String[] parseScannerMessage(String message) {
        Matcher matcher = SCANNER_MESSAGE_PATTERN.matcher(message);
        if (!matcher.find()) {
            return null;
        }
        return new String[]{matcher.group(2), matcher.group(3)};
    }

    private static void populatePlusText(PlainMessageDescriptor descriptor, String originalMessage) {
        Matcher plusTextMatcher = PLUS_TEXT_PATTERN.matcher(originalMessage);
        if (plusTextMatcher.find()) {
            descriptor.setHasPlusText(true);
            descriptor.setPlusText(plusTextMatcher.group());
        } else {
            descriptor.setHasPlusText(false);
            descriptor.setPlusText(null);
        }
    }

    private int scaleValue(int value) {
        return Math.max(1, Math.round(value * this.componentsFactory.getScale()));
    }

    private Dimension scaleSize(int width, int height) {
        return this.componentsFactory.convertSize(new Dimension(width, height));
    }

    private static String extractAfterSeparator(String message, String separator) {
        return StringUtils.strip(StringUtils.substringAfter(message, separator), null);
    }
}
