package com.mercury.platform.ui.frame.titled;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ChatScannerFrameTest {

    @Test
    void extractScannerPayload_supportsTradeAndGlobalSeparators() {
        assertEquals(
                "<AFK> Trader: hi there",
                ChatScannerFrame.extractScannerPayload("2026/04/06 12:00:00 ] $ <AFK> Trader: hi there"));
        assertEquals(
                "GlobalUser: selling carry",
                ChatScannerFrame.extractScannerPayload("2026/04/06 12:00:00 ] # GlobalUser: selling carry"));
        assertEquals(
                "SelfGlobalUser: testing carry service",
                ChatScannerFrame.extractScannerPayload("2026/04/06 12:00:00 ] SelfGlobalUser: testing carry service"));
        assertEquals(
                "GlobalUser: selling [carry] service",
                ChatScannerFrame.extractScannerPayload("2026/04/06 12:00:00 ] # GlobalUser: selling [carry] service"));
    }

    @Test
    void parseScannerMessage_extractsNicknameAndBodyWithOptionalPrefix() {
        assertArrayEquals(
                new String[]{"Trader", " hello +carry"},
                ChatScannerFrame.parseScannerMessage("<AFK> Trader: hello +carry"));
        assertArrayEquals(
                new String[]{"GlobalUser", " selling carry"},
                ChatScannerFrame.parseScannerMessage("GlobalUser: selling carry"));
        assertArrayEquals(
                new String[]{"GlobalUser", "selling carry"},
                ChatScannerFrame.parseScannerMessage("GlobalUser:selling carry"));
    }

    @Test
    void parseScannerMessage_returnsNullForUnexpectedMessageShape() {
        assertNull(ChatScannerFrame.parseScannerMessage("message without nickname separator"));
    }
}
