package com.mercury.platform.shared.config.descriptor;

import lombok.Data;

import java.io.Serializable;

@Data
public class ScannerDescriptor implements Serializable {
    private String words;
    private String responseMessage;
    private boolean enablePlusTextDetection = true;
    private String defaultPlusTextResponse = "I'll join";
}
