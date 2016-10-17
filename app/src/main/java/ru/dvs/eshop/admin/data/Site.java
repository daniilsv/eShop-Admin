package ru.dvs.eshop.admin.data;

public class Site {
    public String host;
    public String token;

    public Site() {
        //TODO: Создать таблицу с настройками приложения. В ней будем хранить токены и прочую инфу про сайт и приложение.
        //Т.е. максимально аккуратно уходим от кранения Важных данных в Preferences
        host = "shop.daniils.ru";//Preferences.getString("host");
        token = "f3447eebfe018f62a4098aea2f26f44b";//Preferences.getString("token");

    }
}
