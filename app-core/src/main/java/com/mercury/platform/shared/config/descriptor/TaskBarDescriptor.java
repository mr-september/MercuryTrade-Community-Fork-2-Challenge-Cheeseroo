package com.mercury.platform.shared.config.descriptor;

import lombok.Data;

import java.io.Serializable;

@Data
public class TaskBarDescriptor implements Serializable {
    private boolean inGameDnd;
    private boolean pushbulletOn;
    private String dndResponseText = "Response message";
    private String joinChannelNumber = "820";
    private HotKeyDescriptor hideoutHotkey = new HotKeyDescriptor();
    private HotKeyDescriptor helpIGHotkey = new HotKeyDescriptor();
}
