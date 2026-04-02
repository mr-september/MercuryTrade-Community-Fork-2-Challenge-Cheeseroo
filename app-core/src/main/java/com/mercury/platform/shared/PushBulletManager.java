package com.mercury.platform.shared;

import com.github.sheigutn.pushbullet.Pushbullet;
import com.github.sheigutn.pushbullet.exception.PushbulletApiException;
import com.github.sheigutn.pushbullet.items.chat.Chat;
import com.github.sheigutn.pushbullet.items.device.Device;
import com.github.sheigutn.pushbullet.items.push.sendable.SendablePush;
import com.github.sheigutn.pushbullet.items.push.sendable.defaults.SendableFilePush;
import com.github.sheigutn.pushbullet.items.push.sendable.defaults.SendableNotePush;
import com.mercury.platform.shared.config.Configuration;
import com.mercury.platform.shared.config.descriptor.TaskBarDescriptor;
import com.mercury.platform.shared.config.descriptor.VulkanDescriptor;
import com.mercury.platform.shared.config.json.JSONHelper;
import com.mercury.platform.shared.store.MercuryStoreCore;
import com.sun.jna.Native;
import com.sun.jna.platform.DesktopWindow;
import com.sun.jna.platform.WindowUtils;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PushBulletManager {
    private Logger logger = LogManager.getLogger(PushBulletManager.class.getSimpleName());
    public static PushBulletManager INSTANCE = PushBulletManagerHolder.HOLDER_INSTANCE;
    protected TaskBarDescriptor config;
    private String pushbulletAccessToken;
    private Pushbullet pushbullet;

    private PushBulletManager() {
        reloadAccessToken();
    }

    public void reloadAccessToken() {
        pushbulletAccessToken = Configuration.get().applicationConfiguration().get().getPushbulletAccessToken();
        config = Configuration.get().taskBarConfiguration().get();
        if (pushbulletAccessToken != null && !pushbulletAccessToken.isEmpty()) {
            pushbulletAccessToken = pushbulletAccessToken.replace(" ", "");
            pushbullet = new Pushbullet(pushbulletAccessToken);
        }
    }

    public void sendPush(String content) {
        config = Configuration.get().taskBarConfiguration().get();
        if (pushbullet != null && config.isPushbulletOn()) {
            pushbullet.push(new SendableNotePush("MercuryChat", content));
        }
    }

    public void sendPush(String content, String nickName) {
        config = Configuration.get().taskBarConfiguration().get();
        if (pushbullet != null && config.isPushbulletOn()) {
            try {
                pushbullet.push(new SendableNotePush("MercuryChat - " + nickName, content));
            } catch (PushbulletApiException e) {
                logger.error(e.getMessage());
            }
        }
    }

    public void testPush() {
        if (pushbullet != null && config.isPushbulletOn()) {
            pushbullet.push(new SendableNotePush("MercuryChat" , "Test notification"));
        }
    }
    private static class PushBulletManagerHolder {
        static final PushBulletManager HOLDER_INSTANCE = new PushBulletManager();
    }
}
