package com.mercury.platform.ui.components.panel.settings.page;

import com.mercury.platform.shared.entity.message.MercuryError;
import com.mercury.platform.shared.store.MercuryStoreCore;
import org.junit.jupiter.api.Test;
import rx.Subscription;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AboutPagePanelTest {

    @Test
    void shouldNotifyAndKeepPageRenderableWhenPatchNotesResourceIsMissing() {
        AtomicReference<MercuryError> captured = new AtomicReference<>();
        Subscription subscription = MercuryStoreCore.errorHandlerSubject.subscribe(captured::set);

        try {
            List<com.mercury.platform.patches.PatchNotes> patchNotes = AboutPagePanel.loadPatchNotes(null);

            assertNotNull(patchNotes);
            assertTrue(patchNotes.isEmpty());
            assertNotNull(captured.get());
            assertEquals("Failed to load MercuryChat patch notes.", captured.get().getErrorMessage());
            assertTrue(captured.get().getStackTrace() instanceof java.io.IOException);
        } finally {
            subscription.unsubscribe();
        }
    }

    @Test
    void shouldNotifyAndKeepPageRenderableWhenPatchNotesJsonIsInvalid() {
        AtomicReference<MercuryError> captured = new AtomicReference<>();
        Subscription subscription = MercuryStoreCore.errorHandlerSubject.subscribe(captured::set);

        try {
            List<com.mercury.platform.patches.PatchNotes> patchNotes = AboutPagePanel.loadPatchNotes(
                    new ByteArrayInputStream("not json".getBytes(StandardCharsets.UTF_8)));

            assertNotNull(patchNotes);
            assertTrue(patchNotes.isEmpty());
            assertNotNull(captured.get());
            assertEquals("Failed to load MercuryChat patch notes.", captured.get().getErrorMessage());
            assertTrue(captured.get().getStackTrace() instanceof RuntimeException);
        } finally {
            subscription.unsubscribe();
        }
    }
}
