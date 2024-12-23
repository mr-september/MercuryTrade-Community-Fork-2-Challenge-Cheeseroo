package com.mercury.platform;

import com.mercury.platform.core.DevStarter;
import com.mercury.platform.core.ProdStarter;
import com.mercury.platform.core.utils.FileMonitor;
import com.mercury.platform.core.utils.error.ErrorHandler;
import com.mercury.platform.shared.MainWindowHWNDFetch;
import com.mercury.platform.shared.config.Configuration;
import com.mercury.platform.shared.store.MercuryStoreCore;
import com.mercury.platform.ui.frame.other.MercuryLoadingFrame;
import com.mercury.platform.ui.frame.titled.GamePathChooser;
import com.mercury.platform.ui.manager.FramesManager;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.io.File;
import java.time.Instant;

public class AppMain {

    private static final Logger logger = LogManager.getLogger(AppMain.class);
    private static boolean shouldLogPerformance = false;
    private static String MERCURY_TRADE_FOLDER = SystemUtils.IS_OS_WINDOWS ? System.getenv("USERPROFILE") + "\\AppData\\Local\\MercuryTrade" : "AppData/Local/MercuryTrade";

    private static MercuryLoadingFrame mercuryLoadingFrame;

    public static void main(String[] args) {
        Thread mercuryLoadingFrameThread = null;
        try {
            Instant start = Instant.now();
            System.setProperty("sun.java2d.d3d", "false");
            System.setProperty("jna.nosys", "true");

            UIManager.put("ToolTipManager.enableToolTipMode", "allWindows");

            boolean standalone = BooleanUtils.toBoolean(System.getProperty("standalone"));
            boolean dev = BooleanUtils.toBoolean(System.getProperty("dev"));
            boolean hideLoadingIcon = BooleanUtils.toBoolean(System.getProperty("hideLoadingIcon"));

            logger.warn("loaded runtime settings: ");
            logger.warn("standalone=" + standalone);
            logger.warn("dev=" + dev);
            logger.warn("hideLoadingIcon=" + hideLoadingIcon);

            new ErrorHandler();
            if (!hideLoadingIcon) {
                mercuryLoadingFrameThread = new Thread(() -> {
                    mercuryLoadingFrame = new MercuryLoadingFrame();
                    mercuryLoadingFrame.init();
                    mercuryLoadingFrame.showComponent();
                    mercuryLoadingFrame.subscribe();
                });
                mercuryLoadingFrameThread.start();
            }

            checkCreateAppDataFolder();

            if (dev) {
                new DevStarter().startApplication();
            } else if (standalone) {
                new ProdStarter().startApplication(true);
            } else {
                new ProdStarter().startApplication(false);
            }


            String configGamePath = Configuration.get().applicationConfiguration().get().getGamePath();
            if (configGamePath.equals("") || !isValidGamePath(configGamePath)) {
                String gamePath = getGamePath();
                if (gamePath == null) {
                    MercuryStoreCore.appLoadingSubject.onNext(false);
                    GamePathChooser gamePathChooser = new GamePathChooser();
                    gamePathChooser.init();
                } else {
                    gamePath = gamePath + "/";
                    Configuration.get().applicationConfiguration().get().setGamePath(gamePath);
                    MercuryStoreCore.saveConfigSubject.onNext(true);
                    new FileMonitor().start();
                    FramesManager.INSTANCE.start();
                    MercuryStoreCore.appLoadingSubject.onNext(false);
                }
            } else {
                new FileMonitor().start();
                FramesManager.INSTANCE.start();
                MercuryStoreCore.appLoadingSubject.onNext(false);
            }

            if (!hideLoadingIcon) {
                mercuryLoadingFrameThread.join();
            }
            Instant end = Instant.now();
            logger.warn("Startup time:"+ (end.toEpochMilli() - start.toEpochMilli()) + " ms");
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex);
            System.exit(-153);
        }
    }

    private static boolean isValidGamePath(String gamePath) {
        File client = new File(gamePath + File.separator + "logs" + File.separator + "Client.txt");
        File kakaoClient = new File(gamePath + File.separator + "logs" + File.separator + "KakaoClient.txt");
        return client.exists() || kakaoClient.exists();
    }

    private static String getGamePath() {
        if (SystemUtils.IS_OS_WINDOWS) {
            return MainWindowHWNDFetch.INSTANCE.getMainWindow().map(x -> StringUtils.substringBeforeLast(x.getFilePath(), "\\")).orElse(null);
        } else {
            return null;
        }
    }

    private static void checkCreateAppDataFolder() {
        File mercuryTradeFolder = new File(MERCURY_TRADE_FOLDER);
        if (!mercuryTradeFolder.exists()) {
            boolean mercuryTradeFolderCreated = mercuryTradeFolder.mkdirs();
            if (!mercuryTradeFolderCreated) {
                logger.error("Mercury trade folder in location %s couldn't be created - check permissions");
            }
        }
    }
}
