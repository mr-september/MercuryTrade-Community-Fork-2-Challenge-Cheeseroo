package com.mercury.platform.shared.entity.message;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(exclude = {"relatedMessages"})
public class NotificationDescriptor {
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
        this.sourceString = StringUtils.trim(sourceString).replaceFirst(".*?: ", "");
    }
}
