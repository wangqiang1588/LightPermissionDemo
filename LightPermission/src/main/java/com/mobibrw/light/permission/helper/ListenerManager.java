package com.mobibrw.light.permission.helper;

import android.support.annotation.NonNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

/**
 * Created by longsky on 2017/9/3.
 */

public class ListenerManager<T> {

    private final LinkedHashSet<EquatableWeakReference<T>> listeners = new LinkedHashSet<>();
    private final Object lock = new Object();
    private final LinkedHashMap<RunnableEx<T>, WeakReference<T>> runnableExs = new LinkedHashMap<>();
    private final android.os.Handler mHandler = new android.os.Handler(android.os.Looper.getMainLooper());

    public boolean registerListener(@NonNull final T listener) {
        final EquatableWeakReference<T> equatableWeakListener = new EquatableWeakReference<>(listener);
        synchronized (lock) {
            if (listeners.contains(equatableWeakListener)) {
                return false;
            }
            listeners.add(equatableWeakListener);
            cancelPostedRunnable(listener);
            cleanupDeadListener();
        }
        return true;
    }

    public boolean unRegisterListener(@NonNull final T listener) {
        final EquatableWeakReference<T> equatableWeakListener = new EquatableWeakReference<>(listener);
        synchronized (lock) {
            if (!listeners.contains(equatableWeakListener)) {
                return false;
            }
            listeners.remove(equatableWeakListener);
            cancelPostedRunnable(listener);
            cleanupDeadListener();
        }
        return true;
    }

    private void cleanupDeadListener() {
        final ArrayList<EquatableWeakReference<T>> cleanupArr = new ArrayList<>();
        for (EquatableWeakReference<T> weak : listeners) {
            if (null != weak) {
                final T strong = weak.get();
                if (null == strong) {
                    cleanupArr.add(weak);
                }
            }
        }
        // cleanup dead listener
        for (EquatableWeakReference<T> weak : cleanupArr) {
            listeners.remove(weak);
        }
    }

    private void cancelPostedRunnable(@NonNull final T listener) {
        final ArrayList<RunnableEx<T>> runnableArr = new ArrayList<>();
        for (LinkedHashMap.Entry<RunnableEx<T>, WeakReference<T>> element : runnableExs.entrySet()) {
            final RunnableEx<T> rx = element.getKey();
            if (null != rx) {
                final WeakReference<T> weak = element.getValue();
                if (null != weak) {
                    final T strong = weak.get();
                    if (null != strong) {
                        if (strong.equals(listener)) {
                            mHandler.removeCallbacks(rx);
                            runnableArr.add(rx);
                        }
                    } else {
                        mHandler.removeCallbacks(rx);
                        runnableArr.add(rx);
                    }
                } else {
                    mHandler.removeCallbacks(rx);
                    runnableArr.add(rx);
                }
            }
        }
        for (RunnableEx<T> r : runnableArr) {
            runnableExs.remove(r);
        }
    }

    public void clearListener() {
        synchronized (lock) {
            for (EquatableWeakReference<T> listener : listeners) {
                if (null != listener) {
                    final T strongListener = listener.get();
                    if (null != strongListener) {
                        cancelPostedRunnable(strongListener);
                    }
                }
            }
            listeners.clear();
            runnableExs.clear();
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    public void forEachListener(@NonNull final IForEachListener<T> listener) {
        final ArrayList<EquatableWeakReference<T>> cleanupArr = new ArrayList<>();
        ArrayList<EquatableWeakReference<T>> listListeners;
        synchronized (lock) {
            listListeners = new ArrayList<>(listeners);
        }
        for (EquatableWeakReference<T> weak : listListeners) {
            if (null != weak) {
                final T strong = weak.get();
                if (null != strong) {
                    listener.onForEachListener(strong);
                } else {
                    cleanupArr.add(weak);
                }
            }
        }
        synchronized (lock) {
            // cleanup dead listener
            for (EquatableWeakReference<T> weak : cleanupArr) {
                listeners.remove(weak);
            }
        }
    }

    public boolean postRunnable(@NonNull final T listener, @NonNull final Runnable runnable) {
        final RunnableEx<T> runnableEx = new RunnableEx<>(runnableExs, lock, runnable);
        synchronized (lock) {
            runnableExs.put(runnableEx, new WeakReference<>(listener));
            if (!mHandler.post(runnableEx)) {
                runnableExs.remove(runnableEx);
                return false;
            }
        }
        return true;
    }

    public interface IForEachListener<T> {
        void onForEachListener(@NonNull final T listener);
    }

    private static final class RunnableEx<T> implements Runnable {

        private final Object lock;
        private Runnable runnable;
        private LinkedHashMap<RunnableEx<T>, WeakReference<T>> container;

        public RunnableEx(@NonNull final LinkedHashMap<RunnableEx<T>, WeakReference<T>> container, @NonNull final Object criticalSection, @NonNull final Runnable runnable) {
            this.runnable = runnable;
            this.container = container;
            lock = criticalSection;
        }

        public void run() {
            synchronized (lock) {
                container.remove(this);
            }
            runnable.run();
        }
    }
}