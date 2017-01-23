package ru.dvs.eshop.admin.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.ViewGroup;
import ru.dvs.eshop.admin.ui.views.FormField;

import java.util.ArrayList;


public class Utils {
    private static String pUniqueId = null;

    //Получает уникальный для каждого устройства идентификатор
    public static String getUniqueID(Context context) {
        if (pUniqueId == null)
            pUniqueId = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        return pUniqueId;
    }

    //Проверяет наличие соединения с сетью интернет
    public static boolean hasConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
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

    public static String strRepeat(String str, int count) {
        return new String(new char[count]).replace("\0", str);
    }

    public static String fileName(String filePath) {
        int idx = filePath.replaceAll("\\\\", "/").lastIndexOf("/");
        return filePath.substring(idx + 1);
    }

    static String toCamelCase(String s) {
        String[] parts = s.split("_");
        String camelCaseString = "";
        for (String part : parts) {
            camelCaseString = camelCaseString + toProperCase(part);
        }
        return camelCaseString;
    }

    static String toProperCase(String s) {
        return s.substring(0, 1).toUpperCase() +
                s.substring(1).toLowerCase();
    }

    public void generateForm(ViewGroup parent, ArrayList<FormField> form) {
        for (FormField f : form) {
            parent.addView(f.getView(parent.getContext()));
        }
    }
}