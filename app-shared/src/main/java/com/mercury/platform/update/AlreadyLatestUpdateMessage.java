package com.mercury.platform.update;

import com.mercury.platform.TranslationKey;

/**
 * Created by Константин on 07.03.2017.
 */
public class AlreadyLatestUpdateMessage extends ServerMessage {
    public AlreadyLatestUpdateMessage() {
        super(TranslationKey.you_have_latest_version.value());
    }
}
