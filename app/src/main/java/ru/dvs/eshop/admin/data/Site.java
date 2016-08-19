package ru.dvs.eshop.admin.data;

public class Site {
    public String host;
    public String token;

    public Site() {
        //TODO: Создать таблицу с настройками приложения. В ней будем хранить токены и прочую инфу про сайт и приложение.
        //Т.е. максимально аккуратно уходим от кранения Важных данных в Preferences
        host = Preferences.getString("host");
        token = Preferences.getString("token");

    }
}
