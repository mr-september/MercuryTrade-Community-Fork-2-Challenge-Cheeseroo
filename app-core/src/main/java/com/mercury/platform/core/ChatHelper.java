package com.mercury.platform.core;

import com.mercury.platform.shared.AsSubscriber;
import com.mercury.platform.shared.MainWindowHWNDFetch;
import com.mercury.platform.shared.config.Configuration;
import com.mercury.platform.shared.config.descriptor.TaskBarDescriptor;
import com.mercury.platform.shared.entity.message.MercuryError;
import com.mercury.platform.shared.store.MercuryStoreCore;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.DesktopWindow;
import com.sun.jna.platform.WindowUtils;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import org.apache.commons.lang3.SystemUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.io.IOException;


public class ChatHelper implements AsSubscriber {
    private Robot robot;
    private static boolean clipboardMessageOn = true;

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
        clipboardMessageOn = false;
        this.gameToFront();
        StringSelection selection = new StringSelection(toBeFound);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, null);
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
        if (SystemUtils.IS_OS_WINDOWS) {
            for (DesktopWindow window : MainWindowHWNDFetch.INSTANCE.getMainWindowList()) {
                char[] className = new char[512];
                User32.INSTANCE.GetClassName(window.getHWND(), className, 512);

                User32.INSTANCE.ShowWindow(window.getHWND(), 5);

                boolean isAtFront = User32.INSTANCE.SetForegroundWindow(window.getHWND());
                int counter = 0;
                while (!isAtFront && counter < 10) {
                    isAtFront = User32.INSTANCE.SetForegroundWindow(window.getHWND());
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    counter++;
                }

                User32.INSTANCE.SetFocus(window.getHWND());

            }
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
}
