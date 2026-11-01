package com.artificialintelligence.patterns.bulkhead;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Bulkhead implementation backed by a bounded {@link ThreadPoolExecutor}. Calls are
 * rejected with {@link BulkheadFullException} when both the pool and its queue are
 * saturated.
 */
public class ThreadPoolBulkhead implements Bulkhead {

    private final String name;
    private final BulkheadConfig config;
    private final ThreadPoolExecutor executor;
    private final Semaphore admission;

    public ThreadPoolBulkhead(String name) {
        this(name, BulkheadConfig.ofDefaults());
    }

    public ThreadPoolBulkhead(String name, BulkheadConfig config) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        this.name = name;
        this.config = config == null ? BulkheadConfig.ofDefaults() : config;
        int capacity = this.config.getMaxThreadPoolSize() + this.config.getQueueCapacity();
        this.admission = new Semaphore(capacity, true);
        this.executor = new ThreadPoolExecutor(
                this.config.getCoreThreadPoolSize(),
                this.config.getMaxThreadPoolSize(),
                this.config.getKeepAliveDuration().toMillis(),
                TimeUnit.MILLISECONDS,
                this.config.getQueueCapacity() == 0
                        ? new java.util.concurrent.SynchronousQueue<>()
                        : new LinkedBlockingQueue<>(this.config.getQueueCapacity()),
                new ThreadPoolExecutor.AbortPolicy());
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public BulkheadMetrics getMetrics() {
        return new BulkheadMetrics() {
            @Override
            public int getAvailableConcurrentCalls() {
                return admission.availablePermits();
            }

            @Override
            public int getMaxAllowedConcurrentCalls() {
                return config.getMaxThreadPoolSize() + config.getQueueCapacity();
            }
        };
    }

    @Override
    public <T> T executeSupplier(Supplier<T> supplier) {
        if (supplier == null) {
            throw new IllegalArgumentException("supplier must not be null");
        }
        if (!tryAcquirePermission()) {
            throw new BulkheadFullException(name);
        }
        Future<T> future;
        try {
            future = executor.submit(new Callable<T>() {
                @Override
                public T call() {
                    return supplier.get();
                }
            });
        } catch (RejectedExecutionException e) {
            releasePermission();
            throw new BulkheadFullException(name);
        }
        try {
            return future.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            future.cancel(true);
            throw new BulkheadFullException(name);
        } catch (java.util.concurrent.ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            if (cause instanceof Error) {
                throw (Error) cause;
            }
            throw new RuntimeException(cause);
        } finally {
            releasePermission();
        }
    }

    @Override
    public void executeRunnable(Runnable runnable) {
        if (runnable == null) {
            throw new IllegalArgumentException("runnable must not be null");
        }
        executeSupplier(new Supplier<Void>() {
            @Override
            public Void get() {
                runnable.run();
                return null;
            }
        });
    }

    @Override
    public boolean tryAcquirePermission() {
        Duration wait = config.getMaxWaitDuration();
        try {
            if (wait.isZero() || wait.isNegative()) {
                return admission.tryAcquire();
            }
            return admission.tryAcquire(wait.toMillis(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    @Override
    public void releasePermission() {
        admission.release();
    }

    @Override
    public int getAvailableConcurrentCalls() {
        return admission.availablePermits();
    }

    @Override
    public int getMaxAllowedConcurrentCalls() {
        return config.getMaxThreadPoolSize() + config.getQueueCapacity();
    }

    public int getActiveThreadCount() {
        return executor.getActiveCount();
    }

    public int getQueueSize() {
        return executor.getQueue().size();
    }

    public void shutdown() {
        executor.shutdown();
    }

    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return executor.awaitTermination(timeout, unit);
    }
}
