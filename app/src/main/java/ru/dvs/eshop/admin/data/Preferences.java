package ru.dvs.eshop.admin.data;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Класс для удобного пользования настройками приложения.
 * порядок действий:
 * * setContext(Activity);
 * * get/set...
 */
public class Preferences {
    private static SharedPreferences mSettings;

    /////////////////////////////////////// STRING
    public static void setString(String key, String value) {
        mSettings.edit().putString(key, value).apply();
    }

    public static String getString(String key) {
        return mSettings.getString(key, null);
    }

    /////////////////////////////////////// INT
    public static void setInt(String key, int value) {
        mSettings.edit().putInt(key, value).apply();
    }

    public static int getInt(String key) {
        return mSettings.getInt(key, 0);
    }

    /////////////////////////////////////// BOOL
    public static void setBool(String key, boolean value) {
        mSettings.edit().putBoolean(key, value).apply();
    }

    public static boolean getBool(String key) {
        return mSettings.getBoolean(key, false);
    }

    public static void setContext(Activity activity) {
        mSettings = activity.getSharedPreferences("appSettings", Context.MODE_PRIVATE);
    }
}
