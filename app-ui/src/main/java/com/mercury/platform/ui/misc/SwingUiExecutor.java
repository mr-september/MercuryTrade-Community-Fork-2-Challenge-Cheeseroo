package com.mercury.platform.ui.misc;

import rx.functions.Action1;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

public final class SwingUiExecutor {
    private SwingUiExecutor() {
    }

    // Schedule Swing work on the EDT without blocking the caller.
    public static void run(Runnable action) {
        if (SwingUtilities.isEventDispatchThread()) {
            action.run();
        } else {
            SwingUtilities.invokeLater(action);
        }
    }

    public static <T> T call(Callable<T> action) {
        if (SwingUtilities.isEventDispatchThread()) {
            return invoke(action);
        }

        AtomicReference<T> result = new AtomicReference<>();
        AtomicReference<Throwable> failure = new AtomicReference<>();
        try {
            SwingUtilities.invokeAndWait(() -> {
                try {
                    result.set(action.call());
                } catch (Throwable t) {
                    failure.set(t);
                }
            });
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting for Swing task.", e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("Swing task failed before completion.", e.getCause());
        }

        Throwable throwable = failure.get();
        if (throwable instanceof RuntimeException) {
            throw (RuntimeException) throwable;
        }
        if (throwable instanceof Error) {
            throw (Error) throwable;
        }
        if (throwable != null) {
            throw new IllegalStateException("Failed to execute Swing task.", throwable);
        }
        return result.get();
    }

    public static Runnable onEdt(Runnable action) {
        return () -> run(action);
    }

    public static <T> Action1<T> onEdt(Action1<T> action) {
        return value -> run(() -> action.call(value));
    }

    private static <T> T invoke(Callable<T> action) {
        try {
            return action.call();
        } catch (RuntimeException e) {
            throw e;
        } catch (Error e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to execute Swing task.", e);
        }
    }
}
