package ru.dvs.eshop.admin.data;

public class Site {
    public String host;
    public String token;

    public Site() {
        host = Preferences.getString("host");
        token = Preferences.getString("token");

    }
}
