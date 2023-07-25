package ru.ccooll.rabbitclient.error;

import com.rabbitmq.client.ExceptionHandler;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Callable;

/**
 * represents a functional interface of global error handler that can be used
 * for debugging and logging
 *
 * @see ExceptionHandler for built-in handlers
 */
@FunctionalInterface
public interface ErrorHandler {

    void handle(Throwable er);

    default <T> @Nullable T computeSafe(Callable<T> callable) {
        T value = null;
        try {
            value = callable.call();
        } catch (Throwable er) {
            handle(er);
        }
        return value;
    }

    default void computeSafe(ThrowableRunnable throwableRunnable) {
        try {
            throwableRunnable.run();
        } catch (Throwable ex) {
            handle(ex);
        }
    }

    @FunctionalInterface
    interface ThrowableRunnable {

        void run() throws Throwable;
    }
}
