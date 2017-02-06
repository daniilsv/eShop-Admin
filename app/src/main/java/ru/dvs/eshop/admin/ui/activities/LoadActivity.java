package ru.dvs.eshop.admin.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import ru.dvs.eshop.admin.data.DataBase;
import ru.dvs.eshop.admin.data.sevices.RequestService;

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
        new DataBase(getApplicationContext()).close();
        startService(new Intent(this, RequestService.class));
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
