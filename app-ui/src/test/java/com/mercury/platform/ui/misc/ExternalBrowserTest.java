package com.mercury.platform.ui.misc;

import com.mercury.platform.shared.entity.message.MercuryError;
import com.mercury.platform.shared.store.MercuryStoreCore;
import org.junit.jupiter.api.Test;
import rx.Subscription;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExternalBrowserTest {

    @Test
    void shouldReturnTrueWhenBrowserLaunchSucceeds() {
        boolean opened = ExternalBrowser.open(
                URI.create("https://example.com"),
                "Failed to open browser.",
                uri -> {
                });

        assertTrue(opened);
    }

    @Test
    void shouldPublishMercuryErrorWhenBrowseFails() {
        AtomicReference<MercuryError> captured = new AtomicReference<>();
        Subscription subscription = MercuryStoreCore.errorHandlerSubject.subscribe(captured::set);

        try {
            boolean opened = ExternalBrowser.open(
                    URI.create("https://example.com"),
                    "Failed to open browser.",
                    uri -> {
                        throw new IOException("no browser");
                    });

            assertFalse(opened);
            assertNotNull(captured.get());
            assertEquals("Failed to open browser.", captured.get().getErrorMessage());
            assertTrue(captured.get().getStackTrace() instanceof IOException);
        } finally {
            subscription.unsubscribe();
        }
    }
}
