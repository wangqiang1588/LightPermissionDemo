package com.mobibrw.light.permission.helper;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;

public class ActivityHelper {
    @MainThread
    public static boolean activityAlive(@Nullable final Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return (null != activity) && (!activity.isFinishing()) && (!activity.isDestroyed());
        } else {
            return (null != activity) && (!activity.isFinishing());
        }
    }
}
