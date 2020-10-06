package com.mobibrw.light.permission.biz;

import android.content.Context;
import android.support.annotation.NonNull;

import com.mobibrw.light.permission.api.IPermissionApi;

public class LightPermission {

    @NonNull
    static public IPermissionApi api(final @NonNull Context c) {
        return PermissionBizBu.api(c);
    }

    static public void destroy(@NonNull final IPermissionApi api) {
        if (api instanceof PermissionBizBu) {
            PermissionBizBu bizBu = (PermissionBizBu) api;
            bizBu.terminateBundle();
        }
    }
}
