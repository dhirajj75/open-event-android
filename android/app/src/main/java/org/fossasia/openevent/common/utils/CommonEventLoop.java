package org.fossasia.openevent.common.utils;

import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

public class CommonEventLoop {
    private ScheduledExecutorService m_executor = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(@NonNull Runnable r) {
            return new LooperThread(r);
        }
    });

    public void post(Runnable call) {
        m_executor.execute(call);

    }

    public void cancel(Future f) {
        if (f.isCancelled()) {
            return;
        }
        if (f.isDone()) {
            return;
        }
        f.cancel(true);
    }

    public Future delayPost(Runnable call, int nMillSec) {
        return m_executor.schedule(call, nMillSec, TimeUnit.MILLISECONDS);
    }

//    public void waitTask(Future f) {
//
//    }

    public void shutdown() {
        m_executor.shutdown();
    }

    /**
     * Use a Handler Thread.
     */
    class LooperThread extends Thread {

        private final Runnable runnable;

        public LooperThread(Runnable r) {
            super("CommonEventLoop");
            this.runnable = r;
        }

        @Override
        public void run() {
            Looper.prepare();
            try {
                runnable.run();
            } catch (Exception e) {
                Timber.e("Parsing Error Occurred at CommonEventLoop::run.");
            }
        }
    }
}
