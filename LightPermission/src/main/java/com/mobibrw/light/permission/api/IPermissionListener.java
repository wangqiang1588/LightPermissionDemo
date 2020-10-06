package com.mobibrw.light.permission.api;

import android.support.annotation.MainThread;
import android.support.annotation.NonNull;

public interface IPermissionListener {
    @MainThread
    void onPermissionCompleted(@NonNull final IPermission permission, boolean success);
}
