package com.mercury.platform.core.utils.interceptor.plain;

import com.mercury.platform.shared.MainWindowHWNDFetch;
import org.apache.commons.lang3.StringUtils;

public class EngIncLocalizationMatcher extends LocalizationMatcher {
    @Override
    public boolean isSuitableFor(String message) {
        if (MainWindowHWNDFetch.INSTANCE.isPoe2()) {
            return message.contains("@");
        } else {
            return message.contains("@From");
        }
    }

    @Override
    public boolean isIncoming() {
        return true;
    }

    @Override
    public String trimString(String message) {
        if (MainWindowHWNDFetch.INSTANCE.isPoe2()) {
            return StringUtils.substringAfter(message, "@");
        } else {
            return StringUtils.substringAfter(message, "@From");
        }
    }
}