package com.mobibrw.light.permission.api;

import android.content.Context;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface IPermissionApi {

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
    @MainThread
    IPermission requirePermissions(@Nullable final Context c, boolean requirePermanentlyDenied, boolean jumpSettingIfPermanentlyDenied, @Nullable String rationale, @NonNull final String... permissions);


    /**
     * require bluetooth permission
     *
     * @param c         context for require Permission,if is null ,we will use application context, but if you want to show permission dialog on spec activity ,do'nt set null
     * @param rationale explain why we need this permission
     * @return permission object for different api require
     */
    @Nullable
    @MainThread
    IPermission requireBleEnablePermission(@Nullable final Context c, @Nullable String rationale);

    /**
     * require location permission
     *
     * @param c         context for require Permission,if is null ,we will use application context, but if you want to show permission dialog on spec activity ,do'nt set null
     * @param rationale explain why we need this permission
     * @return permission object for different api require
     */
    @Nullable
    @MainThread
    IPermission requireLocationEnablePermission(@Nullable final Context c, @Nullable String rationale);

    /**
     * cancel permission require
     *
     * @param biz cancel permission require if require is queued
     * @return success return true,else return false
     */
    @MainThread
    boolean cancelPermissionRequire(@NonNull final IPermission biz);


    @MainThread
    boolean registerPermissionListener(@NonNull final IPermissionListener l);

    @MainThread
    void unregisterPermissionListener(@NonNull final IPermissionListener l);
}
