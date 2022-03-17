package com.journeyOS.setting.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.journeyOS.liteframework.utils.KLog;

public class AppUtils {

    public static boolean isIntentAvailable(Context context, Intent intent) {
        if (intent == null) {
            return false;
        }
        return context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0;
    }

    public static boolean startIntentInternal(Context context, Intent intent) {
        if (context == null || intent == null) {
            KLog.e("can't start null object!");
            return false;
        }

        try {
//            if (isIntentAvailable(context, intent)) {
                context.startActivity(intent);
//            }
            return true;
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean openInMarket(@NonNull Context context, String packageName, String title) {
        boolean success = false;
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        if (packageName != null) {
            intent.setPackage(packageName);
        }

        try {
            AppUtils.startIntentInternal(context, Intent.createChooser(intent, title)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            success = true;
        } catch (ActivityNotFoundException e) {
            success = false;
        }

        return success;
    }

    public static void openEmail(@NonNull Context context, @NonNull String email, String title) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});

        try {
            AppUtils.startIntentInternal(context, Intent.createChooser(intent, title)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void openBrowser(@NonNull Context context, @NonNull String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            AppUtils.startIntentInternal(context, intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }
}
