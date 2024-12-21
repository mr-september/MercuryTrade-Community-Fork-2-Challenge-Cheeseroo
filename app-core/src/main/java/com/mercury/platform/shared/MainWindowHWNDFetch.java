package com.mercury.platform.shared;

import com.mercury.platform.shared.config.Configuration;
import com.sun.jna.Native;
import com.sun.jna.platform.DesktopWindow;
import com.sun.jna.platform.WindowUtils;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MainWindowHWNDFetch {
    private Logger logger = LogManager.getLogger(MainWindowHWNDFetch.class.getSimpleName());
    public static MainWindowHWNDFetch INSTANCE = MainWindowHWNDFetch.MainWindowHWNDFetchHolder.HOLDER_INSTANCE;

    private static class MainWindowHWNDFetchHolder {
        static final MainWindowHWNDFetch HOLDER_INSTANCE = new MainWindowHWNDFetch();
    }

    public boolean isPoe2() {
        boolean isPoe2 = Configuration.get().applicationConfiguration().get().isPoe2();
        return isPoe2;
    }

    public Optional<DesktopWindow> getMainWindow() {
        List<DesktopWindow> windowList = getMainWindowList();
        if (windowList.isEmpty()) {
            return Optional.empty();
        } else  {
            return Optional.ofNullable(windowList.get(0));
        }
    }

    public List<DesktopWindow> getMainWindowList() {
        List<DesktopWindow> windowList = WindowUtils.getAllWindows(false).stream().filter(x -> isPoe(x)).collect(Collectors.toList());
        return windowList;
    }

    public WinDef.HWND findWindow() {
        WinDef.HWND hwnd = User32.INSTANCE.FindWindow("POEWindowClass", null);
        return hwnd;
    }

    public boolean isPoe(DesktopWindow desktopWindow) {
//        if (this.isPoe2()) {

//            return desktopWindow.getTitle().contains("PathOfExile");
//        } else {
            char[] className = new char[512];
            User32.INSTANCE.GetClassName(desktopWindow.getHWND(), className, 512);
            return Native.toString(className).equals("POEWindowClass");
//        }
    }
}
