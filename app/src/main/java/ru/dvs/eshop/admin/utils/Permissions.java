package ru.dvs.eshop.admin.utils;

import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import ru.dvs.eshop.admin.Core;

public class Permissions {

    //Проверяет, есть ли у приложения разрешение
    public static boolean checkPermission(String perm) {
        return (Build.VERSION.SDK_INT < 23 || ContextCompat.checkSelfPermission(Core.getInstance().context, perm) == PackageManager.PERMISSION_GRANTED);
    }

    //Запрашивает у системы разрешение
    public static void requestPermission(String perm) {
        ActivityCompat.requestPermissions(Core.getInstance().activity, new String[]{perm}, 0);
    }
}
