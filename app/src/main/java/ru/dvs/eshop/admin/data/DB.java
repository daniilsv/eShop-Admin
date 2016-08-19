package ru.dvs.eshop.admin.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Map;

import ru.dvs.eshop.admin.Core;

/**
 * Класс для удобного пользования Базой данных.
 * Порядок действий:
 * * DB db = DB.getInstance();
 * * db.setContext(context);
 * * db.connect();
 */
public class DB {

    private static final String LOG_TAG = "DB";
    private static DB ourInstance = null;
    private SQLiteDatabase db;
    private Context context;

    private DB() {
    }

    public static DB getInstance() {
        if (ourInstance == null)
            ourInstance = new DB();
        return ourInstance;
    }

    //Добавляет элемент в таблицу
    public static long insert(String table, Map sm) {
        if (ourInstance == null)
            return 0;
        ContentValues cv = new ContentValues();
        for (Object o : sm.entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            cv.put((String) pair.getKey(), (String) pair.getValue());
        }
        return ourInstance.db.insert(table, null, cv);
    }

    //Обновляет элемент таблицы по ID
    public static long update(String table, int id, Map sm) {
        if (ourInstance == null)
            return 0;
        ContentValues cv = new ContentValues();
        for (Object o : sm.entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            cv.put((String) pair.getKey(), (String) pair.getValue());
        }
        return ourInstance.db.update(table, cv, "id=" + id, null);
    }

    //Удаляет по запросу элемент из таблицы
    public static int delete(String table, String whereClause, String[] whereArgs) {
        if (ourInstance == null)
            return 0;
        return ourInstance.db.delete(table, whereClause, whereArgs);
    }

    //Поучает Cursor по запросу из таблицы
    @Nullable
    public static Cursor query(String table, String[] columns, String selection,
                               String[] selectionArgs, String groupBy, String having,
                               String orderBy) {

        if (ourInstance == null)
            return null;
        if (orderBy == null)
            orderBy = "id DESC";
        return ourInstance.db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
    }

    //Получает кол-во элементов в таблице по запросу
    public static int getCount(String table, String where) {
        if (ourInstance == null)
            return 0;
        Cursor c = ourInstance.db.query(table, null, where, null, null, null, null);
        int ret = c.getCount();
        c.close();
        return ret;
    }

    //Удаляет элемент по ID из таблицы
    public static int removeById(String table, int id) {
        if (ourInstance == null)
            return 0;
        return ourInstance.db.delete(table, "id = " + id, null);
    }

    //Устанавливаем контекст
    public void setContext(Context _context) {
        context = _context;
    }

    //Подключаемся к БД
    public void connect() {
        DBHelper dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    private class DBHelper extends SQLiteOpenHelper {

        DBHelper(Context context) {
            super(context, "appEShopAdmin", null, Core.versionI);
        }

        //При создании базы данных
        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d(LOG_TAG, "--- onCreate database ---");
            ////////////////////
            Log.d(LOG_TAG, "--- Create com_eshop_categories");
            db.execSQL("CREATE TABLE com_eshop_categories (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "is_enabled INTEGER NOT NULL DEFAULT '1'," +
                    "title TEXT NOT NULL," +
                    "ordering INTEGER DEFAULT NULL," +
                    "parent_id INTEGER DEFAULT NULL," +
                    "description TEXT," +
                    "icon TEXT," +
                    "meta_keys TEXT," +
                    "meta_desc TEXT," +
                    "url TEXT DEFAULT NULL," +
                    "tpl TEXT DEFAULT NULL," +
                    "PRIMARY KEY (`id`));"
            );
            ///////////////////
            Log.d(LOG_TAG, "--- Create com_eshop_chars");
            db.execSQL("CREATE TABLE com_eshop_chars (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "is_enabled INTEGER NOT NULL DEFAULT '1'," +
                    "title TEXT NOT NULL," +
                    "type TEXT NOT NULL," +
                    "group TEXT NOT NULL," +
                    "units TEXT NOT NULL," +
                    "is_custom INTEGER NOT NULL DEFAULT '1'," +
                    "is_published INTEGER NOT NULL DEFAULT '1'," +
                    "values TEXT," +
                    "categories TEXT," +
                    "ordering INTEGER DEFAULT NULL," +
                    "PRIMARY KEY (`id`));"
            );
            /////////////////////
            Log.d(LOG_TAG, "--- Create com_eshop_delivery_methods");
            db.execSQL("CREATE TABLE com_eshop_delivery_methods (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "is_enabled INTEGER NOT NULL DEFAULT '1'," +
                    "ordering INTEGER DEFAULT NULL," +
                    "title TEXT NOT NULL," +
                    "description TEXT," +
                    "icon TEXT," +
                    "price FLOAT DEFAULT NULL," +
                    "PRIMARY KEY (`id`));"
            );
            ////////////////////
            Log.d(LOG_TAG, "--- Create com_eshop_items");
            db.execSQL("CREATE TABLE com_eshop_items (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "is_enabled INTEGER NOT NULL DEFAULT '1'," +
                    "ordering INTEGER DEFAULT NULL," +
                    "category_id INTEGER DEFAULT NULL," +
                    "art_no TEXT DEFAULT NULL," +
                    "title TEXT NOT NULL," +
                    "date_pub DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    "price FLOAT DEFAULT NULL,"  +
                    "price_old TEXT DEFAULT NULL," +
                    "quantity FLOAT DEFAULT NULL," +
                    "desc_short TEXT," +
                    "desc_full TEXT," +
                    "vendor_id INTEGER DEFAULT NULL," +
                    "category_add_id TEXT," +
                    "img TEXT," +
                    "images TEXT," +
                    "chars TEXT," +
                    "meta_keys TEXT," +
                    "meta_desc TEXT," +
                    "url TEXT DEFAULT NULL," +
                    "tpl TEXT DEFAULT NULL," +
                    "PRIMARY KEY (`id`));"
            );
            ////////////////////
            Log.d(LOG_TAG, "--- Create com_eshop_payment_types");
            db.execSQL("CREATE TABLE com_eshop_payment_types (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "is_enabled INTEGER NOT NULL DEFAULT '1'," +
                    "ordering INTEGER DEFAULT NULL," +
                    "name TEXT DEFAULT NULL," +
                    "title TEXT DEFAULT NULL," +
                    "description TEXT," +
                    "icon TEXT NOT NULL," +
                    "options TEXT NOT NULL," +
                    "PRIMARY KEY (`id`));"
            );
            ////////////////////
            Log.d(LOG_TAG, "--- Create com_eshop_vendors");
            db.execSQL("CREATE TABLE com_eshop_vendors (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "is_enabled INTEGER NOT NULL DEFAULT '1'," +
                    "title TEXT DEFAULT NULL," +
                    "icon TEXT," +
                    "description TEXT," +
                    "url TEXT DEFAULT NULL," +
                    "ordering INTEGER DEFAULT NULL," +
                    "PRIMARY KEY (`id`));"
            );

        }

        //При обновлении приложения
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS appEShopAdmin");
            onCreate(db);
        }
    }
}