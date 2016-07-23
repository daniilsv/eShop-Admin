package ru.dvs.eshop.admin.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import ru.dvs.eshop.admin.Core;


public class Utils {
    private static String pUniqueId = null;

    //Получает уникальный для каждого устройства идентификатор
    public static String getUniqueID() {
        if (pUniqueId == null) {
            Context context = Core.getInstance().context;
            pUniqueId = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        }
        return pUniqueId;
    }

    //Проверяет наличие соединения с сетью интернет
    public static boolean hasConnection() {
        ConnectivityManager cm = (ConnectivityManager) Core.getInstance().context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                return true;
            }
        }
        return false;
    }
}