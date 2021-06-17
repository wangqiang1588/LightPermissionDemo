package com.mobibrw.light.permission.biz;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Window;

import com.mobibrw.light.permission.R;
import com.mobibrw.light.permission.helper.ActivityHelper;
import com.mobibrw.light.permission.helper.DialogHelper;
import com.mobibrw.light.permission.helper.LogHelper;
import com.mobibrw.light.permission.helper.PermissionHelper;
import com.mobibrw.light.permission.helper.ToastHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

public class PermissionActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private final static String TAG = "PermissionActivity";
    // require code，must less than 256
    private final static int PERMISSION_REQ_CODE = 255;
    // for some android os like color os ,if activity mark's 'taskaffinity=""' ,we will not get startActivityForResult's notify
    private final boolean startActivityForResultEnable = false;
    private PermissionBiz permissionBiz;

    public static void requirePermissions(@NonNull final Context c) {
        final Intent intent = new Intent(c, PermissionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ActivityCompat.startActivity(c, intent, new Bundle());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hide title bar
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_permission);
        final ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.hide();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        final IPermissionBizApi bizApi = PermissionBizApi.bizApi();
        if (null != bizApi) {
            permissionBiz = bizApi.fetchPermissionBiz();
            if (null != permissionBiz) {
                doPermissionRequireCommand(permissionBiz);
            } else {
                doPermissionsReqCompleted();
            }
        } else {
            doPermissionsReqCompleted();
        }
    }

    @Nullable
    public PermissionBiz getPermissionBiz() {
        return permissionBiz;
    }

    @MainThread
    private void doRequirePermissionCommand(@NonNull final String[] perms, @Nullable final String rationale) {
        LogHelper.d(TAG, "doPermissionCommand");
        final PermissionRequest.Builder builder = new PermissionRequest.Builder(PermissionActivity.this, PERMISSION_REQ_CODE, perms);
        builder.setRationale(rationale);
        final PermissionRequest permReq = builder.build();
        EasyPermissions.requestPermissions(permReq);
    }

    @MainThread
    private void doRequireBleEnableCommand(@NonNull final Activity activity, @Nullable final String rationale) {
        DialogHelper.alertDialog(activity, getString(R.string.prompt), rationale, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!PermissionHelper.bleEnabled()) {
                    final Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                    /*
                     * 1. XiaoMi 10 MIUI 12.0.11 if use ActivityCompat.startActivity(activity, intent, null);  when we set breakpoint it works fine,but if we unset breakpoint ,it does not work
                     * so we must use ActivityCompat.startActivity(activity, intent, new Bundle());
                     *
                     * 2. OnePlus 3 H2OS 9.0.3  ActivityCompat.startActivityForResult(activity, intent, PERMISSION_REQ_CODE, new Bundle()); does not back to caller activity,so we can't get activity result.
                     * */
                    if (startActivityForResultEnable && (activity == PermissionActivity.this)) {
                        ActivityCompat.startActivityForResult(activity, intent, PERMISSION_REQ_CODE, new Bundle());
                    } else {
                        ActivityCompat.startActivity(activity, intent, new Bundle());
                        finish();
                    }
                }
            }
        });
        if ((!startActivityForResultEnable) && (activity != PermissionActivity.this)) {
            finish();
        }
    }

    @MainThread
    private void doRequireLocationEnableCommand(@NonNull final Activity activity, @Nullable final String rationale) {
        DialogHelper.alertDialog(activity, getString(R.string.prompt), rationale, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!PermissionHelper.locationEnabled(activity)) {
                    final Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    /*
                     * 1. XiaoMi 10 MIUI 12.0.11 if use ActivityCompat.startActivity(activity, intent, null);  when we set breakpoint it works fine,but if we unset breakpoint ,it does not work
                     * so we must use ActivityCompat.startActivity(activity, intent, new Bundle());
                     *
                     * 2. OnePlus 3 H2OS 9.0.3  ActivityCompat.startActivityForResult(activity, intent, PERMISSION_REQ_CODE, new Bundle()); does not back to caller activity,so we can't get activity result.
                     * */
                    if (startActivityForResultEnable && (activity == PermissionActivity.this)) {
                        ActivityCompat.startActivityForResult(activity, intent, PERMISSION_REQ_CODE, new Bundle());
                    } else {
                        ActivityCompat.startActivity(activity, intent, new Bundle());
                        finish();
                    }
                }
            }
        });
        if ((!startActivityForResultEnable) && (activity != PermissionActivity.this)) {
            finish();
        }
    }

    @NonNull
    public Activity getActivity(@NonNull final PermissionBiz biz) {
        final Context c = biz.getContext();
        if (c instanceof Activity) {
            return (Activity) c;
        }
        return PermissionActivity.this;
    }

    @MainThread
    private void doPermissionRequireCommand(@NonNull final PermissionBiz biz) {
        LogHelper.d(TAG, "doPermissionRequireCommand");
        switch (biz.getBizClz()) {
            case PERMISSION:
                doRequirePermissionCommand(biz.getPermissions(), biz.getRationale());
                break;
            case BLE:
                doRequireBleEnableCommand(getActivity(biz), biz.getRationale());
                break;
            case LOCATION:
                doRequireLocationEnableCommand(getActivity(biz), biz.getRationale());
                break;
            default:
                doPermissionsReqCompleted();
                break;
        }
    }

    // 如果勾选不再询问 然而却需要用户授权，则跳转到应用的权限设置页面
    @SuppressLint("ObsoleteSdkInt")
    private void openApplicationPermissionSettings(@NonNull final Activity activity) {
        final Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            final Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
        } else {
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            intent.putExtra("com.android.settings.ApplicationPkgName", this.getPackageName());
        }
        // try if system page not exists
        try {
            /*
             * 1. XiaoMi 10 MIUI 12.0.11 if use ActivityCompat.startActivity(activity, intent, null);  when we set breakpoint it works fine,but if we unset breakpoint ,it does not work
             * so we must use ActivityCompat.startActivity(activity, intent, new Bundle());
             *
             * 2. OnePlus 3 H2OS 9.0.3  ActivityCompat.startActivityForResult(activity, intent, PERMISSION_REQ_CODE, new Bundle()); does not back to caller activity,so we can't get activity result.
             * */
            if (startActivityForResultEnable) {
                ActivityCompat.startActivityForResult(activity, intent, PERMISSION_REQ_CODE, new Bundle());
            } else {
                ActivityCompat.startActivity(activity, intent, new Bundle());
                finish();
            }
        } catch (Throwable t) {
            LogHelper.e(TAG, t.getMessage(), t);
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (permissionBiz.getRequirePermanentlyDenied()) {
            if (EasyPermissions.hasPermissions(this, permissions)) {
                EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
            } else {
                final List<String> permitsArr = new ArrayList<>();
                Collections.addAll(permitsArr, permissions);
                if (permissionBiz.getJumpSettingIfPermanentlyDenied() && EasyPermissions.somePermissionPermanentlyDenied(this, permitsArr)) {
                    ToastHelper.showLong(this, permissionBiz.getRationale());
                    openApplicationPermissionSettings(getActivity(permissionBiz));
                } else {
                    // Forward results to EasyPermissions
                    EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
                }
            }
        } else {
            // Forward results to EasyPermissions
            EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        doPermissionsReqCompleted();
    }

    /**
     * inject back key
     *
     * @param keyCode key code
     * @param event   key event
     * @return if inject return true,else return false
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * we don't check return code ,just close the require window
     * we check permission result use permission api later
     */
    @MainThread
    private void doPermissionsReqCompleted() {
        if (ActivityHelper.activityAlive(this)) {
            finish();
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        doPermissionsReqCompleted();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        doPermissionsReqCompleted();
    }
}