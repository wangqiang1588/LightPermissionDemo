package com.mobibrw.light.permission.biz;

import android.content.Context;
import android.support.annotation.AnyThread;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobibrw.light.permission.api.IPermission;
import com.mobibrw.light.permission.api.IPermissionApi;
import com.mobibrw.light.permission.api.IPermissionListener;
import com.mobibrw.light.permission.helper.ListenerManager;
import com.mobibrw.light.permission.helper.LogHelper;

import java.util.LinkedHashSet;

class PermissionBizBu implements IPermissionApi, IPermissionBizApi, IActivityListener {
    private final static String TAG = "PermissionBiz";
    static private volatile IPermissionBizApi mBizApi;
    protected final android.os.Handler mHandler = new android.os.Handler(android.os.Looper.getMainLooper());
    @NonNull
    private final LinkedHashSet<PermissionBiz> permissionBizArr = new LinkedHashSet<>();
    /**
     * if activity don't init success after 5 seconds , cancel this require
     */
    private final int permitMaxInitDelay = 5000;
    private Context context;
    private ActivityListener activityListener;
    private Runnable permitReqRunnable = null;
    private ListenerManager<IPermissionListener> listenersManager = new ListenerManager<>();
    private volatile boolean isDestroyed = false;

    private PermissionBizBu(final @NonNull Context c) {
        initBundle(c);
    }

    @Nullable
    static IPermissionBizApi bizApi() {
        return mBizApi;
    }

    @NonNull
    static public IPermissionApi api(final @NonNull Context c) {
        return PermissionBizBu.newInstance(c);
    }

    @NonNull
    private static PermissionBizBu newInstance(final @NonNull Context c) {
        if (null == mBizApi) {
            synchronized (PermissionBizBu.class) {
                if (null == mBizApi) {
                    new PermissionBizBu(c);
                }
            }
        }
        return (PermissionBizBu) mBizApi;
    }

    protected boolean isDestroyed() {
        return isDestroyed;
    }

    private void initBundle(final @NonNull Context c) {
        context = c;
        mBizApi = this;
        this.activityListener = new ActivityListener(context, this);
    }

    public void terminateBundle() {
        synchronized (PermissionBizBu.class) {
            if (null != mBizApi) {
                permissionBizArr.clear();
                activityListener.onBundleTerminate();
                mBizApi = null;
                permitReqRunnable = null;
                isDestroyed = true;
                listenersManager.clearListener();
                mHandler.removeCallbacksAndMessages(null);
            }
        }
    }

    /**
     * require permission
     *
     * @param c                              context for require Permission,if is null ,we will use application context, but if you want to show permission dialog on spec activity ,do'nt set null
     * @param requirePermanentlyDenied       require permission even if user have permanently denied
     * @param jumpSettingIfPermanentlyDenied jump to system settings page if permission permanently denied
     * @param rationale                      explain why we need this permission
     * @param permissions                    permission
     * @return permission object for different api require
     */
    @Nullable
    @Override
    public IPermission requirePermissions(@Nullable final Context c, boolean requirePermanentlyDenied, boolean jumpSettingIfPermanentlyDenied, @Nullable final String rationale, @NonNull final String... permissions) {
        if (permissions.length <= 0) {
            return null;
        }
        Context ctx = c;
        if (null == c) {
            ctx = context;
        }
        final PermissionBiz biz = new PermissionBiz(ctx, requirePermanentlyDenied, jumpSettingIfPermanentlyDenied, rationale, permissions);
        if (PermissionBiz.hasPermissions(ctx, permissions)) {
            // async return permission require result, make sure api have return
            asyncPostPermissionCompleteEvent(biz, true);
        } else {
            doOrQueueRequirePermissions(biz);
        }
        return biz;
    }

    private void doOrQueueRequirePermissions(@NonNull final PermissionBiz biz) {
        permissionBizArr.add(biz);
        firePermissionReqCommandEvent(null);
    }

    @Nullable
    @Override
    public IPermission requireBleEnablePermission(@Nullable final Context c, @Nullable String rationale) {
        Context ctx = c;
        if (null == c) {
            ctx = context;
        }
        final PermissionBiz biz = new PermissionBiz(ctx, BizClz.BLE, rationale);
        if (biz.bleEnabled()) {
            // async return permission require result, make sure api have return
            asyncPostPermissionCompleteEvent(biz, true);
        } else {
            doOrQueueRequirePermissions(biz);
        }
        return biz;
    }

    @Nullable
    @Override
    public IPermission requireLocationEnablePermission(@Nullable final Context c, @Nullable String rationale) {
        Context ctx = c;
        if (null == c) {
            ctx = context;
        }
        final PermissionBiz biz = new PermissionBiz(ctx, BizClz.LOCATION, rationale);
        if (biz.locationEnabled()) {
            // async return permission require result, make sure api have return
            asyncPostPermissionCompleteEvent(biz, true);
        } else {
            doOrQueueRequirePermissions(biz);
        }
        return biz;
    }

    @Override
    @MainThread
    public boolean cancelPermissionRequire(@NonNull final IPermission biz) {
        if (biz instanceof PermissionBiz) {
            final PermissionBiz permitBiz = ((PermissionBiz) biz);
            return permissionBizArr.remove(permitBiz);
        }
        return false;
    }

    @Override
    public boolean registerPermissionListener(@NonNull final IPermissionListener l) {
        return listenersManager.registerListener(l);
    }

    @Override
    public void unregisterPermissionListener(@NonNull final IPermissionListener l) {
        listenersManager.unregisterListener(l);
    }

    @MainThread
    private void postPermissionCompleteEventOnMainThread(@NonNull final IPermissionListener listener, @NonNull final IPermission permission, boolean success) {
        listener.onPermissionCompleted(permission, success);
    }

    @MainThread
    private void onPermissionCompleteEventMainThread(@NonNull final PermissionBiz biz, final boolean success) {
        LogHelper.d(TAG, "onPermissionCompleteEventMainThread");
        if (!isDestroyed()) {
            permissionBizArr.remove(biz);
            listenersManager.forEachListener(new ListenerManager.IForEachListener<IPermissionListener>() {
                @Override
                public void onForEachListener(@NonNull final IPermissionListener listener) {
                    postPermissionCompleteEventOnMainThread(listener, biz, success);
                }
            });
        } else {
            LogHelper.e(TAG, "onPermissionCompleteEventMainThread Bu Destroyed why?");
        }
    }

    @AnyThread
    private void asyncPostPermissionCompleteEvent(@NonNull final PermissionBiz biz, final boolean success) {
        LogHelper.d(TAG, "asyncPostPermissionCompleteEvent");
        this.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                onPermissionCompleteEventMainThread(biz, success);
            }
        });
    }

    /**
     * we don't check return code ,just close the require window
     * we check permission result use permission api later
     */
    @AnyThread
    public void firePermissionReqCompleteEvent(@NonNull final PermissionBiz biz) {
        asyncPostPermissionCompleteEvent(biz, biz.isExecSuccess());
    }

    @AnyThread
    protected final boolean runOnMainThread(@NonNull final Runnable r) {
        if (!isDestroyed()) {
            return mHandler.post(r);
        }
        return false;
    }

    @AnyThread
    protected final boolean postDelayed(@NonNull final Runnable r, final long delayMillis) {
        if (!isDestroyed()) {
            return mHandler.postDelayed(r, delayMillis);
        }
        return false;
    }

    @AnyThread
    protected final void removeCallbacks(@NonNull final Runnable r) {
        if (!isDestroyed()) {
            mHandler.removeCallbacks(r);
        }
    }

    @Nullable
    @Override
    public PermissionBiz fetchPermissionBiz() {
        for (PermissionBiz biz : permissionBizArr) {
            // remove timer
            if (null != permitReqRunnable) {
                removeCallbacks(permitReqRunnable);
            }
            return biz;
        }
        return null;
    }

    @MainThread
    private void firePermissionReqCommandEvent(@Nullable final PermissionBiz biz) {
        if (null != biz) {
            permitReqRunnable = null;
            permissionBizArr.remove(biz);
            firePermissionReqCompleteEvent(biz);
        }

        if (null == permitReqRunnable) {
            for (final PermissionBiz permitBiz : permissionBizArr) {
                permitBiz.fireRequirePermissionCommand();
                permitReqRunnable = new Runnable() {
                    @Override
                    public void run() {
                        LogHelper.e(TAG, "permission require timeout");
                        firePermissionReqCommandEvent(permitBiz);
                    }
                };
                postDelayed(permitReqRunnable, permitMaxInitDelay);
                break;
            }
        }
    }

    @Override
    public void onActivityFinished(@Nullable final PermissionBiz biz) {
        firePermissionReqCommandEvent(biz);
    }
}
