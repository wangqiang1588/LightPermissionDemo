package com.mobibrw.light.permission.demo;

import android.content.Intent;
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

public class MainActivity extends AppCompatActivity implements IPermissionListener {
    public static final String[] PERMISSIONS_CONTACTS = new String[]{android.Manifest.permission.READ_CONTACTS};
    private IPermissionApi api;
    private IPermission contactsPermission;
    private IPermission blePermission;
    private IPermission locPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button btnReadContacts = findViewById(R.id.btnReadContacts);
        api = LightPermission.api(MainActivity.this);
        api.registerPermissionListener(this);
        btnReadContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contactsPermission = api.requirePermissions(null, false, false, getString(R.string.permissions_read_contacts), PERMISSIONS_CONTACTS);
            }
        });
        final Button btnBle = findViewById(R.id.btnBle);
        btnBle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blePermission = api.requireBleEnablePermission(null, getString(R.string.permissions_ble));
            }
        });

        final Button btnLoc = findViewById(R.id.btnLoc);
        btnLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locPermission = api.requireLocationEnablePermission(null, getString(R.string.permissions_location));
            }
        });
        final Button btnTaskAffinity = findViewById(R.id.btnTaskAffinity);
        btnTaskAffinity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent();
                intent.setClass(MainActivity.this, TaskAffinityActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        api.unregisterPermissionListener(this);
        LightPermission.destroy(api);
        api = null;
        contactsPermission = null;
        blePermission = null;
        locPermission = null;
        super.onDestroy();
    }

    @Override
    public void onPermissionCompleted(@NonNull final IPermission permission, final boolean success) {
        ToastHelper.showLong(MainActivity.this, success ? getString(R.string.success) : getString(R.string.fail));
    }
}