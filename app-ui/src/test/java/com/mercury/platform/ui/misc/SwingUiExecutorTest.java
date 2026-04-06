package com.mercury.platform.ui.misc;

import org.junit.jupiter.api.Test;
import rx.functions.Action1;

import javax.swing.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SwingUiExecutorTest {

    @Test
    void shouldRunBackgroundWorkOnEdt() throws InterruptedException {
        AtomicReference<Thread> executedThread = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        Thread worker = new Thread(() -> SwingUiExecutor.run(() -> {
            assertTrue(SwingUtilities.isEventDispatchThread());
            executedThread.set(Thread.currentThread());
            latch.countDown();
        }));

        worker.start();
        worker.join();

        assertTrue(latch.await(2, TimeUnit.SECONDS));
        assertNotNull(executedThread.get());
        assertTrue(executedThread.get().getName().startsWith("AWT-EventQueue"));
    }

    @Test
    void shouldScheduleWithoutBlockingBackgroundCaller() throws InterruptedException {
        CountDownLatch actionStarted = new CountDownLatch(1);
        CountDownLatch releaseAction = new CountDownLatch(1);

        Thread worker = new Thread(() -> SwingUiExecutor.run(() -> {
            actionStarted.countDown();
            try {
                releaseAction.await(2, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Interrupted while running EDT task.", e);
            }
        }));

        worker.start();
        assertTrue(actionStarted.await(2, TimeUnit.SECONDS));
        worker.join(500);
        assertFalse(worker.isAlive());
        releaseAction.countDown();
    }

    @Test
    void shouldReturnValuesFromEdtTasks() {
        String threadName = SwingUiExecutor.call(() -> {
            assertTrue(SwingUtilities.isEventDispatchThread());
            return Thread.currentThread().getName();
        });

        assertTrue(threadName.startsWith("AWT-EventQueue"));
    }

    @Test
    void shouldExecuteInlineWhenAlreadyOnEdt() throws Exception {
        AtomicReference<Thread> outerThread = new AtomicReference<>();
        AtomicReference<Thread> innerThread = new AtomicReference<>();

        SwingUtilities.invokeAndWait(() -> {
            outerThread.set(Thread.currentThread());
            SwingUiExecutor.run(() -> innerThread.set(Thread.currentThread()));
        });

        assertSame(outerThread.get(), innerThread.get());
        assertEquals(outerThread.get(), innerThread.get());
    }

    @Test
    void shouldWrapConsumersOntoEdt() throws InterruptedException {
        AtomicReference<String> value = new AtomicReference<>();
        AtomicReference<Thread> executedThread = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);
        Action1<String> callback = SwingUiExecutor.onEdt(input -> {
            assertTrue(SwingUtilities.isEventDispatchThread());
            value.set(input);
            executedThread.set(Thread.currentThread());
            latch.countDown();
        });

        Thread worker = new Thread(() -> callback.call("settings"));
        worker.start();
        worker.join();

        assertTrue(latch.await(2, TimeUnit.SECONDS));
        assertEquals("settings", value.get());
        assertNotNull(executedThread.get());
        assertTrue(executedThread.get().getName().startsWith("AWT-EventQueue"));
    }

    @Test
    void shouldWrapRunnablesOntoEdt() throws InterruptedException {
        AtomicReference<Thread> executedThread = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);
        Runnable callback = SwingUiExecutor.onEdt(() -> {
            assertTrue(SwingUtilities.isEventDispatchThread());
            executedThread.set(Thread.currentThread());
            latch.countDown();
        });

        Thread worker = new Thread(callback);
        worker.start();
        worker.join();

        assertTrue(latch.await(2, TimeUnit.SECONDS));
        assertNotNull(executedThread.get());
        assertTrue(executedThread.get().getName().startsWith("AWT-EventQueue"));
    }
}
