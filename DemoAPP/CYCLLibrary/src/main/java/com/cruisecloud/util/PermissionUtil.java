/*
 * Copyright 2017 CruiseCloud. All Rights Reserved.
 */
package com.cruisecloud.util;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.cruisecloud.custom.CustomAlertDialog;
import com.cruisecloud.library.R;

public class PermissionUtil {

    private static String TAG = "PermissionUtil";

    // privacy permission
    public static boolean requestPermission(final Activity activity, final String permission, final int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = ContextCompat.checkSelfPermission(activity, permission);
            CCLog.i(TAG, "checkSelfPermission result:" + result);

            if (result == PackageManager.PERMISSION_DENIED) {
                boolean shouldShow = ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
                CCLog.i(TAG, "shouldShowRequestPermissionRationale shouldShow:" + shouldShow);
                if (shouldShow) {
                    CustomAlertDialog dialog = new CustomAlertDialog(activity, "Attention", "Need this permission",
                            activity.getResources().getString(R.string.cancel), activity.getResources().getString(R.string.ok));
                    dialog.setOnDialogButtonClickListener(new CustomAlertDialog.OnDialogButtonClickLister() {
                        @Override
                        public void leftClick(DialogInterface dialog) {
                        }

                        @Override
                        public void rightClick(DialogInterface dialog) {
                            ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
                        }
                    });
                    dialog.show();
                } else {
                    ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
                }

                return false;
            }
        }

        return true;
    }

    // system permission
    private boolean requestSystemPermission(final Activity activity, final String permission, final int requestCode){
        // 判断是否有SYSTEM_ALERT_WINDOW权限
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(activity)) {
            // 申请SYSTEM_ALERT_WINDOW权限
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + activity.getPackageName()));
            activity.startActivityForResult(intent, requestCode);

            CCLog.i(TAG, "user do not have ACTION_MANAGE_OVERLAY_PERMISSION!");
            return false;
        }

        return true;
    }

    private static void nextToSetting(Activity activity) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", activity.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings","com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", activity.getPackageName());
        }
        activity.startActivity(localIntent);
    }

    public static void onRequestPermissionsResultCallback (final Activity activity, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        CCLog.i(TAG, "onRequestPermissionsResult requestCode:" + requestCode);
        for (int i = 0; i < permissions.length; i++) {
            CCLog.i(TAG, "onRequestPermissionsResult permission:" + permissions[i] + ", grantResult:" + grantResults[i]);

            if(grantResults[i] == PackageManager.PERMISSION_DENIED && !ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[i])){
                CustomAlertDialog dialog = new CustomAlertDialog(activity, "Request permission", "Need some permissions, please go to setting and open manually",
                        activity.getResources().getString(R.string.ok));
                dialog.setOnDialogSingleButtonClickListener(new CustomAlertDialog.OnDialogSingleButtonClickLister() {
                    @Override
                    public void single_Click(DialogInterface dialog) {
                        nextToSetting(activity);
                    }
                });
                dialog.show();
            }
        }
    }

}
