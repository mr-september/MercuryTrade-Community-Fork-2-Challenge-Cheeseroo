package com.mercury.platform.core.utils;

import com.mercury.platform.shared.config.Configuration;
import com.mercury.platform.shared.store.MercuryStoreCore;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class FileMonitor {
    public static final FileMonitor INSTANCE = new FileMonitor();

    private static final long POLLING_INTERVAL = 350;
    private final Logger logger = LogManager.getLogger(FileMonitor.class.getSimpleName());
    private MessageFileHandler fileHandler;
    private FileAlterationMonitor monitor;
    private File monitoredLogFile;

    private FileMonitor() {
        MercuryStoreCore.poeFolderChangedSubject.subscribe(state -> start());
    }

    public synchronized void start() {
        this.stopMonitor();

        File logFile = this.resolveLogFile();
        if (logFile == null) {
            return;
        }

        this.monitoredLogFile = logFile.getAbsoluteFile();
        if (this.fileHandler == null) {
            this.fileHandler = new MessageFileHandler(this.monitoredLogFile.getPath());
        } else {
            this.fileHandler.updateLogFilePath(this.monitoredLogFile.getPath());
        }
        FileAlterationObserver observer = new FileAlterationObserver(this.monitoredLogFile.getParentFile());
        FileAlterationListener listener = new FileAlterationListenerAdaptor() {
            @Override
            public void onFileChange(File file) {
                if (fileHandler != null && FileMonitor.this.isCurrentLogFile(file)) {
                    fileHandler.parse();
                }
            }
        };

        observer.addListener(listener);
        monitor = new FileAlterationMonitor(POLLING_INTERVAL, observer);
        try {
            monitor.start();
        } catch (Exception e) {
            logger.error("Failed to start the Path of Exile log monitor for {}", this.monitoredLogFile.getPath(), e);
            this.stopMonitor();
        }
    }

    private File resolveLogFile() {
        String gamePath = Configuration.get().applicationConfiguration().get().getGamePath();
        if (StringUtils.isBlank(gamePath)) {
            return null;
        }

        File folder = new File(gamePath, "logs");
        if (!folder.isDirectory()) {
            return null;
        }

        File clientLog = new File(folder, "Client.txt");
        if (clientLog.isFile()) {
            return clientLog;
        }

        File kakaoClientLog = new File(folder, "KakaoClient.txt");
        if (kakaoClientLog.isFile()) {
            return kakaoClientLog;
        }

        return null;
    }

    private boolean isCurrentLogFile(File file) {
        return this.monitoredLogFile != null && file != null
                && this.monitoredLogFile.equals(file.getAbsoluteFile());
    }

    private void stopMonitor() {
        this.monitoredLogFile = null;
        if (this.monitor == null) {
            return;
        }
        try {
            this.monitor.stop();
        } catch (Exception e) {
            logger.error("Error in FileMonitor: ", e);
        } finally {
            this.monitor = null;
        }
    }
}
