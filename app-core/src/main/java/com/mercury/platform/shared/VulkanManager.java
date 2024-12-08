package com.mercury.platform.shared;

import com.mercury.platform.shared.config.descriptor.VulkanDescriptor;
import com.mercury.platform.shared.store.MercuryStoreCore;
import com.sun.jna.Native;
import com.sun.jna.platform.DesktopWindow;
import com.sun.jna.platform.WindowUtils;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import org.apache.commons.lang3.SystemUtils;

import java.util.Timer;
import java.util.TimerTask;

public class VulkanManager {
    private static final int POE_WINDOWED_FULLSCREEN = 0x94000000;
    private static final int POE_WINDOWED = 0x14cf0000;
    private static final int WS_VISIBLE = 0x10000000;
    public static VulkanManager INSTANCE = VulkanManagerManagerHolder.HOLDER_INSTANCE;
    protected VulkanDescriptor vulkanSnapshot = new VulkanDescriptor();

    private VulkanManager() {
    }

    public void runSupport(VulkanDescriptor vulkanSnapshot) {
        this.vulkanSnapshot = vulkanSnapshot;
        MercuryStoreCore.uiLoadedSubject.subscribe(state -> {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    setVulkanSupport();
                }
            }, 0, 2000);
        });
    }

    public void changeSetting() {
        this.vulkanSnapshot.setVulkanSupportEnabled(!this.vulkanSnapshot.isVulkanSupportEnabled());
    }

    public boolean getSetting() {
        return this.vulkanSnapshot.isVulkanSupportEnabled();
    }

    private boolean setVulkanSupport() {
        if (this.vulkanSnapshot.isVulkanSupportEnabled()) {
            if (SystemUtils.IS_OS_WINDOWS) {

                HWND poeWindowClass = WindowUtils.getAllWindows(false).stream().filter(window -> {
                    char[] className = new char[512];
                    User32.INSTANCE.GetClassName(window.getHWND(), className, 512);
                    return Native.toString(className).equals("POEWindowClass");
                }).map(DesktopWindow::getHWND).findFirst().orElse(null);
                if (poeWindowClass == null) {
                    return false;
                }
                int windowLong = User32.INSTANCE.GetWindowLong(poeWindowClass, -16);
                if (windowLong == WS_VISIBLE) {
                    return true;
                }
                User32.INSTANCE.SetWindowLong(poeWindowClass, -16, WS_VISIBLE);
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    private static class VulkanManagerManagerHolder {
        static final VulkanManager HOLDER_INSTANCE = new VulkanManager();
    }
}
