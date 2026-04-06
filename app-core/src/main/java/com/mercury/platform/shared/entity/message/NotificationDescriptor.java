package com.mercury.platform.shared.entity.message;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@EqualsAndHashCode(exclude = {"relatedMessages"})
public class NotificationDescriptor {
    private static final Pattern SOURCE_PREFIX_PATTERN = Pattern.compile("^(?:@(?:From|To)\\s+)?(?:<[^>]+>\\s+)?[^\\s:]+:\\s+(.*)$");

    private String sourceString;
    private String whisperNickname;
    private NotificationType type;
    private List<PlainMessageDescriptor> relatedMessages = new ArrayList<>();

    public String getWhisperNickname() {
        return whisperNickname;
    }

    public void setWhisperNickname(String whisperNickname) {
        this.whisperNickname = StringUtils.trim(whisperNickname);
    }

    public String getSourceString() {
        return sourceString;
    }

    public void setSourceString(String sourceString) {
        final String trimmed = StringUtils.trim(sourceString);
        if (trimmed == null) {
            this.sourceString = null;
            return;
        }

        final Matcher matcher = SOURCE_PREFIX_PATTERN.matcher(trimmed);
        this.sourceString = matcher.matches() ? matcher.group(1) : trimmed;
    }
}
