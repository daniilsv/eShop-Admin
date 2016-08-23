package ru.dvs.eshop.admin;


import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import java.io.File;

import ru.dvs.eshop.admin.data.DB;
import ru.dvs.eshop.admin.data.Site;
import ru.dvs.eshop.admin.ui.activities.MainActivity;


//TODO: Упразднить класс. Раскидать функции по соответствующим пакетам

/**
 * Ядро системы.
 * Пока еще Дает доступ к некоторым функциям.
 **/
public class Core {
    public final static String version = "0.0.2";
    public final static int versionI = 2;
    private static Core ourInstance = null;
    public Context context;
    public Activity activity;
    public Site site;
    private DB db = null;

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

    //Получает(создает) кэш-папку приложения (...(SDCARD).../Android/data/ru.dvs.eshop.admin/)
    public static String getStorageDir() {
        if (ourInstance == null)
            return "";
        File file = ourInstance.context.getExternalFilesDir(null);
        new File(file, "icons/vendors").mkdirs();
        return file != null ? file.getPath() : null;
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
    public void start(Activity _activity) {
        setActivity(_activity);
        if (_activity == null)
            return;
        if (db == null) {//БД
            db = DB.getInstance();
            db.setContext(context);
            db.connect();
        }
        site = new Site();
    }

    //Устанавливает текущее активити
    public void setActivity(Activity _activity) {
        if (activity != null && activity instanceof MainActivity)
            return;
        activity = _activity;
        context = _activity;
    }

}