package com.mobibrw.light.permission.helper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

public class ToastHelper {

    public static void showLong(@NonNull final Context c, @NonNull final String txt) {
        final Toast toast = Toast.makeText(c, txt, Toast.LENGTH_LONG);
        toast.setText(txt);
        toast.show();
    }
}
