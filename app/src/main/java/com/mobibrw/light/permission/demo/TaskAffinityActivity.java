package com.mobibrw.light.permission.demo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.mobibrw.light.permission.api.IPermission;
import com.mobibrw.light.permission.api.IPermissionApi;
import com.mobibrw.light.permission.api.IPermissionListener;
import com.mobibrw.light.permission.biz.LightPermission;
import com.mobibrw.light.permission.helper.ToastHelper;

public class TaskAffinityActivity extends AppCompatActivity implements IPermissionListener {
    public static final String[] PERMISSIONS_CONTACTS = new String[]{android.Manifest.permission.READ_CONTACTS};
    private IPermissionApi api;
    private IPermission contactsPermission;
    private IPermission blePermission;
    private IPermission locPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_affinity);
        final Button btnReadContacts = findViewById(R.id.btnReadContacts);
        api = LightPermission.api(TaskAffinityActivity.this);
        api.registerPermissionListener(this);
        btnReadContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contactsPermission = api.requirePermissions(TaskAffinityActivity.this, false, false, getString(R.string.permissions_read_contacts), PERMISSIONS_CONTACTS);
            }
        });
        final Button btnBle = findViewById(R.id.btnBle);
        btnBle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blePermission = api.requireBleEnablePermission(TaskAffinityActivity.this, getString(R.string.permissions_ble));
            }
        });

        final Button btnLoc = findViewById(R.id.btnLoc);
        btnLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locPermission = api.requireLocationEnablePermission(TaskAffinityActivity.this, getString(R.string.permissions_location));
            }
        });

    }

    @Override
    public void onPermissionCompleted(@NonNull IPermission permission, boolean success) {
        ToastHelper.showLong(TaskAffinityActivity.this, success ? getString(R.string.success) : getString(R.string.fail));
    }
}