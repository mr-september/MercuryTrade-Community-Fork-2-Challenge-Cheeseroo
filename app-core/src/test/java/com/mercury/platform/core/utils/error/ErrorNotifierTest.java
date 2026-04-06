package com.mercury.platform.core.utils.error;

import com.mercury.platform.shared.entity.message.MercuryError;
import com.mercury.platform.shared.store.MercuryStoreCore;
import org.junit.jupiter.api.Test;
import rx.Subscription;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class ErrorNotifierTest {

    @Test
    void shouldPublishMercuryErrorsWithContext() {
        AtomicReference<MercuryError> captured = new AtomicReference<>();
        Subscription subscription = MercuryStoreCore.errorHandlerSubject.subscribe(captured::set);
        IllegalStateException failure = new IllegalStateException("boom");

        try {
            ErrorNotifier.notify("Failed to do thing.", failure);

            assertNotNull(captured.get());
            assertEquals("Failed to do thing.", captured.get().getErrorMessage());
            assertSame(failure, captured.get().getStackTrace());
        } finally {
            subscription.unsubscribe();
        }
    }

    @Test
    void shouldPublishMercuryErrorsWithDefaultMessage() {
        AtomicReference<MercuryError> captured = new AtomicReference<>();
        Subscription subscription = MercuryStoreCore.errorHandlerSubject.subscribe(captured::set);
        IllegalArgumentException failure = new IllegalArgumentException("broken");

        try {
            ErrorNotifier.notify(failure);

            assertNotNull(captured.get());
            assertEquals("Unexpected runtime error.", captured.get().getErrorMessage());
            assertSame(failure, captured.get().getStackTrace());
        } finally {
            subscription.unsubscribe();
        }
    }
}
