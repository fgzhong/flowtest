package com.mypro.spider.utils;

import com.google.common.collect.Maps;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author fgzhong
 * @description: 线程执行时间限制，超时 interrupt
 * @since 2019/1/26
 */
public class TimeoutThreadPoolExecutor extends ThreadPoolExecutor {
    private final long timeout;
    private final TimeUnit timeoutUnit;
    private final AtomicInteger active = new AtomicInteger(0);
    private int threadNum;


    private final ConcurrentMap<Runnable, ScheduledFuture> runningTasks = Maps.newConcurrentMap();
    private final ScheduledExecutorService timeoutExecutor = Executors.newScheduledThreadPool(1);

    public static TimeoutThreadPoolExecutor newFixedThreadPool(int nThreads,long timeout, TimeUnit timeoutUnit) {
        return new TimeoutThreadPoolExecutor(nThreads, nThreads,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), timeout, timeoutUnit);
    }

    public TimeoutThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, long timeout, TimeUnit timeoutUnit) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        this.threadNum = corePoolSize;
        this.timeout = timeout;
        this.timeoutUnit = timeoutUnit;
    }

    public TimeoutThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, long timeout, TimeUnit timeoutUnit) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
        this.threadNum = corePoolSize;
        this.timeout = timeout;
        this.timeoutUnit = timeoutUnit;
    }

    public TimeoutThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler, long timeout, TimeUnit timeoutUnit) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
        this.threadNum = corePoolSize;
        this.timeout = timeout;
        this.timeoutUnit = timeoutUnit;
    }

    public TimeoutThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler, long timeout, TimeUnit timeoutUnit) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        this.threadNum = corePoolSize;
        this.timeout = timeout;
        this.timeoutUnit = timeoutUnit;
    }

    public int getActive() {
        return active.get();
    }

    public boolean isReady() {
        return active.get() < this.threadNum;
    }

    @Override
    public void execute(Runnable command) {
        active.incrementAndGet();
        super.execute(command);
    }

    @Override
    public void shutdown() {
        timeoutExecutor.shutdown();
        super.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        timeoutExecutor.shutdownNow();
        return super.shutdownNow();
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        if(timeout > 0) {
            final ScheduledFuture<?> scheduled = this.timeoutExecutor.schedule(new TimeoutTask(t), this.timeout, this.timeoutUnit);
            runningTasks.put(r, scheduled);
        }
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        ScheduledFuture timeoutTask = this.runningTasks.remove(r);
        active.decrementAndGet();
        if(timeoutTask != null) {
            timeoutTask.cancel(false);
        }
    }



    class TimeoutTask implements Runnable {
        private final Thread thread;

        TimeoutTask(Thread thread) {
            this.thread = thread;
        }

        @Override
        public void run() {
            thread.interrupt();
        }
    }
}
