package com.mercury.platform.shared.config.descriptor;

import com.mercury.platform.Languages;
import com.mercury.platform.core.misc.WhisperNotifierStatus;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class ApplicationDescriptor implements Serializable {
    private WhisperNotifierStatus notifierStatus;
    private int minOpacity;
    private int maxOpacity;
    private int fadeTime;
    private String gamePath;
    private String pushbulletAccessToken;
    private boolean showOnStartUp;
    private boolean itemsGridEnable;
    private boolean checkOutUpdate;
    private boolean hideTaskbarUntilHover;
    private boolean poe2;
    private Languages languages = Languages.en;
    private boolean disableGameToFront;
    private LocalDateTime lastCheckForUpdateDate;
}
