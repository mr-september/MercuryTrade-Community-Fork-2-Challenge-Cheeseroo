package com.mercury.platform.shared;

//import sun.awt.SunToolkit;
//import sun.print.ProxyPrintGraphics;
//import sun.swing.PrintColorUIResource;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.print.PrinterGraphics;
import java.util.HashMap;
import java.util.Map;

import static java.awt.RenderingHints.*;

public class SwingUtilitiesMorph {
    private static final int CHAR_BUFFER_SIZE = 100;
    private static final Object charsBufferLock = new Object();
    private static char[] charsBuffer = new char[CHAR_BUFFER_SIZE];
    public static final int HI_SURROGATE_START = 0xD800;
    public static final int HI_SURROGATE_END = 0xDBFF;
    public static final int LO_SURROGATE_START = 0xDC00;
    public static final int LO_SURROGATE_END = 0xDFFF;

    public static void drawString(JComponent c, Graphics g, String text,
                                  int x, int y) {
        // c may be null

        // All non-editable widgets that draw strings call into this
        // methods.  By non-editable that means widgets like JLabel, JButton
        // but NOT JTextComponents.
        if (text == null || text.length() <= 0) { //no need to paint empty strings
            return;
        }
        if (isPrinting(g)) {
            Graphics2D g2d = getGraphics2D(g);
            if (g2d != null) {
                /* The printed text must scale linearly with the UI.
                 * Calculate the width on screen, obtain a TextLayout with
                 * advances for the printer graphics FRC, and then justify
                 * it to fit in the screen width. This distributes the spacing
                 * more evenly than directly laying out to the screen advances.
                 */
                String trimmedText = trimTrailingSpaces(text);
                if (!trimmedText.isEmpty()) {
                    float screenWidth = (float) g2d.getFont().getStringBounds
                            (trimmedText, getFontRenderContext(c)).getWidth();
                    TextLayout layout = createTextLayout(c, text, g2d.getFont(),
                            g2d.getFontRenderContext());

                    // If text fits the screenWidth, then do not need to justify
                    if (SwingUtilitiesMorph.stringWidth(c, g2d.getFontMetrics(),
                            trimmedText) > screenWidth) {
                        layout = layout.getJustifiedLayout(screenWidth);
                    }
                    /* Use alternate print color if specified */
                    Color col = g2d.getColor();
//                    if (col instanceof PrintColorUIResource) {
//                        g2d.setColor(((PrintColorUIResource) col).getPrintColor());
//                    }

                    layout.draw(g2d, x, y);

                    g2d.setColor(col);
                }

                return;
            }
        }

        // If we get here we're not printing
        if (g instanceof Graphics2D) {
            AATextInfo info = drawTextAntialiased(c);
            Graphics2D g2 = (Graphics2D) g;

            boolean needsTextLayout = ((c != null) &&
                    (c.getClientProperty(TextAttribute.NUMERIC_SHAPING) != null));

            if (needsTextLayout) {
                synchronized (charsBufferLock) {
                    int length = syncCharsBuffer(text);
                    needsTextLayout = isComplexLayout(charsBuffer, 0, length);
                }
            }

            if (info != null) {
                Object oldContrast = null;
                Object oldAAValue = g2.getRenderingHint(KEY_TEXT_ANTIALIASING);
                if (info.aaHint != oldAAValue) {
                    g2.setRenderingHint(KEY_TEXT_ANTIALIASING, info.aaHint);
                } else {
                    oldAAValue = null;
                }
                if (info.lcdContrastHint != null) {
                    oldContrast = g2.getRenderingHint(KEY_TEXT_LCD_CONTRAST);
                    if (info.lcdContrastHint.equals(oldContrast)) {
                        oldContrast = null;
                    } else {
                        g2.setRenderingHint(KEY_TEXT_LCD_CONTRAST,
                                info.lcdContrastHint);
                    }
                }

                if (needsTextLayout) {
                    TextLayout layout = createTextLayout(c, text, g2.getFont(),
                            g2.getFontRenderContext());
                    layout.draw(g2, x, y);
                } else {
                    g.drawString(text, x, y);
                }

                if (oldAAValue != null) {
                    g2.setRenderingHint(KEY_TEXT_ANTIALIASING, oldAAValue);
                }
                if (oldContrast != null) {
                    g2.setRenderingHint(KEY_TEXT_LCD_CONTRAST, oldContrast);
                }

                return;
            }

            if (needsTextLayout) {
                TextLayout layout = createTextLayout(c, text, g2.getFont(),
                        g2.getFontRenderContext());
                layout.draw(g2, x, y);
                return;
            }
        }

        g.drawString(text, x, y);
    }

    public static class AATextInfo {

        private static AATextInfo getAATextInfoFromMap(Map hints) {

            Object aaHint = hints.get(KEY_TEXT_ANTIALIASING);
            Object contHint = hints.get(KEY_TEXT_LCD_CONTRAST);

            if (aaHint == null ||
                    aaHint == VALUE_TEXT_ANTIALIAS_OFF ||
                    aaHint == VALUE_TEXT_ANTIALIAS_DEFAULT) {
                return null;
            } else {
                return new AATextInfo(aaHint, (Integer) contHint);
            }
        }

//        public static AATextInfo getAATextInfo(boolean lafCondition) {
//            SunToolkit.setAAFontSettingsCondition(lafCondition);
//            Toolkit tk = Toolkit.getDefaultToolkit();
//            Object map = tk.getDesktopProperty(SunToolkit.DESKTOPFONTHINTS);
//            if (map instanceof Map) {
//                return getAATextInfoFromMap((Map) map);
//            } else {
//                return null;
//            }
//        }

        Object aaHint;
        Integer lcdContrastHint;
        FontRenderContext frc;

        /* These are rarely constructed objects, and only when a complete
         * UI is being updated, so the cost of the tests here is minimal
         * and saves tests elsewhere.
         * We test that the values are ones we support/expect.
         */
        public AATextInfo(Object aaHint, Integer lcdContrastHint) {
            if (aaHint == null) {
                throw new InternalError("null not allowed here");
            }
            if (aaHint == VALUE_TEXT_ANTIALIAS_OFF ||
                    aaHint == VALUE_TEXT_ANTIALIAS_DEFAULT) {
                throw new InternalError("AA must be on");
            }
            this.aaHint = aaHint;
            this.lcdContrastHint = lcdContrastHint;
            this.frc = new FontRenderContext(null, aaHint,
                    VALUE_FRACTIONALMETRICS_DEFAULT);
        }
    }

    public static AATextInfo drawTextAntialiased(JComponent c) {
        if (c != null) {
            /* a non-null property implies some form of AA requested */
            return (AATextInfo) c.getClientProperty(AA_TEXT_PROPERTY_KEY);
        }
        // No component, assume aa is off
        return null;
    }


    public static final Object AA_TEXT_PROPERTY_KEY =
            new StringBuffer("AATextInfoPropertyKey");

    static boolean isPrinting(Graphics g) {
        return (g instanceof PrinterGraphics || g instanceof PrintGraphics);
    }

    public static Graphics2D getGraphics2D(Graphics g) {
        if (g instanceof Graphics2D) {
            return (Graphics2D) g;
//        } else if (g instanceof ProxyPrintGraphics) {
//            return (Graphics2D) (((ProxyPrintGraphics) g).getGraphics());
//        }
        } else {
            return null;
        }
    }

    private static String trimTrailingSpaces(String s) {
        int i = s.length() - 1;
        while (i >= 0 && Character.isWhitespace(s.charAt(i))) {
            i--;
        }
        return s.substring(0, i + 1);
    }

    public static FontRenderContext getFontRenderContext(Component c) {
        assert c != null;
        if (c == null) {
            return DEFAULT_FRC;
        } else {
            return c.getFontMetrics(c.getFont()).getFontRenderContext();
        }
    }

    /**
     * A convenience method to get FontRenderContext.
     * Returns the FontRenderContext for the passed in FontMetrics or
     * for the passed in Component if FontMetrics is null
     */
    private static FontRenderContext getFontRenderContext(Component c, FontMetrics fm) {
        assert fm != null || c != null;
        return (fm != null) ? fm.getFontRenderContext()
                : getFontRenderContext(c);
    }

    public static final FontRenderContext DEFAULT_FRC =
            new FontRenderContext(null, false, false);

    private static TextLayout createTextLayout(JComponent c, String s,
                                               Font f, FontRenderContext frc) {
        Object shaper = (c == null ?
                null : c.getClientProperty(TextAttribute.NUMERIC_SHAPING));
        if (shaper == null) {
            return new TextLayout(s, f, frc);
        } else {
            Map<TextAttribute, Object> a = new HashMap<TextAttribute, Object>();
            a.put(TextAttribute.FONT, f);
            a.put(TextAttribute.NUMERIC_SHAPING, shaper);
            return new TextLayout(s, a, frc);
        }
    }

    private static int syncCharsBuffer(String s) {
        int length = s.length();
        if ((charsBuffer == null) || (charsBuffer.length < length)) {
            charsBuffer = s.toCharArray();
        } else {
            s.getChars(0, length, charsBuffer, 0);
        }
        return length;
    }

    public static final boolean isComplexLayout(char[] text, int start, int limit) {
        return isComplexText(text, start, limit);
    }

    public static final int MIN_LAYOUT_CHARCODE = 0x0300;
    public static final int MAX_LAYOUT_CHARCODE = 0x206F;

    public static boolean isNonSimpleChar(char ch) {
        return
                isComplexCharCode(ch) ||
                        (ch >= HI_SURROGATE_START &&
                                ch <= LO_SURROGATE_END);
    }

    public static boolean isComplexText(char[] chs, int start, int limit) {

        for (int i = start; i < limit; i++) {
            if (chs[i] < MIN_LAYOUT_CHARCODE) {
                continue;
            } else if (isNonSimpleChar(chs[i])) {
                return true;
            }
        }
        return false;
    }

    public static boolean isComplexCharCode(int code) {

        if (code < MIN_LAYOUT_CHARCODE || code > MAX_LAYOUT_CHARCODE) {
            return false;
        } else if (code <= 0x036f) {
            // Trigger layout for combining diacriticals 0x0300->0x036f
            return true;
        } else if (code < 0x0590) {
            // No automatic layout for Greek, Cyrillic, Armenian.
            return false;
        } else if (code <= 0x06ff) {
            // Hebrew 0590 - 05ff
            // Arabic 0600 - 06ff
            return true;
        } else if (code < 0x0900) {
            return false; // Syriac and Thaana
        } else if (code <= 0x0e7f) {
            // if Indic, assume shaping for conjuncts, reordering:
            // 0900 - 097F Devanagari
            // 0980 - 09FF Bengali
            // 0A00 - 0A7F Gurmukhi
            // 0A80 - 0AFF Gujarati
            // 0B00 - 0B7F Oriya
            // 0B80 - 0BFF Tamil
            // 0C00 - 0C7F Telugu
            // 0C80 - 0CFF Kannada
            // 0D00 - 0D7F Malayalam
            // 0D80 - 0DFF Sinhala
            // 0E00 - 0E7F if Thai, assume shaping for vowel, tone marks
            return true;
        } else if (code < 0x0f00) {
            return false;
        } else if (code <= 0x0fff) { // U+0F00 - U+0FFF Tibetan
            return true;
        } else if (code < 0x1100) {
            return false;
        } else if (code < 0x11ff) { // U+1100 - U+11FF Old Hangul
            return true;
        } else if (code < 0x1780) {
            return false;
        } else if (code <= 0x17ff) { // 1780 - 17FF Khmer
            return true;
        } else if (code < 0x200c) {
            return false;
        } else if (code <= 0x200d) { //  zwj or zwnj
            return true;
        } else if (code >= 0x202a && code <= 0x202e) { // directional control
            return true;
        } else if (code >= 0x206a && code <= 0x206f) { // directional control
            return true;
        }
        return false;
    }

    public static int stringWidth(JComponent c, FontMetrics fm, String string) {
        if (string == null || string.equals("")) {
            return 0;
        }
        boolean needsTextLayout = ((c != null) &&
                (c.getClientProperty(TextAttribute.NUMERIC_SHAPING) != null));
        if (needsTextLayout) {
            synchronized (charsBufferLock) {
                int length = syncCharsBuffer(string);
                needsTextLayout = isComplexLayout(charsBuffer, 0, length);
            }
        }
        if (needsTextLayout) {
            TextLayout layout = createTextLayout(c, string,
                    fm.getFont(), fm.getFontRenderContext());
            return (int) layout.getAdvance();
        } else {
            return fm.stringWidth(string);
        }
    }

    // Maintain a static 1x1 dummy image purely to extract an impartial coordinate space for font metrics when JComponent and Graphics are completely inaccessible.
    private static final java.awt.image.BufferedImage FONT_METRICS_DUMMY = new java.awt.image.BufferedImage(1, 1, java.awt.image.BufferedImage.TYPE_INT_ARGB);

    public static FontMetrics getFontMetrics(JComponent c, Graphics g,
                                             Font font) {
        if (c != null) {
            // Note: We assume that we're using the FontMetrics
            // from the widget to layout out text, otherwise we can get
            // mismatches when printing.
            return c.getFontMetrics(font);
        }
        if (g != null) {
            return g.getFontMetrics(font);
        }
        return FONT_METRICS_DUMMY.createGraphics().getFontMetrics(font);
    }
}
