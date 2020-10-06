package com.mobibrw.light.permission.biz;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobibrw.light.permission.api.IPermission;
import com.mobibrw.light.permission.helper.PermissionHelper;

import pub.devrel.easypermissions.EasyPermissions;

public class PermissionBiz implements IPermission {
    private final static String TAG = "PermissionBiz";
    @NonNull
    private Context context;
    private String[] permissions;
    private String rationale;
    private BizClz bizClz;
    private boolean requirePermanentlyDenied;
    private boolean jumpSettingIfPermanentlyDenied;

    /**
     * require permission
     *
     * @param c                              context for require Permission
     * @param requirePermanentlyDenied       require permission even if user have permanently denied
     * @param jumpSettingIfPermanentlyDenied jump to system settings page if permission permanently denied
     * @param rationale                      explain why we need this permission
     * @param permissions                    permission
     * @return permission object for different api require
     */
    public PermissionBiz(@NonNull final Context c, boolean requirePermanentlyDenied, boolean jumpSettingIfPermanentlyDenied, @Nullable final String rationale, @NonNull final String... permissions) {
        this.context = c;
        this.permissions = permissions;
        this.rationale = rationale;
        this.requirePermanentlyDenied = requirePermanentlyDenied;
        this.jumpSettingIfPermanentlyDenied = jumpSettingIfPermanentlyDenied;
        this.bizClz = BizClz.PERMISSION;
    }

    /**
     * @param c         context
     * @param rationale explain why we need this permission
     */
    public PermissionBiz(@NonNull final Context c, @NonNull final BizClz bizClz, @Nullable final String rationale) {
        this.context = c;
        this.permissions = new String[0];
        this.rationale = rationale;
        this.requirePermanentlyDenied = false;
        this.jumpSettingIfPermanentlyDenied = false;
        this.bizClz = bizClz;
    }

    /**
     * @param c           context
     * @param permissions permission
     */
    public static boolean hasPermissions(@NonNull final Context c, @NonNull final String... permissions) {
        return EasyPermissions.hasPermissions(c, permissions);
    }

    public boolean isExecSuccess() {
        switch (bizClz) {
            case BLE:
                return bleEnabled();
            case LOCATION:
                return locationEnabled();
            case PERMISSION:
                return grantedPermissions();
            default:
                return false;
        }
    }

    public boolean grantedPermissions() {
        return EasyPermissions.hasPermissions(getContext(), permissions);
    }

    /**
     * @return if bluetooth enabled return true, else return false
     */
    public boolean bleEnabled() {
        return PermissionHelper.bleEnabled();
    }

    /**
     * @return if location enabled return true, else return false
     */
    public boolean locationEnabled() {
        return PermissionHelper.locationEnabled(getContext());
    }

    @NonNull
    public BizClz getBizClz() {
        return bizClz;
    }

    @NonNull
    public String[] getPermissions() {
        return permissions;
    }

    @Nullable
    public String getRationale() {
        return rationale;
    }

    @NonNull
    public Context getContext() {
        return context;
    }

    public boolean getRequirePermanentlyDenied() {
        return requirePermanentlyDenied;
    }

    public boolean getJumpSettingIfPermanentlyDenied() {
        return jumpSettingIfPermanentlyDenied;
    }

    public void fireRequirePermissionCommand() {
        PermissionActivity.requirePermissions(getContext());
    }
}
