package com.mobibrw.light.permission.helper;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public class LogHelper {

    private static boolean enableLog = true;

    public static void disableLog() {
        enableLog = false;
    }

    public static void d(@NonNull final String tag, @Nullable String msg) {
        if (enableLog) {
            if (null == msg) {
                msg = "";
            }
            Log.d(tag, msg);
        }
    }

    public static void e(@NonNull final String tag, @Nullable String msg) {
        if (enableLog) {
            if (null == msg) {
                msg = "";
            }
            Log.e(tag, msg);
        }
    }

    public static void e(@NonNull final String tag, @Nullable String msg, @NonNull final Throwable tr) {
        if (enableLog) {
            if (null == msg) {
                msg = "";
            }
            Log.e(tag, msg, tr);
        }
    }

}
