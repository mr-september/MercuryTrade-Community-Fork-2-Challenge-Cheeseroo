package com.mercury.platform.core.utils.error;

import com.mercury.platform.shared.entity.message.MercuryError;
import com.mercury.platform.shared.store.MercuryStoreCore;

public final class ErrorNotifier {
    private static final String DEFAULT_MESSAGE = "Unexpected runtime error.";

    private ErrorNotifier() {
    }

    public static void notify(Throwable throwable) {
        notify(DEFAULT_MESSAGE, throwable);
    }

    public static void notify(String message, Throwable throwable) {
        MercuryStoreCore.errorHandlerSubject.onNext(new MercuryError(message, throwable));
    }
}
