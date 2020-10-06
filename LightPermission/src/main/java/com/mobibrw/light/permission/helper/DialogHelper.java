package com.mobibrw.light.permission.helper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.mobibrw.light.permission.R;

public class DialogHelper {

    public static void alertDialog(@NonNull final Context c, @NonNull final String title, @Nullable final String message, @NonNull final DialogInterface.OnClickListener clickListener) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(title);
        if (!TextUtils.isEmpty(message)) {
            builder.setMessage(message);
        }
        builder.setPositiveButton(R.string.ok, clickListener);
        final AlertDialog alert = builder.create();
        alert.show();
    }
}
