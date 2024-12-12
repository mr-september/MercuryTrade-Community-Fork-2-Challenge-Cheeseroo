package com.mercury.platform.core.misc;

import com.mercury.platform.TranslationKey;

public enum WhisperNotifierStatus {
    ALWAYS {
        @Override
        public String asPretty() {
            return TranslationKey.always_play_a_sound.value();
        }
    },
    ALTAB {
        @Override
        public String asPretty() {
            return TranslationKey.only_when_tabbed_out.value();
        }
    },
    NONE {
        @Override
        public String asPretty() {
            return TranslationKey.never.value();
        }
    };

    public static WhisperNotifierStatus valueOfPretty(String s) {
        for (WhisperNotifierStatus status : WhisperNotifierStatus.values()) {
            if (status.asPretty().equals(s)) {
                return status;
            }
        }
        return null;
    }

    public abstract String asPretty();
}
