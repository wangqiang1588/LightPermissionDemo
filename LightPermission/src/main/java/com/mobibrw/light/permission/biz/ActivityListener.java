package com.mobibrw.light.permission.biz;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class ActivityListener implements Application.ActivityLifecycleCallbacks {
    @NonNull
    private final Application application;
    @NonNull
    private final IActivityListener listener;

    @MainThread
    public ActivityListener(final @NonNull Context c, final @NonNull IActivityListener l) {
        application = ((Application) c.getApplicationContext());
        application.registerActivityLifecycleCallbacks(this);
        this.listener = l;
    }

    @MainThread
    public void onBundleTerminate() {
        application.unregisterActivityLifecycleCallbacks(this);
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        if (activity instanceof PermissionActivity) {
            final PermissionActivity reqActivity = (PermissionActivity) activity;
            listener.onActivityFinished(reqActivity.getPermissionBiz());
        }
    }
}
