package ru.dvs.eshop.admin.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class Permissions {

    //Проверяет, есть ли у приложения разрешение
    public static boolean checkPermission(Context context, String perm) {
        return (Build.VERSION.SDK_INT < 23 || ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED);
    }

    //Запрашивает у системы разрешение
    public static void requestPermission(Activity activity, String perm) {
        ActivityCompat.requestPermissions(activity, new String[]{perm}, 0);
    }
}
