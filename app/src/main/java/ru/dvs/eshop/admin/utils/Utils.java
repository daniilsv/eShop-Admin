package ru.dvs.eshop.admin;

/**
 * Created by MSI1 on 10.06.2016.
 */
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class Utils {

    //Получает уникальный для каждого устройства идентификатор
    static String getUniqueID(Context context) {
        return android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
    }

    //Получает MD5 хеш строки
    public static String MD5(String s) {
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(s.getBytes(), 0, s.length());
            return new BigInteger(1, m.digest()).toString(16);
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return "NULL";
    }

    //Проверяет, является ли строка валидным MD5 хешем
    public static boolean isValidMD5(String s) {
        return s.matches("[a-fA-F0-9]{32}");
    }

    //Проверяет наличие соединения с сетью интернет
    static boolean hasConnection() {
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

    //Переводит long время в человеко-понятную дату День-Месяц-Год
    public static String timeToDate(Long time) {
        if (time == null)
            time = System.currentTimeMillis();
        return new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date(time));
    }

    //Переводит long время в Час от начала дня
    public static int timeToHour(Long time) {
        if (time == null)
            time = System.currentTimeMillis();
        return Integer.parseInt(new SimpleDateFormat("HH", Locale.getDefault()).format(new Date(time)));
    }

    //Проверяет, есть ли у приложения разрешение
    public static boolean checkPermission(String perm) {
        return (Build.VERSION.SDK_INT < 23 || ContextCompat.checkSelfPermission(Core.getInstance().context, perm) == PackageManager.PERMISSION_GRANTED);
    }

    //Запрашивает у системы разрешение
    public static void requestPermission(String perm) {
        ActivityCompat.requestPermissions(Core.getInstance().activity, new String[]{perm}, 0);
    }
}