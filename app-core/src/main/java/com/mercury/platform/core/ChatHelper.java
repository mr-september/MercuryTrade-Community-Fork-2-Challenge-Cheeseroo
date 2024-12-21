package com.mercury.platform.core;

import com.mercury.platform.shared.AsSubscriber;
import com.mercury.platform.shared.MainWindowHWNDFetch;
import com.mercury.platform.shared.config.Configuration;
import com.mercury.platform.shared.config.descriptor.TaskBarDescriptor;
import com.mercury.platform.shared.entity.message.MercuryError;
import com.mercury.platform.shared.store.MercuryStoreCore;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.time.Instant;


public class ChatHelper implements AsSubscriber {
    private Robot robot;
    private static boolean clipboardMessageOn = true;
    private final static Logger logger = LogManager.getLogger(ChatHelper.class);

    public ChatHelper() {
        subscribe();
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    private void executeClipboardMessage() {
        if (clipboardMessageOn && isGameOpen()) {
            this.gameToFront();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            MercuryStoreCore.blockHotkeySubject.onNext(true);
            robot.keyRelease(KeyEvent.VK_ALT);
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);

            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_A);
            robot.keyRelease(KeyEvent.VK_CONTROL);
            robot.keyRelease(KeyEvent.VK_A);

            robot.keyPress(KeyEvent.VK_BACK_SPACE);
            robot.keyRelease(KeyEvent.VK_BACK_SPACE);

            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_V);
            robot.keyRelease(KeyEvent.VK_V);
            robot.keyRelease(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
            MercuryStoreCore.blockHotkeySubject.onNext(false);
        }
    }

    private void executeMessage(String message) {
        clipboardMessageOn = false;
        this.gameToFront();
        StringSelection selection = new StringSelection(message);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, null);
        MercuryStoreCore.blockHotkeySubject.onNext(true);
        robot.keyRelease(KeyEvent.VK_ALT);
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);

        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_A);
        robot.keyRelease(KeyEvent.VK_CONTROL);
        robot.keyRelease(KeyEvent.VK_A);

        robot.keyPress(KeyEvent.VK_BACK_SPACE);
        robot.keyRelease(KeyEvent.VK_BACK_SPACE);

        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
        MercuryStoreCore.blockHotkeySubject.onNext(false);
        clipboardMessageOn = true;
    }

    private void executeTradeMessage() {
        clipboardMessageOn = false;
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Clipboard clipboard = toolkit.getSystemClipboard();
        try {
            String result = (String) clipboard.getData(DataFlavor.stringFlavor);
            if (result != null && (result.contains("listed for") || result.contains("for my"))) {
                this.gameToFront();
                MercuryStoreCore.blockHotkeySubject.onNext(true);
                robot.keyRelease(KeyEvent.VK_ALT);
                robot.keyPress(KeyEvent.VK_ENTER);
                robot.keyRelease(KeyEvent.VK_ENTER);

                robot.keyPress(KeyEvent.VK_CONTROL);
                robot.keyPress(KeyEvent.VK_A);
                robot.keyRelease(KeyEvent.VK_CONTROL);
                robot.keyRelease(KeyEvent.VK_A);

                robot.keyPress(KeyEvent.VK_BACK_SPACE);
                robot.keyRelease(KeyEvent.VK_BACK_SPACE);

                robot.keyPress(KeyEvent.VK_CONTROL);
                robot.keyPress(KeyEvent.VK_V);
                robot.keyRelease(KeyEvent.VK_V);
                robot.keyRelease(KeyEvent.VK_CONTROL);
                robot.keyPress(KeyEvent.VK_ENTER);
                robot.keyRelease(KeyEvent.VK_ENTER);

                Timer timer = new Timer(300, action -> {
                    StringSelection selection = new StringSelection("");
                    clipboard.setContents(selection, null);
                });
                timer.setRepeats(false);
                timer.start();

                MercuryStoreCore.blockHotkeySubject.onNext(false);
            }
        } catch (UnsupportedFlavorException | IOException e) {
            MercuryStoreCore.errorHandlerSubject.onNext(new MercuryError(e));
        }
        clipboardMessageOn = true;
    }

    private void openChat(String whisper) {
        clipboardMessageOn = false;
        this.gameToFront();
        StringSelection selection = new StringSelection("@" + whisper);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
        MercuryStoreCore.blockHotkeySubject.onNext(true);
        robot.keyRelease(KeyEvent.VK_ALT);
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);

        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_A);
        robot.keyRelease(KeyEvent.VK_CONTROL);
        robot.keyRelease(KeyEvent.VK_A);

        robot.keyPress(KeyEvent.VK_BACK_SPACE);
        robot.keyRelease(KeyEvent.VK_BACK_SPACE);

        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_SPACE);
        robot.keyRelease(KeyEvent.VK_SPACE);
        MercuryStoreCore.blockHotkeySubject.onNext(false);
        clipboardMessageOn = true;
    }

    private void findInStashTab(String toBeFound) {
        toBeFound = removeCharactersThatBreakTheSearch(toBeFound);
        clipboardMessageOn = false;
        this.gameToFront();
        StringSelection selection = new StringSelection(toBeFound);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        System.out.println(toBeFound);
        clipboard.setContents(selection, null);
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            logger.error("Unexpected expection while waiting till clipboard contents will set", e);
        }
        MercuryStoreCore.blockHotkeySubject.onNext(true);

        robot.keyRelease(KeyEvent.VK_ALT);
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_F);

        robot.keyRelease(KeyEvent.VK_CONTROL);
        robot.keyRelease(KeyEvent.VK_F);


        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_V);

        robot.keyRelease(KeyEvent.VK_CONTROL);
        robot.keyRelease(KeyEvent.VK_V);

        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);

        MercuryStoreCore.blockHotkeySubject.onNext(false);
        clipboardMessageOn = true;
    }


    final WinDef.HWND HWND_TOPMOST = new WinDef.HWND(new Pointer(-1));
    final int SWP_NOSIZE = 0x0001;
    final int SWP_NOMOVE = 0x0002;
    final int SWP_SHOWWINDOW = 0x0040;

    private void gameToFront() {
        if (Configuration.get().applicationConfiguration().get().isDisableGameToFront()) {
            return;
        }


        if (SystemUtils.IS_OS_WINDOWS) {
            WinDef.HWND hwnd = MainWindowHWNDFetch.INSTANCE.findWindow();
            User32.INSTANCE.ShowWindow(hwnd, 5);
            User32.INSTANCE.SetForegroundWindow(hwnd);
            User32.INSTANCE.SetFocus(hwnd);
        }

    }

    private boolean isGameOpen() {
        if (SystemUtils.IS_OS_WINDOWS) {
            return MainWindowHWNDFetch.INSTANCE.getMainWindow().isPresent();
        } else {
            return true;
        }
    }

    @Override
    public void subscribe() {
        MercuryStoreCore.chatCommandSubject.subscribe(this::executeMessage);
        MercuryStoreCore.chatClipboardSubject.subscribe(state -> this.executeClipboardMessage());
        MercuryStoreCore.openChatSubject.subscribe(this::openChat);
        MercuryStoreCore.findInStashTab.subscribe(this::findInStashTab);
        MercuryStoreCore.tradeWhisperSubject.subscribe(state -> this.executeTradeMessage());
        MercuryStoreCore.dndSubject.subscribe(state -> {
            TaskBarDescriptor config = Configuration.get().taskBarConfiguration().get();
            if (config.isInGameDnd()) {
                if (state) {
                    executeMessage("/dnd " + config.getDndResponseText());
                } else {
                    executeMessage("/dnd");
                }
            }
        });
        MercuryStoreCore.pushbulletSubject.subscribe(state -> {
            TaskBarDescriptor config = Configuration.get().taskBarConfiguration().get();
            config.setPushbulletOn(state);
        });
    }

    private String removeCharactersThatBreakTheSearch(String textToSearch) {
        if (StringUtils.isBlank(textToSearch)) {
            return StringUtils.EMPTY;
        }
        return textToSearch.replaceAll(",", StringUtils.EMPTY);
    }
}
