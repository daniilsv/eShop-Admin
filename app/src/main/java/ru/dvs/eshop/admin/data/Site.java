package ru.dvs.eshop.admin.data;

public class Site {
    String host;
    String token;

    public Site() {
        host = Preferences.getString("host");
        token = Preferences.getString("token");

    }
}
