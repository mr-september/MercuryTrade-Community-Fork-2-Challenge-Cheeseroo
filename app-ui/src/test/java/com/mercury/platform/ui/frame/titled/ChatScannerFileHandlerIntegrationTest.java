package com.mercury.platform.ui.frame.titled;

import com.mercury.platform.core.utils.MessageFileHandler;
import com.mercury.platform.core.utils.interceptor.MessageInterceptor;
import com.mercury.platform.core.utils.interceptor.filter.MessageMatcher;
import com.mercury.platform.shared.config.Configuration;
import com.mercury.platform.shared.config.MercuryConfigManager;
import com.mercury.platform.shared.config.MercuryConfigurationSource;
import com.mercury.platform.shared.store.MercuryStoreCore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChatScannerFileHandlerIntegrationTest {

    @TempDir
    Path tempDir;

    @Test
    void parse_readsRealisticChatLinesWithoutDroppingBracketedPayloads() throws IOException {
        MercuryConfigManager configuration = new MercuryConfigManager(new MercuryConfigurationSource());
        try {
            configuration.load();
        } catch (Exception ignored) {
        }
        Configuration.set(configuration);

        Path clientLog = tempDir.resolve("Client.txt");
        Files.write(
                clientLog,
                "2026/04/06 12:00:00 ] # SeedUser: previous line".concat(System.lineSeparator()).getBytes(StandardCharsets.UTF_8));

        MessageFileHandler handler = new MessageFileHandler(clientLog.toString());
        AtomicReference<String> detectedPayload = new AtomicReference<>();
        MessageInterceptor interceptor = new MessageInterceptor() {
            @Override
            protected void process(String stubMessage) {
                detectedPayload.set(ChatScannerFrame.extractScannerPayload(stubMessage));
            }

            @Override
            protected MessageMatcher match() {
                return stubMessage -> ChatScannerFrame.extractScannerPayload(stubMessage).contains("[carry]");
            }
        };

        try {
            MercuryStoreCore.addInterceptorSubject.onNext(interceptor);

            String timestamp = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
            String chatLine = timestamp + " ] # GlobalUser: selling [carry] service" + System.lineSeparator();
            Files.write(clientLog, chatLine.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);

            handler.parse();

            assertEquals("GlobalUser: selling [carry] service", detectedPayload.get());
        } finally {
            MercuryStoreCore.removeInterceptorSubject.onNext(interceptor);
        }
    }

    @Test
    void parse_keepsDynamicInterceptorsWhenTheSameHandlerSwitchesLogFiles() throws IOException {
        MercuryConfigManager configuration = new MercuryConfigManager(new MercuryConfigurationSource());
        try {
            configuration.load();
        } catch (Exception ignored) {
        }
        Configuration.set(configuration);

        Path firstLog = tempDir.resolve("Client.txt");
        Path secondLog = tempDir.resolve("KakaoClient.txt");
        Files.write(firstLog, "2026/04/06 12:00:00 ] # SeedUser: previous line".concat(System.lineSeparator()).getBytes(StandardCharsets.UTF_8));
        Files.write(secondLog, "2026/04/06 12:00:00 ] # SeedUser: previous line".concat(System.lineSeparator()).getBytes(StandardCharsets.UTF_8));

        MessageFileHandler handler = new MessageFileHandler(firstLog.toString());
        AtomicReference<String> detectedPayload = new AtomicReference<>();
        MessageInterceptor interceptor = new MessageInterceptor() {
            @Override
            protected void process(String stubMessage) {
                detectedPayload.set(ChatScannerFrame.extractScannerPayload(stubMessage));
            }

            @Override
            protected MessageMatcher match() {
                return stubMessage -> ChatScannerFrame.extractScannerPayload(stubMessage).contains("carry service");
            }
        };

        try {
            MercuryStoreCore.addInterceptorSubject.onNext(interceptor);
            handler.updateLogFilePath(secondLog.toString());

            String timestamp = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
            String chatLine = timestamp + " ] # GlobalUser: selling carry service" + System.lineSeparator();
            Files.write(secondLog, chatLine.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);

            handler.parse();

            assertEquals("GlobalUser: selling carry service", detectedPayload.get());
        } finally {
            MercuryStoreCore.removeInterceptorSubject.onNext(interceptor);
        }
    }
}
