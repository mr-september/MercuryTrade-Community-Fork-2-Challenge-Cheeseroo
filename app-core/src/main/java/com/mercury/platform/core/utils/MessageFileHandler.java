package com.mercury.platform.core.utils;

import com.mercury.platform.core.utils.interceptor.*;
import com.mercury.platform.shared.AsSubscriber;
import com.mercury.platform.shared.store.MercuryStoreCore;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MessageFileHandler implements AsSubscriber {
    private static final int RECENT_MESSAGE_LIMIT = 30;
    private final SimpleDateFormat messageDateFormat = createMessageDateFormat();
    private final Logger logger = LogManager.getLogger(MessageFileHandler.class);
    private String logFilePath;
    private Date lastMessageDate = new Date();
    private final Pattern timestampPattern = Pattern.compile("20[2-9][0-9]\\/[0-2][0-9]\\/[0-3][0-9] [0-2][0-9]:[0-5][0-9]:[0-5][0-9]");

    private final List<MessageInterceptor> interceptors = new ArrayList<>();

    public MessageFileHandler(String logFilePath) {
        this.logFilePath = logFilePath;

        this.interceptors.add(new TradeIncMessagesInterceptor());
        this.interceptors.add(new TradeOutMessagesInterceptor());
        this.interceptors.add(new PlainMessageInterceptor());
        this.interceptors.add(new PlayerJoinInterceptor());
        this.interceptors.add(new PlayerLeftInterceptor());
        this.interceptors.add(new PlayerInaccessibleInterceptor());

        this.subscribe();
    }

    public void updateLogFilePath(String logFilePath) {
        this.logFilePath = logFilePath;
        this.lastMessageDate = createInterceptorAnchorDate();
    }

    public void parse() {
        List<String> resultMessages = this.readRecentMessages().stream()
                .filter(this::isMessageAfterLastReadDate)
                .collect(Collectors.toList());
        Collections.reverse(resultMessages);
        this.interceptors.forEach(interceptor -> {
            resultMessages.forEach(message -> {
                if (interceptor.match(message)) {
                    Date messageDate = this.parseMessageDate(message);
                    if (messageDate != null) {
                        this.lastMessageDate = messageDate;
                    }
                }
            });
        });
    }

    @Override
    public void subscribe() {
        MercuryStoreCore.addInterceptorSubject.subscribe(interceptor -> {
            this.interceptors.add(interceptor);
            this.lastMessageDate = createInterceptorAnchorDate();
        });
        MercuryStoreCore.removeInterceptorSubject.subscribe(interceptor -> {
            this.interceptors.remove(interceptor);
        });
    }

    private static Date createInterceptorAnchorDate() {
        long currentTimeMillis = System.currentTimeMillis();
        long anchoredToSeconds = (currentTimeMillis / 1000L) * 1000L;
        return new Date(Math.max(0L, anchoredToSeconds - 1000L));
    }

    private List<String> readRecentMessages() {
        List<String> stubMessages = new ArrayList<>();
        File logFile = new File(this.logFilePath);
        long length = logFile.length();
        if (length <= 0) {
            return stubMessages;
        }

        try (RandomAccessFile randomAccessFile = new RandomAccessFile(logFile, "r")) {
            int lines = 0;
            StringBuilder builder = new StringBuilder();
            long seekPosition = length - 1;
            randomAccessFile.seek(seekPosition);
            for (; seekPosition >= 0; --seekPosition) {
                randomAccessFile.seek(seekPosition);
                char c = (char) randomAccessFile.read();
                builder.append(c);
                if (c == '\n') {
                    builder.reverse();
                    String message = new String(builder.toString().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                    message = StringUtils.strip(message, null);
                    if (StringUtils.isNotBlank(message)) {
                        stubMessages.add(message);
                    }
                    lines++;
                    builder = new StringBuilder();
                    if (lines == RECENT_MESSAGE_LIMIT) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error in MessageFileHandler: ", e);
        }

        return stubMessages;
    }

    private boolean isMessageAfterLastReadDate(String message) {
        Matcher matcher = this.timestampPattern.matcher(message);
        if (!matcher.find()) {
            return false;
        }
        Date messageDate = this.parseMessageDate(message);
        return messageDate != null && messageDate.after(this.lastMessageDate);
    }

    private Date parseMessageDate(String message) {
        try {
            return this.messageDateFormat.parse(StringUtils.substring(message, 0, 20));
        } catch (ParseException | RuntimeException e) {
            logger.error("Error while parsing date from message: " + message, e);
            return null;
        }
    }

    private static SimpleDateFormat createMessageDateFormat() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        format.setLenient(false);
        return format;
    }
}
