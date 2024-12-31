package com.mercury.platform.ui.manager;

import com.mercury.platform.TranslationKey;
import com.mercury.platform.shared.AsSubscriber;
import com.mercury.platform.shared.FrameVisibleState;
import com.mercury.platform.shared.IconConst;
import com.mercury.platform.shared.VulkanManager;
import com.mercury.platform.shared.config.Configuration;
import com.mercury.platform.shared.config.configration.impl.FramesConfigurationServiceImpl;
import com.mercury.platform.shared.config.descriptor.ApplicationDescriptor;
import com.mercury.platform.shared.config.descriptor.FrameDescriptor;
import com.mercury.platform.shared.store.MercuryStoreCore;
import com.mercury.platform.ui.adr.AdrManager;
import com.mercury.platform.ui.adr.AdrState;
import com.mercury.platform.ui.components.ComponentsFactory;
import com.mercury.platform.ui.components.fields.font.FontStyle;
import com.mercury.platform.ui.frame.AbstractComponentFrame;
import com.mercury.platform.ui.frame.AbstractOverlaidFrame;
import com.mercury.platform.ui.frame.AbstractScalableComponentFrame;
import com.mercury.platform.ui.frame.movable.AbstractMovableComponentFrame;
import com.mercury.platform.ui.frame.movable.ItemsGridFrame;
import com.mercury.platform.ui.frame.movable.NotificationFrame;
import com.mercury.platform.ui.frame.movable.TaskBarFrame;
import com.mercury.platform.ui.frame.other.*;
import com.mercury.platform.ui.frame.setup.location.SetUpLocationCommander;
import com.mercury.platform.ui.frame.setup.scale.SetUpScaleCommander;
import com.mercury.platform.ui.frame.titled.*;
import com.mercury.platform.ui.manager.routing.SettingsRoutManager;
import com.mercury.platform.ui.misc.AppThemeColor;
import com.mercury.platform.ui.misc.MercuryStoreUI;
import com.mercury.platform.ui.misc.note.Note;
import com.mercury.platform.ui.misc.note.NotesLoader;
import com.sun.jna.Native;
import com.sun.jna.platform.WindowUtils;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jnativehook.mouse.NativeMouseEvent;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.List;

public class FramesManager implements AsSubscriber {
    private ComponentsFactory componentsFactory = ComponentsFactory.INSTANCE;
    private final static Logger logger = LogManager.getLogger(FramesManager.class);
    public static FramesManager INSTANCE = FramesManagerHolder.HOLDER_INSTANCE;
    private Map<Class, AbstractOverlaidFrame> framesMap;
    private SetUpLocationCommander locationCommander;
    private SetUpScaleCommander scaleCommander;
    private AdrManager adrManager;
    private TrayIcon trayIcon;

    public AbstractOverlaidFrame taskBarFrame;

    private FramesManager() {
        this.framesMap = new HashMap<>();
        this.locationCommander = new SetUpLocationCommander();
        this.scaleCommander = new SetUpScaleCommander();
        this.adrManager = new AdrManager();
    }

    public void start() {
        Thread baseInitThread = new Thread(() -> {
            this.createTrayIcon();

            AbstractOverlaidFrame incMessageFrame = new NotificationFrame();
            this.framesMap.put(NotificationFrame.class, incMessageFrame);
            AbstractOverlaidFrame itemsMeshFrame = new ItemsGridFrame();
            this.framesMap.put(ItemsGridFrame.class, itemsMeshFrame);
            this.locationCommander.addFrame((AbstractMovableComponentFrame) incMessageFrame);
            this.locationCommander.addFrame((AbstractMovableComponentFrame) itemsMeshFrame);

            this.scaleCommander.addFrame((AbstractScalableComponentFrame) incMessageFrame);
            this.scaleCommander.addFrame((AbstractScalableComponentFrame) itemsMeshFrame);
        });
        baseInitThread.start();

        taskBarFrame = new TaskBarFrame();
        this.locationCommander.addFrame((AbstractMovableComponentFrame) taskBarFrame);
        this.scaleCommander.addFrame((AbstractScalableComponentFrame) taskBarFrame);

        NotesLoader notesLoader = new NotesLoader();

        List<Note> notesOnFirstStart = notesLoader.getNotesOnFirstStart();
        this.framesMap.put(NotesFrame.class, new NotesFrame(notesOnFirstStart, NotesFrame.NotesType.INFO));
        this.framesMap.put(HistoryFrame.class, new HistoryFrame());
        SettingsFrame settingsFrame = new SettingsFrame();
        this.framesMap.put(SettingsFrame.class, settingsFrame);
        this.framesMap.put(TestCasesFrame.class, new TestCasesFrame());
        this.framesMap.put(TooltipFrame.class, new TooltipFrame());
        this.framesMap.put(ChatHistoryFrame.class, new ChatHistoryFrame());
        this.framesMap.put(NotificationAlertFrame.class, new NotificationAlertFrame());
        this.framesMap.put(MercuryLoadingFrame.class, new MercuryLoadingFrame());
        this.framesMap.put(ChatScannerFrame.class, new ChatScannerFrame());
        this.framesMap.put(UpdateReadyFrame.class, new UpdateReadyFrame());
        this.framesMap.put(TaskBarFrame.class, taskBarFrame);
        this.framesMap.put(SetUpLocationFrame.class, new SetUpLocationFrame());
        this.framesMap.put(SetUpScaleFrame.class, new SetUpScaleFrame());
        this.framesMap.put(AlertFrame.class, new AlertFrame());
        this.framesMap.put(HelpIGFrame.class, new HelpIGFrame());

        Thread framesMapInit = new Thread(() -> {
            List<Thread> loopThread = new ArrayList<>();
            this.framesMap.forEach((k, v) -> {
                loopThread.add(new Thread(v::init));
            });
            loopThread.forEach(Thread::start);
            loopThread.forEach((thread) -> {
                try {
                    thread.join();
                } catch (Exception e) {
                    logger.error(e);
                }
            });
        });
        framesMapInit.start();
        ApplicationDescriptor config = Configuration.get().applicationConfiguration().get();
        Thread framesMap = new Thread(() -> {
            List<Thread> loopThread = new ArrayList<>();
            this.framesMap.forEach((k, frame) -> {
                loopThread.add(new Thread(() -> {
                    if (frame instanceof AbstractComponentFrame) {
                        if (config.getFadeTime() > 0) {
                            ((AbstractComponentFrame) frame).enableHideEffect(config.getFadeTime(),
                                    config.getMinOpacity(),
                                    config.getMaxOpacity());
                        } else {
                            ((AbstractComponentFrame) frame).disableHideEffect();
                            frame.setOpacity(config.getMaxOpacity() / 100f);
                        }
                    }
                }));
            });
            loopThread.forEach(Thread::start);
            loopThread.forEach((thread) -> {
                try {
                    thread.join();
                } catch (Exception e) {
                    logger.error(e);
                }
            });
        });
        framesMap.start();
        Thread routManager = new Thread(() -> {
            new SettingsRoutManager(settingsFrame);
        });
        routManager.start();
        this.subscribe();
        this.adrManager.load();

        try {
            baseInitThread.join();
            framesMapInit.join();
            framesMap.join();
            routManager.join();

        } catch (InterruptedException e) {
            logger.error(e);
        }
        MercuryStoreCore.uiLoadedSubject.onNext(true);
    }

    @Override
    public void subscribe() {
        MercuryStoreCore.showPatchNotesSubject.subscribe(json -> {
            NotesLoader notesLoader = new NotesLoader();
            List<Note> notes = notesLoader.getPatchNotesFromString(json);
            NotesFrame patchNotesFrame = new NotesFrame(notes, NotesFrame.NotesType.PATCH);
            patchNotesFrame.init();
            patchNotesFrame.setFrameTitle("MercuryTrade v" + notesLoader.getVersionFrom(json));
            patchNotesFrame.showComponent();
        });
        MercuryStoreUI.packSubject.subscribe(className -> this.framesMap.get(className).pack());
        MercuryStoreUI.repaintSubject.subscribe(className -> {
            this.framesMap.get(className).repaint();
            this.framesMap.get(className).revalidate();
        });
    }

    public void exit() {
        this.framesMap.forEach((k, v) -> v.setVisible(false));
        MercuryStoreCore.shutdownAppSubject.onNext(true);
    }

    public void exitForUpdate() {
        this.framesMap.forEach((k, v) -> v.setVisible(false));
        MercuryStoreCore.shutdownForUpdateSubject.onNext(true);
    }

    public void showFrame(Class frameClass) {
        this.framesMap.get(frameClass).showComponent();
    }

    public void preShowFrame(Class frameClass) {
        this.framesMap.get(frameClass).setPrevState(FrameVisibleState.SHOW);
    }

    public void hideFrame(Class frameClass) {
        this.framesMap.get(frameClass).hideComponent();
    }

    public void hideOrShowFrame(Class frameClass) {
        AbstractOverlaidFrame frame = this.framesMap.get(frameClass);
        if (frame != null && frame.isVisible()) {
            hideFrame(frameClass);
        } else {
            showFrame(frameClass);
        }
    }

    public void hideOrShowFrame(Class frameClass, FrameVisibleState state) {
        AbstractOverlaidFrame frame = this.framesMap.get(frameClass);
        if (state.equals(FrameVisibleState.SHOW)) {
            showFrame(frameClass);
        } else {
            hideFrame(frameClass);
        }
    }

    public void enableMovementExclude(Class... frames) {
        this.locationCommander.setUpAllExclude(frames);
    }

    public void enableOrDisableMovementDirect(Class frameClass) {
        this.locationCommander.setOrEndUp(frameClass, false);
    }

    public void disableMovement() {
        this.locationCommander.endUpAll();
    }

    public void disableMovement(Class frameClass) {
        this.locationCommander.endUp(frameClass);
    }

    public void enableScale() {
        this.showFrame(SetUpScaleFrame.class);
        this.scaleCommander.setUpAll();
    }

    public void disableScale() {
        this.hideFrame(SetUpScaleFrame.class);
        this.scaleCommander.endUpAll();
    }

    public void performAdr() {
        if (this.adrManager.getState().equals(AdrState.DEFAULT)) {
            this.adrManager.enableSettings();
        } else {
            this.adrManager.disableSettings();
        }
    }

    public void restoreDefaultLocation() {
        this.framesMap.forEach((k, v) -> {
            FramesConfigurationServiceImpl service = (FramesConfigurationServiceImpl) Configuration.get()
                    .framesConfiguration();
            if (service != null) {
                FrameDescriptor settings = service.getDefault().get(k.getSimpleName());
                if (settings != null) {
                    v.setLocation(settings.getFrameLocation());
                    if (v instanceof AbstractMovableComponentFrame) {
                        ((AbstractMovableComponentFrame) v).onLocationChange(settings.getFrameLocation());
                    }
                }
            }
        });
    }

    public void setLocationToCurrentMonitor() {
        this.framesMap.forEach((k, v) -> {
            if (v.getClass().equals(ItemsGridFrame.class)) {
                Point location = v.getLocation();
                System.out.println(location);
            }
            Point frameLocation = v.getLocation();
            frameLocation.x += getWindowLeftLocation();
            v.setLocation(frameLocation);
            if (v instanceof AbstractMovableComponentFrame) {
                ((AbstractMovableComponentFrame) v).onLocationChange(frameLocation);
            }
        });

        this.adrManager.getFrames().forEach((v) -> {
            Point frameLocation = v.getLocation();
            frameLocation.x += getWindowLeftLocation();
            v.setLocation(frameLocation);
        });
    }


    private int getWindowLeftLocation() {
        if (SystemUtils.IS_OS_WINDOWS) {
            return WindowUtils.getAllWindows(false).stream().filter(window -> {
                char[] className = new char[512];
                User32.INSTANCE.GetClassName(window.getHWND(), className, 512);
                return Native.toString(className).equals("POEWindowClass");
            }).map(it -> {
                WinDef.RECT rect = new WinDef.RECT();
                User32.INSTANCE.GetWindowRect(it.getHWND(), rect);
                return rect.left;
            }).findAny().orElse(0);
        } else {
            return 0;
        }
    }

    private void createTrayIcon() {
        if (SystemUtils.IS_OS_WINDOWS) {
            JPopupMenu trayMenu = this.componentsFactory.getContextPanel();

            JMenuItem exit = this.componentsFactory.getMenuItem(TranslationKey.exit.value(), null);
            exit.addActionListener(e -> {
                exit();
            });

            JMenuItem restore = this.componentsFactory.getMenuItem(TranslationKey.restore_default_location.value(), null);
            restore.addActionListener(e -> {
                FramesManager.INSTANCE.restoreDefaultLocation();
                hideSystemTray(trayMenu);
            });


            JMenuItem settings = this.componentsFactory.getMenuItem(TranslationKey.settings.value(), null);
            settings.addActionListener(e -> {
                FramesManager.INSTANCE.showFrame(SettingsFrame.class);
                hideSystemTray(trayMenu);
            });

            JCheckBoxMenuItem vulkanSupport = this.componentsFactory.checkBoxMenuItem(VulkanManager.INSTANCE.getSetting(), TranslationKey.vulkan_support.value());
            vulkanSupport.addItemListener(e -> {
                VulkanManager.INSTANCE.changeSetting();
                MercuryStoreUI.settingsSaveSubject.onNext(true);
            });

            trayMenu.add(restore);
            trayMenu.add(settings);
            trayMenu.add(vulkanSupport);
            trayMenu.add(exit);

            BufferedImage icon = null;
            try {
                icon = ImageIO.read(getClass().getClassLoader().getResource(IconConst.APP_ICON));
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.trayIcon = new TrayIcon(icon, "MercuryTrade");
            this.trayIcon.setImageAutoSize(true);

            trayIcon.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseReleased(java.awt.event.MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        showJPopupMenu(e, trayMenu);
                    }
                }
            });

            MercuryStoreCore.hideSystemTraySubject.subscribe(nativeMouseEvent -> {
                if (!isClickInsidePopup(nativeMouseEvent, trayMenu)) {
                    hideSystemTray(trayMenu);
                }
            });

            SystemTray tray = SystemTray.getSystemTray();
            try {
                tray.add(this.trayIcon);
            } catch (AWTException e) {
                e.printStackTrace();
            }
        }
    }

    private static void showJPopupMenu(MouseEvent e, JPopupMenu popupMenu) {
        // Position the JPopupMenu near the tray icon
        Point point = e.getPoint();
        SwingUtilities.invokeLater(() -> {
            //popupMenu.setInvoker(null);
            popupMenu.setVisible(true);
            popupMenu.setLocation(point.x, point.y - (int) popupMenu.bounds().getHeight());
            MercuryStoreCore.enableDisableHideSystemTrayListenerSubject.onNext(true);
        });

    }

    private static boolean isClickInsidePopup(NativeMouseEvent me, JPopupMenu popupMenu) {
        // Calculate the bounds of the JPopupMenu
        if (popupMenu.isShowing()) {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice g2d = ge.getDefaultScreenDevice();
            double scale = g2d.getDisplayMode().getWidth() / Toolkit.getDefaultToolkit().getScreenSize().getWidth();

            Rectangle bounds = popupMenu.getBounds();
            Point locationOnScreen = popupMenu.getLocationOnScreen();
            bounds.setLocation(locationOnScreen);

            logger.warn("point");
            logger.warn(me.getX());
            logger.warn(me.getY());
            logger.warn("point scaled");
            logger.warn(me.getX() / scale);
            logger.warn(me.getY() / scale);
            logger.warn("bounds");
            logger.warn(bounds.getX());
            logger.warn(bounds.getY());
            return bounds.contains(me.getX() / scale, me.getY() / scale);
        }
        return false;
    }

    private static class FramesManagerHolder {
        static final FramesManager HOLDER_INSTANCE = new FramesManager();
    }

    private void hideSystemTray(JPopupMenu trayMenu) {
        trayMenu.setVisible(false);
        MercuryStoreCore.enableDisableHideSystemTrayListenerSubject.onNext(false);
    }
}
