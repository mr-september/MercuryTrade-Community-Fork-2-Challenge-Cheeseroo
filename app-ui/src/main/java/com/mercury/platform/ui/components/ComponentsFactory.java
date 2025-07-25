package com.mercury.platform.ui.components;

import com.mercury.platform.Languages;
import com.mercury.platform.TranslationKey;
import com.mercury.platform.core.misc.SoundType;
import com.mercury.platform.shared.MainWindowHWNDFetch;
import com.mercury.platform.shared.config.Configuration;
import com.mercury.platform.shared.entity.message.NotificationDescriptor;
import com.mercury.platform.shared.store.MercuryStoreCore;
import com.mercury.platform.ui.components.fields.font.FontStyle;
import com.mercury.platform.ui.components.fields.font.TextAlignment;
import com.mercury.platform.ui.components.fields.style.MercuryComboBoxUI;
import com.mercury.platform.ui.components.fields.style.MercuryScrollBarUI;
import com.mercury.platform.ui.components.panel.misc.ToggleCallback;
import com.mercury.platform.ui.misc.AppThemeColor;
import com.mercury.platform.ui.misc.MercuryStoreUI;
import com.mercury.platform.ui.misc.ToggleAdapter;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

// Display detection imports
import com.mercury.platform.ui.scaling.DisplayDetector;
import com.mercury.platform.ui.scaling.ScalingLookupTable;

/**
 * Factory for each element which uses in application
 */
public class ComponentsFactory {
    private final static Logger log = LogManager.getLogger(ComponentsFactory.class);

    public static ComponentsFactory INSTANCE = ComponentsFactory.ComponentsFactoryHolder.HOLDER_INSTANCE;

    private static class ComponentsFactoryHolder {
        static final ComponentsFactory HOLDER_INSTANCE = new ComponentsFactory();
    }

    private Font BOLD_FONT;
    private Font REGULAR_FONT;
    private Font DEFAULT_FONT;
    private Font CJK_FONT;
    private Font KR_FONT;
    private float scale;
    private ExecutorService executor = Executors.newFixedThreadPool(3);

    private final static Map<TextAttribute, Float> boldAttr = new HashMap<TextAttribute, Float>() {{
        put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
        put(TextAttribute.WIDTH, TextAttribute.WIDTH_SEMI_CONDENSED);
    }};

    private final static Map<TextAttribute, Float> regularAttr = new HashMap<TextAttribute, Float>() {{
        put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_REGULAR);
        put(TextAttribute.WIDTH, TextAttribute.WIDTH_SEMI_CONDENSED);
    }};

    private ComponentsFactory() {
        this.scale = 1.0f;
        loadFonts();

        UIManager.put("ComboBox.selectionBackground", AppThemeColor.HEADER);
        UIManager.put("ComboBox.selectionForeground", AppThemeColor.ADR_POPUP_BG);
        UIManager.put("ComboBox.disabledForeground", AppThemeColor.ADR_FOOTER_BG);
    }

    // Cache for display detection to avoid repeated expensive operations
    private volatile DisplayDetector.DisplayInfo cachedDisplayInfo = null;
    private volatile long lastDisplayDetectionTime = 0;
    private static final long DISPLAY_CACHE_DURATION_MS = 30000; // 30 seconds cache

    /**
     * Detects the current display configuration and provides scaling recommendations.
     * This method performs detection but does not automatically apply scaling.
     * Results are cached for 30 seconds to improve performance.
     * 
     * @return DisplayDetector.DisplayInfo containing the detected display characteristics
     */
    public DisplayDetector.DisplayInfo detectDisplayConfiguration() {
        long currentTime = System.currentTimeMillis();
        
        // Return cached result if it's still valid
        if (cachedDisplayInfo != null && 
            (currentTime - lastDisplayDetectionTime) < DISPLAY_CACHE_DURATION_MS) {
            return cachedDisplayInfo;
        }
        
        // Perform new detection and cache result
        synchronized (this) {
            // Double-check in case another thread updated it
            if (cachedDisplayInfo != null && 
                (currentTime - lastDisplayDetectionTime) < DISPLAY_CACHE_DURATION_MS) {
                return cachedDisplayInfo;
            }
            
            cachedDisplayInfo = DisplayDetector.detectPrimaryDisplay();
            lastDisplayDetectionTime = currentTime;
            return cachedDisplayInfo;
        }
    }

    /**
     * Forces a refresh of the cached display configuration.
     * Call this when you know the display setup has changed.
     */
    public void refreshDisplayConfiguration() {
        synchronized (this) {
            cachedDisplayInfo = null;
            lastDisplayDetectionTime = 0;
        }
    }

    /**
     * Gets scaling recommendations for the current display configuration.
     * This method uses the lookup table to provide optimal scaling values.
     * 
     * @return ScalingLookupTable.ScalingRecommendation with recommended scaling values
     */
    public ScalingLookupTable.ScalingRecommendation getScalingRecommendations() {
        try {
            DisplayDetector.DisplayInfo displayInfo = detectDisplayConfiguration();
            ScalingLookupTable.DisplayConfig displayConfig = displayInfo.toDisplayConfig();
            return ScalingLookupTable.calculateRecommendation(displayConfig);
        } catch (Exception e) {
            // Fallback to safe default if detection fails
            System.err.println("[ComponentsFactory] Failed to get scaling recommendations, using defaults: " + e.getMessage());
            return new ScalingLookupTable.ScalingRecommendation(
                1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 
                "Fallback recommendation due to detection failure"
            );
        }
    }

    /**
     * Detects all available displays in a multi-monitor setup.
     * 
     * @return Array of DisplayInfo objects for all detected displays
     */
    public DisplayDetector.DisplayInfo[] detectAllDisplays() {
        return DisplayDetector.detectAllDisplays();
    }

    /**
     * Gets a formatted string with current display information for debugging.
     * This method provides human-readable display configuration details.
     * 
     * @return Formatted string containing display information and scaling recommendations
     */
    public String getDisplayConfigurationInfo() {
        DisplayDetector.DisplayInfo displayInfo = detectDisplayConfiguration();
        ScalingLookupTable.ScalingRecommendation recommendation = getScalingRecommendations();
        
        StringBuilder info = new StringBuilder();
        info.append("=== Display Configuration ===\n");
        info.append(displayInfo.toString()).append("\n");
        info.append("Current Application Scale: ").append((scale * 100)).append("%\n");
        info.append("\n=== Scaling Recommendations ===\n");
        info.append("Base Scale: ").append((recommendation.baseScale * 100)).append("%\n");
        info.append("Notification Scale: ").append((recommendation.notificationScale * 100)).append("%\n");
        info.append("Taskbar Scale: ").append((recommendation.taskbarScale * 100)).append("%\n");
        info.append("Item Cell Scale: ").append((recommendation.itemCellScale * 100)).append("%\n");
        info.append("Other Scale: ").append((recommendation.otherScale * 100)).append("%\n");
        info.append("Reasoning: ").append(recommendation.reasoning).append("\n");
        
        return info.toString();
    }

    /**
     * Checks if the current scaling is optimal for the detected display.
     * 
     * @return true if current scale is within 10% of recommended base scale
     */
    public boolean isCurrentScalingOptimal() {
        ScalingLookupTable.ScalingRecommendation recommendation = getScalingRecommendations();
        float recommendedScale = recommendation.baseScale;
        float tolerance = 0.1f; // 10% tolerance
        
        return Math.abs(scale - recommendedScale) <= tolerance;
    }

    /**
     * Loading all application fonts
     */
    private void loadFonts() {
        try {

            Instant start = Instant.now();
            Callable<Font> baseCallable = () -> Font.createFont(Font.TRUETYPE_FONT, getClass().getClassLoader().getResourceAsStream("font/NotoSans-VariableFont.ttf"));
            Callable<Font> cjkCallable = () -> Font.createFont(Font.TRUETYPE_FONT, getClass().getClassLoader().getResourceAsStream("font/GoNotoCJKCore.ttf"));
            Callable<Font> krCallable = () -> Font.createFont(Font.TRUETYPE_FONT, getClass().getClassLoader().getResourceAsStream("font/HayashiSerif.ttf"));
            Future<Font> cjkFuture = executor.submit(cjkCallable);
            Future<Font> krFuture = executor.submit(krCallable);
            Future<Font> baseFuture = executor.submit(baseCallable);

            Font base = baseFuture.get();
            CJK_FONT = cjkFuture.get();
            KR_FONT = krFuture.get();

            Instant load = Instant.now();

            DEFAULT_FONT = base.deriveFont(regularAttr);
            BOLD_FONT = base.deriveFont(boldAttr);
            REGULAR_FONT = base.deriveFont(regularAttr);

            DEFAULT_FONT = DEFAULT_FONT.deriveFont(16f * scale);
            BOLD_FONT = BOLD_FONT.deriveFont(16f * scale);
            REGULAR_FONT = REGULAR_FONT.deriveFont(16f * scale);
            CJK_FONT = CJK_FONT.deriveFont(16f * scale);
            KR_FONT = KR_FONT.deriveFont(16f * scale);

            GraphicsEnvironment genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
            genv.registerFont(DEFAULT_FONT);
            genv.registerFont(BOLD_FONT);
            genv.registerFont(REGULAR_FONT);
            genv.registerFont(CJK_FONT);
            genv.registerFont(KR_FONT);
            Instant end = Instant.now();
            System.out.println("loading fonts from file: " + (load.toEpochMilli() - start.toEpochMilli()) + " ms");
            System.out.println("processing fonts took: " + (end.toEpochMilli() - load.toEpochMilli()) + " ms");
            executor = null;

        } catch (Exception e) {
            log.error(e);
        }
    }

    public JButton getButton(FontStyle fontStyle, Color background, Border border, String text, float fontSize) {
        return getButton(fontStyle, background, border, text, fontSize, null);
    }

    /**
     * Get button with custom params
     *
     * @param fontStyle  path of exile font type
     * @param background button background
     * @param border     button border
     * @param text       default text
     * @param fontSize   font size
     * @return JButton object
     */
    public JButton getButton(FontStyle fontStyle, Color background, Border border, String text, float fontSize, String tooltip) {
        JButton button = new JButton(text) {
            @Override
            protected void paintBorder(Graphics g) {
                if (!this.getModel().isPressed()) {
                    super.paintBorder(g);
                }
            }

            @Override
            public JToolTip createToolTip() {
                JToolTip tip = ComponentsFactory.this.createTooltip(tooltip);
                return tip;
            }
        };
        button.setBackground(background);
        button.setForeground(AppThemeColor.TEXT_DEFAULT);
        button.setFocusPainted(false);
        button.addMouseListener(new MouseAdapter() {
            Border prevBorder;

            @Override
            public void mouseEntered(MouseEvent e) {
                this.prevBorder = button.getBorder();
                CompoundBorder compoundBorder = BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(AppThemeColor.ADR_SELECTED_BORDER, 1),
                        BorderFactory.createLineBorder(button.getBackground(), 3)
                );
                button.setBorder(compoundBorder);
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBorder(prevBorder);
                button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        button.addActionListener(action -> {
            MercuryStoreCore.soundSubject.onNext(SoundType.CLICKS);
        });
        button.setFont(getSelectedFont(fontStyle, text).deriveFont(scale * fontSize));
        button.setBorder(border);
        button.addChangeListener(e -> {
            if (!button.getModel().isPressed()) {
                button.setBackground(button.getBackground());
            }
        });
        return button;
    }

    public JMenuItem getMenuItem(String text, String tooltip) {
        CompoundBorder compoundBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppThemeColor.TRANSPARENT, 1),
                BorderFactory.createLineBorder(AppThemeColor.BUTTON, 3)
        );

        return getMenuItem(FontStyle.BOLD, AppThemeColor.BUTTON, compoundBorder, text, scale * 16f, tooltip);
    }

    public JMenuItem getMenuItem(FontStyle fontStyle, Color background, Border border, String text, float fontSize, String tooltip) {
        JMenuItem menuItem = new JMenuItem(text) {
            @Override
            protected void paintBorder(Graphics g) {
                if (!this.getModel().isPressed()) {
                    super.paintBorder(g);
                }
            }

            @Override
            public JToolTip createToolTip() {
                JToolTip tip = ComponentsFactory.this.createTooltip(tooltip);
                return tip;
            }
        };
        menuItem.setBackground(background);
        menuItem.setForeground(AppThemeColor.TEXT_DEFAULT);
        menuItem.setFocusPainted(false);
        menuItem.addMouseListener(new MouseAdapter() {
            Border prevBorder;

            @Override
            public void mouseEntered(MouseEvent e) {
                this.prevBorder = menuItem.getBorder();
                CompoundBorder compoundBorder = BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(AppThemeColor.ADR_SELECTED_BORDER, 1),
                        BorderFactory.createLineBorder(menuItem.getBackground(), 3)
                );
                menuItem.setBorder(compoundBorder);
                menuItem.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                menuItem.setBorder(prevBorder);
                menuItem.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        menuItem.addActionListener(action -> {
            MercuryStoreCore.soundSubject.onNext(SoundType.CLICKS);
        });
        menuItem.setFont(getSelectedFont(fontStyle, text).deriveFont(scale * fontSize));
        menuItem.setBorder(border);
        menuItem.addChangeListener(e -> {
            if (!menuItem.getModel().isPressed()) {
                menuItem.setBackground(menuItem.getBackground());
            }
        });
        return menuItem;
    }

    /**
     * Get button with default properties
     *
     * @param text text on button
     * @return Default app button
     */
    public JButton getButton(String text, String tooltip) {
        CompoundBorder compoundBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppThemeColor.TRANSPARENT, 1),
                BorderFactory.createLineBorder(AppThemeColor.BUTTON, 3)
        );

        return getButton(FontStyle.BOLD, AppThemeColor.BUTTON, compoundBorder, text, scale * 14f, tooltip);
    }

    /**
     * Get button with default properties
     *
     * @param text text on button
     * @return Default app button
     */
    public JButton getButton(String text) {
        return getButton(text, null);
    }

    /**
     * Get bordered button with default properties.
     *
     * @param text text on button
     * @return Default bordered app button
     */
    public JButton getBorderedButton(String text) {
        CompoundBorder compoundBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppThemeColor.BORDER, 1),
                BorderFactory.createLineBorder(AppThemeColor.BUTTON, 3)
        );
        return getButton(FontStyle.BOLD, AppThemeColor.BUTTON, compoundBorder, text, scale * 13f);
    }

    public JButton getBorderedButton(String text, float fontSize) {
        CompoundBorder compoundBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppThemeColor.BORDER, 1),
                BorderFactory.createLineBorder(AppThemeColor.BUTTON, 3)
        );
        return getButton(FontStyle.BOLD, AppThemeColor.BUTTON, compoundBorder, text, scale * fontSize);
    }

    public JButton getBorderedButton(String text,
                                     float fontSize,
                                     Color background,
                                     Color outerBorderColor,
                                     Color innerBorderColor) {
        CompoundBorder compoundBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(outerBorderColor, 1),
                BorderFactory.createLineBorder(innerBorderColor, 3)
        );
        return getButton(FontStyle.BOLD, background, compoundBorder, text, scale * fontSize);
    }

    public Component setUpToggleCallbacks(Component button,
                                          ToggleCallback firstState,
                                          ToggleCallback secondState,
                                          boolean initialState) {
        button.addMouseListener(createListenerForToggleCallbacks(button, firstState, secondState, initialState));
        return button;
    }

    public ToggleAdapter createListenerForToggleCallbacks(Component button,
                                                          ToggleCallback firstState,
                                                          ToggleCallback secondState,
                                                          boolean initialState) {
        ToggleAdapter listener = new ToggleAdapter(firstState, secondState, initialState);
        return listener;
    }

    public JButton getIconButton(String iconPath, float iconSize, Color background, String tooltip) {
        int i = iconPath.lastIndexOf("/");
        String temp = iconPath.substring(i + 1);
        temp = temp.replace(".png", "");
        return getIconButton(iconPath, iconSize, background, tooltip, temp);
    }


    /**
     * Get button with icon
     *
     * @param iconPath icon path from maven resources
     * @param iconSize icon size
     * @return JButton object with icon
     */
    public JButton getIconButton(String iconPath,
                                 float iconSize,
                                 Color background,
                                 String tooltip,
                                 String textIfImgNotFound) {
        JButton button = new JButton("") {
            @Override
            protected void paintBorder(Graphics g) {
                if (!this.getModel().isPressed()) {
                    super.paintBorder(g);
                }
            }

            @Override
            public JToolTip createToolTip() {
                JToolTip tip = ComponentsFactory.this.createTooltip(tooltip);
                //tip.setDoubleBuffered(true);
                return tip;
            }

        };
        button.setBackground(background);

        button.addChangeListener(e -> {
            if (!button.getModel().isPressed()) {
                button.setBackground(button.getBackground());
            }
        });
        if (!tooltip.isEmpty()) {
            button.setToolTipText(wrapTextWithPadding(tooltip));
        }

        button.setFocusPainted(false);

        button.setFocusable(true);

        button.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        button.addActionListener(action -> {
            MercuryStoreCore.soundSubject.onNext(SoundType.CLICKS);
            button.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        });
        button.addMouseListener(new MouseAdapter() {
            Border prevBorder;

            @Override
            public void mouseEntered(MouseEvent e) {
                prevBorder = button.getBorder();
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(AppThemeColor.ADR_SELECTED_BORDER),
                        BorderFactory.createEmptyBorder(3, 3, 3, 3)));
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBorder(prevBorder);
                button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        button.setBorder(BorderFactory.createLineBorder(AppThemeColor.TRANSPARENT, 4));
        button.setVerticalAlignment(SwingConstants.CENTER);
        BufferedImage icon = null;
        try {
            URL resource = getClass().getClassLoader().getResource(iconPath);
            if (resource != null) {
                BufferedImage buttonIcon = ImageIO.read(resource);
                icon = Scalr.resize(buttonIcon, (int) (scale * iconSize));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (icon != null) {
            button.setIcon(new ImageIcon(icon));
        } else {
            button.setText(textIfImgNotFound);
            button.setForeground(AppThemeColor.TEXT_DEFAULT);
            button.setFont(getFont(FontStyle.REGULAR, scale * 14));
        }

        return button;
    }

    public JToolTip createTooltip(String text) {
        JToolTip toolTip = new JToolTip();
        toolTip.setBackground(AppThemeColor.SETTINGS_BG);
        toolTip.setForeground(AppThemeColor.TEXT_DEFAULT);
        toolTip.setFont(getSelectedFont(FontStyle.REGULAR, text, scale * 16f));
        toolTip.setBorder(BorderFactory.createLineBorder(AppThemeColor.BORDER));
        return toolTip;
    }

    public String wrapTextWithPadding(String text) {
        StringBuilder b = new StringBuilder();
        b.append("<html>");
        b.append("<div style=\"padding: 2px 4px 2px 4px;\">");
        b.append(text);
        b.append("</div>");
        b.append("</html>");
        return b.toString();
    }

    public JButton getIconifiedTransparentButton(String iconPath, String tooltip) {
        JButton iconButton = getIconButton(iconPath, 10, AppThemeColor.FRAME_RGB, tooltip);
        iconButton.setIcon(getImage(iconPath));
        return iconButton;
    }

    /**
     * Get bordered default button with icon
     *
     * @param iconPath icon path from maven resources
     * @param iconSize icon size
     * @return bordered JButton with icon
     */
    public JButton getBorderedIconButton(String iconPath, int iconSize, String tooltip) {
        CompoundBorder compoundBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppThemeColor.BORDER, 1),
                BorderFactory.createLineBorder(AppThemeColor.BUTTON, 2)
        );
        JButton iconButton = getIconButton(iconPath, iconSize, AppThemeColor.FRAME_ALPHA, tooltip);
        iconButton.setBorder(BorderFactory.createLineBorder(AppThemeColor.BUTTON, 2));
        return iconButton;
    }

    /**
     * Get icon button with custom size
     *
     * @param iconPath   icon path from maven resources
     * @param iconSize   icon size
     * @param buttonSize button size (its only preferred)
     * @return JButton with icon
     */
    public JButton getIconButton(String iconPath, int iconSize, Dimension buttonSize, String tooltip) {
        JButton iconButton = getIconButton(iconPath, iconSize, AppThemeColor.FRAME_ALPHA, tooltip);
        iconButton.setPreferredSize(buttonSize); //todo scale
        iconButton.setSize(buttonSize);
        return iconButton;
    }

    /**
     * Get label with custom params
     *
     * @param fontStyle path of exile font type
     * @param frColor   foreground color
     * @param alignment font alignment
     * @param size      font size
     * @param text      initial text on font
     * @return JLabel object
     */
    public JLabel getTextLabel(FontStyle fontStyle, Color frColor, TextAlignment alignment, float size, String text) {
        JLabel label = new JLabel(text);
        label.setFont(getSelectedFont(fontStyle, text).deriveFont(scale * size));
        label.setForeground(frColor);
        Border border = label.getBorder();
        label.setBorder(new CompoundBorder(border, new EmptyBorder(0, 5, 0, 5)));

        if (alignment != null) {
            switch (alignment) {
                case LEFTOP: {
                    label.setAlignmentX(Component.LEFT_ALIGNMENT);
                    label.setAlignmentY(Component.TOP_ALIGNMENT);
                }
                break;
                case RIGHTOP: {
                    label.setAlignmentX(Component.RIGHT_ALIGNMENT);
                    label.setAlignmentY(Component.TOP_ALIGNMENT);
                }
                case CENTER: {
                    label.setAlignmentX(Component.CENTER_ALIGNMENT);
                    label.setAlignmentY(Component.TOP_ALIGNMENT);
                }
                break;
            }
        }
        return label;
    }

    public JLabel getTextLabel(FontStyle fontStyle,
                               Color frColor,
                               TextAlignment alignment,
                               float size,
                               Border border,
                               String text) {
        JLabel textLabel = getTextLabel(fontStyle, frColor, alignment, size, text);
        textLabel.setBorder(border);
        return textLabel;
    }

    /**
     * Get default label
     *
     * @param text font text
     * @return JLabel object
     */
    public JLabel getTextLabel(String text) {
        return getTextLabel(FontStyle.BOLD, AppThemeColor.TEXT_DEFAULT, TextAlignment.LEFTOP, scale * 15f, text);
    }

    public JLabel getTextLabel(String text, FontStyle style) {
        return getTextLabel(style, AppThemeColor.TEXT_DEFAULT, TextAlignment.LEFTOP, scale * 15f, text);
    }

    public JLabel getTextLabel(String text, FontStyle style, Color color) {
        return getTextLabel(style, color, TextAlignment.LEFTOP, scale * 15f, text);
    }

    public JLabel getTextLabel(String text, FontStyle style, float size) {
        return getTextLabel(style, AppThemeColor.TEXT_DEFAULT, TextAlignment.LEFTOP, size, text);
    }

    public JLabel getTextLabel(String text, FontStyle style, Color color, float size) {
        return getTextLabel(style, color, TextAlignment.LEFTOP, size, text);
    }


    /**
     * Get label with icon
     *
     * @param iconPath icon path from maven resources
     * @param size     icon size
     * @return JLabel object with icon
     */
    public JLabel getIconLabel(String iconPath, int size) {
        JLabel iconLabel = new JLabel();
        try {
            iconLabel.setIcon(getIcon(iconPath, (int) (scale * size)));
        } catch (Exception e) {
            return getTextLabel(StringUtils.substringBetween(iconPath, "/", "."));
        }
        return iconLabel;
    }

    public JLabel getIconLabel(String iconPath, int size, int aligment) {
        JLabel iconLabel = new JLabel();
        try {
            iconLabel.setIcon(getIcon(iconPath, (int) (scale * size)));
        } catch (Exception e) {
            return getTextLabel(StringUtils.substringBetween(iconPath, "/", "."));
        }
        iconLabel.setHorizontalAlignment(aligment);
        return iconLabel;
    }

    public JLabel getIconLabel(URL url, int size) {
        JLabel iconLabel = new JLabel();
        try {
            iconLabel.setIcon(getIcon(url, (int) (scale * size)));
        } catch (Exception e) {
            return getTextLabel(StringUtils.substringBetween(url.getPath(), "/", "."));
        }
        return iconLabel;
    }

    public JLabel getIconLabel(String iconPath, int size, int alignment, String tooltip) {
        JLabel iconLabel = new JLabel() {
            @Override
            public JToolTip createToolTip() {
                return ComponentsFactory.this.createTooltip(tooltip);
            }
        };
        iconLabel.setToolTipText(wrapTextWithPadding(tooltip));
        try {
            iconLabel.setIcon(getIcon(iconPath, (int) (scale * size)));
        } catch (Exception e) {
            return getTextLabel(StringUtils.substringBetween(iconPath, "/", "."));
        }
        iconLabel.setHorizontalAlignment(alignment);
        return iconLabel;
    }

    public JLabel getIconLabel(String iconPath) {
        JLabel iconLabel = new JLabel();
        try {
            BufferedImage buttonIcon = ImageIO.read(getClass().getClassLoader().getResource(iconPath));
            iconLabel.setIcon(new ImageIcon(buttonIcon));
        } catch (Exception e) {
            return getTextLabel(StringUtils.substringBetween(iconPath, "/", "."));
        }
        return iconLabel;
    }

    public JTextField getTextField(String text) {
        JTextField textField = getTextField(text, FontStyle.REGULAR, scale * 16);
        textField.setFont(getSelectedFont(FontStyle.REGULAR, text));
        return textField;
    }

    public JPasswordField getPasswordField(String text) {
        JPasswordField passwordField = getPasswordField(text, FontStyle.REGULAR, scale * 16);
        passwordField.setFont(getSelectedFont(FontStyle.REGULAR, text));
        return passwordField;
    }

    public JFormattedTextField getIntegerTextField(Integer min, Integer max, Integer value) {
        NumberFormat format = NumberFormat.getInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(min);
        formatter.setMaximum(max);
        formatter.setAllowsInvalid(true);
        formatter.setCommitsOnValidEdit(false);

        JFormattedTextField field = new JFormattedTextField(formatter);
        field.setValue(value);
        field.setFont(getSelectedFont(FontStyle.REGULAR, null).deriveFont(scale * 18));
        field.setFocusLostBehavior(JFormattedTextField.PERSIST);
        field.setForeground(AppThemeColor.TEXT_DEFAULT);
        field.setCaretColor(AppThemeColor.TEXT_DEFAULT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppThemeColor.BORDER, 1),
                BorderFactory.createLineBorder(AppThemeColor.TRANSPARENT, 3)
        ));
        field.setBackground(AppThemeColor.HEADER);
        return field;
    }

    public JTextField getTextField(String text, FontStyle style, float fontSize) {
        JTextField textField = new JTextField(text);
        textField.setFont(getSelectedFont(style, text).deriveFont(scale * fontSize));
        textField.setForeground(AppThemeColor.TEXT_DEFAULT);
        textField.setCaretColor(AppThemeColor.TEXT_DEFAULT);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppThemeColor.BORDER, 1),
                BorderFactory.createLineBorder(AppThemeColor.TRANSPARENT, 3)
        ));
        textField.setBackground(AppThemeColor.HEADER);
        return textField;
    }

    public JPasswordField getPasswordField(String text, FontStyle style, float fontSize) {
        JPasswordField passwordField = new JPasswordField(text);
        passwordField.setFont(getSelectedFont(style, text).deriveFont(scale * fontSize));
        passwordField.setForeground(AppThemeColor.TEXT_DEFAULT);
        passwordField.setCaretColor(AppThemeColor.TEXT_DEFAULT);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppThemeColor.BORDER, 1),
                BorderFactory.createLineBorder(AppThemeColor.TRANSPARENT, 3)
        ));
        passwordField.setBackground(AppThemeColor.HEADER);
        return passwordField;
    }

    public JCheckBoxMenuItem checkBoxMenuItem(boolean value, String label) {
        CompoundBorder border = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppThemeColor.TRANSPARENT, 1),
                BorderFactory.createLineBorder(AppThemeColor.BUTTON, 3)
        );
        JCheckBoxMenuItem checkBoxMenuItem = new JCheckBoxMenuItem(label, value) {
            @Override
            protected void paintBorder(Graphics g) {
                if (!this.getModel().isPressed()) {
                    super.paintBorder(g);
                }
            }
        };
        checkBoxMenuItem.setBackground(AppThemeColor.BUTTON);
        checkBoxMenuItem.setForeground(AppThemeColor.TEXT_DEFAULT);
        checkBoxMenuItem.setFont(getFontByLang(label, FontStyle.REGULAR));

        checkBoxMenuItem.addMouseListener(new MouseAdapter() {
            Border prevBorder;

            @Override
            public void mouseEntered(MouseEvent e) {
                this.prevBorder = checkBoxMenuItem.getBorder();
                CompoundBorder compoundBorder = BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(AppThemeColor.ADR_SELECTED_BORDER, 1),
                        BorderFactory.createLineBorder(checkBoxMenuItem.getBackground(), 3)
                );
                checkBoxMenuItem.setBorder(compoundBorder);
                checkBoxMenuItem.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                checkBoxMenuItem.setBorder(prevBorder);
                checkBoxMenuItem.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        checkBoxMenuItem.addActionListener(action -> {
            MercuryStoreCore.soundSubject.onNext(SoundType.CLICKS);
        });
        checkBoxMenuItem.setBorder(border);
        checkBoxMenuItem.addChangeListener(e -> {
            if (!checkBoxMenuItem.getModel().isPressed()) {
                checkBoxMenuItem.setBackground(checkBoxMenuItem.getBackground());
            }
        });

        return checkBoxMenuItem;
    }

    public JCheckBox getCheckBox(String tooltip) {
        JCheckBox checkBox = new JCheckBox() {
            @Override
            public JToolTip createToolTip() {
                return ComponentsFactory.this.createTooltip(tooltip);
            }
        };
        checkBox.setToolTipText(wrapTextWithPadding(tooltip));
        checkBox.setFocusPainted(false);
        checkBox.setBackground(AppThemeColor.TRANSPARENT);
//        checkBox.setUI(new WindowsButtonUI());
        return checkBox;
    }

    public JCheckBox getCheckBox(boolean value) {
        JCheckBox checkBox = new JCheckBox();
        checkBox.setSelected(value);
//        checkBox.setUI(new WindowsButtonUI());
        checkBox.setFocusPainted(false);
        checkBox.setBackground(AppThemeColor.TRANSPARENT);
        return checkBox;
    }

    public JCheckBox getCheckBox(boolean value, String tooltip) {
        JCheckBox checkBox = this.getCheckBox(tooltip);
        checkBox.setSelected(value);
        return checkBox;
    }

    public Font getFontByLang(String text, FontStyle style) {
        return getSelectedFont(style, text);
    }

    public JPanel getSliderSettingsPanel(JLabel titleLabel, JLabel countLabel, JSlider slider) {
        Dimension elementsSize = convertSize(new Dimension((int) scale * 250, 30));
        Dimension countSize = convertSize(new Dimension(40, 30));
        titleLabel.setPreferredSize(elementsSize);
        slider.setPreferredSize(elementsSize);
        countLabel.setPreferredSize(countSize);
        JPanel panel = getTransparentPanel(new GridBagLayout());
        panel.setBackground(AppThemeColor.ADR_BG);
        GridBagConstraints titleGc = new GridBagConstraints();
        GridBagConstraints countGc = new GridBagConstraints();
        GridBagConstraints sliderGc = new GridBagConstraints();
        titleGc.weightx = 0.5f;
        countGc.weightx = 0.1f;
        sliderGc.weightx = 0.4f;
        titleGc.fill = GridBagConstraints.HORIZONTAL;
        countGc.fill = GridBagConstraints.HORIZONTAL;
        sliderGc.fill = GridBagConstraints.HORIZONTAL;
        titleGc.anchor = GridBagConstraints.NORTHWEST;
        countGc.anchor = GridBagConstraints.NORTHWEST;
        sliderGc.anchor = GridBagConstraints.NORTHWEST;
        titleGc.gridx = 0;
        countGc.gridx = 1;
        sliderGc.gridx = 2;

        panel.add(titleLabel, titleGc);
        panel.add(countLabel, countGc);
        panel.add(slider, sliderGc);
        return panel;
    }

    public JPanel getSettingsPanel(JLabel titleLabel, Component component) {
        JPanel panel = getTransparentPanel(new GridLayout(1, 2));
        panel.setBackground(AppThemeColor.ADR_BG);
        panel.add(titleLabel);
        panel.add(component);
        return panel;
    }

    public Font getFont(FontStyle style, float fontSize) {
        return getSelectedFont(style, null).deriveFont(scale * fontSize);
    }

    public JComboBox getComboBox(String[] child) {
        JComboBox comboBox = new JComboBox<>(child);
        comboBox.setBackground(AppThemeColor.HEADER);
        comboBox.setForeground(AppThemeColor.TEXT_DEFAULT);
        comboBox.setFont(getSelectedFont(FontStyle.BOLD, child[0], 16f));
        comboBox.setBorder(BorderFactory.createLineBorder(AppThemeColor.BORDER, 1));
        comboBox.setUI(MercuryComboBoxUI.createUI(comboBox));
        return comboBox;
    }

    public <T> JComboBox<T> getComboBox(T[] child) {
        JComboBox comboBox = new JComboBox<>(child);
        comboBox.setBackground(AppThemeColor.HEADER);
        comboBox.setForeground(AppThemeColor.TEXT_DEFAULT);
        comboBox.setFont(getSelectedFont(FontStyle.BOLD, child[0].toString(), 16f));
        comboBox.setBorder(BorderFactory.createLineBorder(AppThemeColor.BORDER, 1));
        comboBox.setUI(MercuryComboBoxUI.createUI(comboBox));
        return comboBox;
    }

    public JSlider getSlider(int min, int max, int value) {
        JSlider slider = new JSlider(JSlider.HORIZONTAL, min, max, value);
        slider.setMajorTickSpacing(10);
        slider.setMinorTickSpacing(1);
//        slider.setPaintLabels(true);
//        slider.setUI(new WindowsSliderUI(slider));
        slider.setForeground(AppThemeColor.TEXT_DEFAULT);
        slider.setFont(getSelectedFont(FontStyle.REGULAR, null).deriveFont(15f));
        slider.setRequestFocusEnabled(false);
        slider.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                slider.getParent().repaint();
            }
        });
        slider.setBackground(AppThemeColor.FRAME);
        return slider;
    }

    public JSlider getSlider(int min, int max, int value, Color background) {
        JSlider slider = this.getSlider(min, max, value);
        slider.setBackground(background);
        return slider;
    }

    public JScrollPane getVerticalContainer(JPanel container) {
        JScrollPane scrollPane = new JScrollPane(container);
        scrollPane.setBorder(null);
        scrollPane.setBackground(AppThemeColor.FRAME);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.addMouseWheelListener(e -> MercuryStoreUI.scrollToEndSubject.onNext(false));

        container.getParent().setBackground(AppThemeColor.TRANSPARENT);
        JScrollBar vBar = scrollPane.getVerticalScrollBar();
        vBar.setBackground(AppThemeColor.SLIDE_BG);
        vBar.setUI(new MercuryScrollBarUI());
        vBar.setPreferredSize(new Dimension(11, Integer.MAX_VALUE));
        vBar.setUnitIncrement(3);
        vBar.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        return scrollPane;
    }

    public JPanel getTransparentPanel(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(AppThemeColor.TRANSPARENT);
        return panel;
    }

    public JPanel getTransparentPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(AppThemeColor.TRANSPARENT);
        return panel;
    }

    public JPanel getBorderedTransparentPanel(Border border, LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(AppThemeColor.TRANSPARENT);
        panel.setBorder(border);
        return panel;
    }

    public ImageIcon getIcon(String iconPath, float size) {
        BufferedImage icon = null;
        try {
            BufferedImage buttonIcon = ImageIO.read(getClass().getClassLoader().getResource(iconPath));
            icon = Scalr.resize(buttonIcon, (int) (scale * size));
        } catch (IOException e) {
            log.error(e);
        }
        return new ImageIcon(icon);
    }

    public ImageIcon getIcon(URL iconPath, float size) {
        BufferedImage icon = null;
        try {
            BufferedImage buttonIcon = ImageIO.read(iconPath);
            icon = Scalr.resize(buttonIcon, (int) (scale * size));
        } catch (IOException e) {
            log.error(e);
        }
        return new ImageIcon(icon);
    }

    public ImageIcon getImage(String iconPath) {
        BufferedImage icon = null;
        try {
            icon = ImageIO.read(getClass().getClassLoader().getResource(iconPath));
        } catch (IOException e) {
            log.error(e);
        }
        return new ImageIcon(icon);
    }

    public ImageIcon getImageLocal(String iconPath) {
        BufferedImage icon = null;
        try {
            String filePath = "./resources/" + iconPath;
            File file = new File(filePath);
            if (file.exists()) {
                icon = ImageIO.read(file);
            } else {
                return null;
            }
        } catch (IOException e) {
            log.error(e);
        }
        return new ImageIcon(icon);
    }

    public Dimension convertSize(Dimension initialSize) {
        return new Dimension((int) (initialSize.width * scale), (int) (initialSize.height * scale));
    }

    public JTextArea getSimpleTextArea(String text) {
        JTextArea area = new JTextArea(text);
        area.setEditable(false);
        area.setWrapStyleWord(true);
        area.setLineWrap(true);
        area.setBackground(AppThemeColor.FRAME);
        area.setBorder(null);
        area.setFont(getSelectedFont(FontStyle.REGULAR, text).deriveFont(scale * 16f));
        area.setForeground(AppThemeColor.TEXT_DEFAULT);
        return area;
    }

    public JTextArea getSimpleTextArea(String text, FontStyle style, float size) {
        JTextArea area = this.getSimpleTextArea(text);
        area.setFont(this.getFont(style, size));
        area.setAlignmentX(SwingConstants.LEFT);
        area.setAlignmentY(SwingConstants.CENTER);
        return area;
    }

    public JPanel getSeparator() {
        JPanel panel = getTransparentPanel();
        JTextPane textPane = new JTextPane();
        textPane.setBackground(AppThemeColor.FRAME);
        textPane.setContentType("text/html");
        textPane.setText("<hr>");
        panel.add(textPane);
        return panel;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    private Font getSelectedFont(FontStyle fontStyle, String text, Float deriveFont) {
        Font result = null;
        result = fetchByLanguage(text, fontStyle);

        if (deriveFont != null) {
            return result.deriveFont(deriveFont);
        }
        return result;
    }

    private Font getSelectedFont(FontStyle fontStyle, String text) {
        return getSelectedFont(fontStyle, text, null);
    }

    private Font fetchByLanguage(String text, FontStyle fontStyle) {
        if (isKorean(text)) {
            return KR_FONT;
        }
        if (isCJK(text)) {
            return CJK_FONT;
        }
        switch (fontStyle) {
            case BOLD:
                return BOLD_FONT;
            case REGULAR:
                return REGULAR_FONT;
        }
        return DEFAULT_FONT;

    }

    public JPanel getJPanel(LayoutManager layoutManager) {
        JPanel panel = new JPanel(layoutManager);
        panel.setBackground(AppThemeColor.FRAME);
        return panel;
    }

    public JPanel getJPanel(LayoutManager layoutManager, Color bg) {
        JPanel panel = new JPanel(layoutManager);
        panel.setBackground(bg);
        return panel;
    }

    public JPanel wrapToSlide(JComponent panel) {
        JPanel wrapper = this.getJPanel(new BorderLayout());
        wrapper.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        wrapper.add(panel, BorderLayout.CENTER);
        return wrapper;
    }

    public JPanel wrapToSlide(JComponent panel, Color bg) {
        JPanel wrapper = this.wrapToSlide(panel);
        wrapper.setBackground(bg);
        return wrapper;
    }

    public JPanel wrapToSlide(JComponent panel, Color bg, int top, int left, int bottom, int righ) {
        JPanel wrapper = this.wrapToSlide(panel, top, left, bottom, righ);
        wrapper.setBackground(bg);
        return wrapper;
    }

    public JPanel wrapToSlide(JComponent panel, int top, int left, int bottom, int right) {
        JPanel wrapper = this.wrapToSlide(panel);
        wrapper.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
        return wrapper;
    }

    public JPanel wrapToAdrSlide(JComponent panel, int top, int left, int bottom, int right) {
        JPanel wrapper = new JPanel(new BorderLayout()) {
            @Override
            public void remove(Component comp) {
                panel.remove(comp);
            }

            @Override
            public Component add(Component comp) {
                return panel.add(comp);
            }
        };
        wrapper.setBackground(AppThemeColor.FRAME);
        wrapper.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
        wrapper.add(panel, BorderLayout.CENTER);
        return wrapper;
    }

    public JPopupMenu getContextPanel() {
        JPopupMenu contextMenu = new JPopupMenu();
        contextMenu.setBackground(AppThemeColor.FRAME);
        contextMenu.setBorder(BorderFactory.createLineBorder(AppThemeColor.BORDER));
        contextMenu.setFont(getSelectedFont(FontStyle.REGULAR, null, scale * 16f));
        contextMenu.setForeground(AppThemeColor.TEXT_DEFAULT);
        return contextMenu;
    }

    public JMenuItem getMenuItem(String text) {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.setFont(getSelectedFont(FontStyle.REGULAR, text, scale * 16f));
        menuItem.setForeground(AppThemeColor.TEXT_DEFAULT);
        return menuItem;
    }

    public JMenuItem getMenu(String text) {
        JMenu menu = new JMenu(text);
        menu.setFont(getSelectedFont(FontStyle.REGULAR, text, scale * 16f));
        menu.setForeground(AppThemeColor.TEXT_DEFAULT);
        return menu;
    }

    /**
     * Checking if text is chinese/japanese/korean(old korean)
     *
     * @param s
     * @return
     */
    public static boolean isCJK(String s) {
        if (StringUtils.isBlank(s)) {
            return false; //TODO: check for configuration if language is set to china then return true
        }
        return s.codePoints().anyMatch(Character::isIdeographic);
    }

    public static boolean isKorean(String s) {
        if (StringUtils.isBlank(s)) {
            Languages languages = Configuration.get().applicationConfiguration().get().getLanguages();
            if (languages != null && languages.equals(Languages.kr)) {
                return true;
            } else {
                return false;
            }
        }
        return s.codePoints().anyMatch(x -> Character.UnicodeScript.of(x).equals(Character.UnicodeScript.HANGUL));
    }

    public String getTooltipMessageForChatHistory(NotificationDescriptor source) {
        StringBuilder b = new StringBuilder();

        source.getRelatedMessages().stream().forEach(d -> {
            b.append("<p>");
            b.append(d.isIncoming() ? "> " : "");
            b.append(d.getMessage());
            //b.append("<br/>");
            b.append("</p>");
        });
        return b.toString();
    }

}
