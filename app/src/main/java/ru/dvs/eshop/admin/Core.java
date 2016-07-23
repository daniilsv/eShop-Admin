package ru.dvs.eshop.admin;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.io.File;

import ru.dvs.eshop.R;
import ru.dvs.eshop.admin.data.network.FILEQuery;
import ru.dvs.eshop.admin.ui.activities.MainActivity;


/**
 * Ядро системы.
 * Объединяет все компоненты.
 * Дает доступ к функциям.
 **/
public class Core {
    public final static String version = "1.0";
    private static Core ourInstance = null;
    public Context context;
    public Activity activity;
    private DB db = null;
    private SharedPreferences prefs = null;

    private Core() {

    }

    public static Core getInstance() {
        if (ourInstance == null)
            ourInstance = new Core();
        return ourInstance;
    }

    //Простое получение строки из ресурсов приложения
    public static String getString(int res_id) {
        return Core.getInstance().context.getResources().getString(res_id);
    }

    //Получает(создает) кэш-папку приложения (...(SDCARD).../Android/data/ru.daniils.dvscommerce/)
    public static String getStorageDir() {
        if (ourInstance == null)
            return "";
        File file = ourInstance.context.getExternalFilesDir(null);
        new File(file, "favicons").mkdirs();
        return file != null ? file.getPath() : null;
    }

    //Позволяет загрузить файл с сайта по имени хоста и расположения на нем и сохранить в некоторую папку на устройстве
    public static File loadFile(String host, String source, String dest) {
        FILEQuery fq = new FILEQuery(host + source, getStorageDir() + dest);
        fq.get();
        return fq.getResult();
    }

    //Показывает Toast уведомление из строки
    public static void makeToast(final String message, final boolean is_long) {
        Toast.makeText(Core.getInstance().context, message, is_long ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
    }

    //Показывает Toast уведомление из ресурса приложения
    public static void makeToast(final int res_id, final boolean is_long) {
        Toast.makeText(Core.getInstance().context, Core.getString(res_id), is_long ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
    }

    //Запускает соединение и БД приложения
    public void start(Context _context) {
        if (_context == null)
            return;
        context = _context;
        if (db == null) {//БД
            db = DB.getInstance();
            db.setContext(context);
            db.connect();
        }
        if (prefs == null)//Настройки
            prefs = PreferenceManager.getDefaultSharedPreferences(context);

    }

    //Устанавливает текущее активити
    public void setActivity(Activity _activity) {
        if (activity != null && activity instanceof MainActivity)
            return;
        activity = _activity;
        context = _activity;
    }

    //Класс проверяет строку (ответ от сервера) на ошибки и показывает уведомление при их наличии.
    //Завершает работу текущего соединения.
    private static class CheckConnectionFails extends Thread {
        private String result;
        private boolean ended = false;

        CheckConnectionFails(String _result) {
            result = _result;
        }

        public void run() {
            if (result == null || result.equals("NULL")) {
                makeToast(R.string.no_internet, true);
                result = null;
            } else if (result.contains("RC: ")) {
                makeToast(String.format("%s: %s", Core.getString(R.string.enter_error), result.substring(4)), false);
                result = null;
            } else if (result.contains("<html")) {
                makeToast(String.format("%s Wrong request", Core.getString(R.string.enter_error)), false);
                result = null;
            } else if (result.equals("false")) {
                makeToast(R.string.no_more_content, false);
                result = null;
            }
            ended = true;
        }

        String getResult() {
            //"Правильный" join и возврат результата
            try {
                while (!ended)
                    Thread.sleep(300);
                join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return result;
        }

    }
}