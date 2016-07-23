package ru.dvs.eshop.admin.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeData {

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
}
