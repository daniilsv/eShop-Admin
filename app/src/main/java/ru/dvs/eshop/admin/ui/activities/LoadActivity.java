package ru.dvs.eshop.admin.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import ru.dvs.eshop.admin.data.Preferences;

/**
 * Стартовая активность
 * Запускается при включении приложения.
 * При отсутствии токена перенаправляет на логин.
 * Иначе запускает MainActivity.
 */
public class LoadActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Preferences.setContext(this);
        if (Preferences.getInt("login_status") == 2) {
            startActivity(new Intent(this, MainActivity.class));
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
        finish();
    }
}
