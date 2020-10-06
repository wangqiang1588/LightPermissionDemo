package com.mobibrw.light.permission.helper;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.location.LocationManager;
import android.support.annotation.NonNull;

public class PermissionHelper {
    /**
     * @return return true if bluetooth enabled, else return false
     */
    public static boolean bleEnabled() {
        final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return (null != bluetoothAdapter) && (bluetoothAdapter.isEnabled());
    }

    /**
     * @return return true if location enabled, else return false
     */
    public static boolean locationEnabled(@NonNull final Context c) {
        final LocationManager locationManager = (LocationManager) c.getSystemService(android.content.Context.LOCATION_SERVICE);
        if (null != locationManager) {
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }
        return false;
    }
}
