package com.mobibrw.light.permission.biz;

import android.support.annotation.MainThread;
import android.support.annotation.Nullable;

public class PermissionBizApi {
    @MainThread
    @Nullable
    static public IPermissionBizApi bizApi() {
        return PermissionBizBu.bizApi();
    }
}
