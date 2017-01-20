package ru.dvs.eshop.admin.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class DataBase {

    private static final String LOG_TAG = "DataBase";
    private SQLiteDatabase db;

    public DataBase(Context context) {
        DBHelper dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    //Добавляет элемент в таблицу
    public long insert(String table, Map sm) {
        ContentValues cv = new ContentValues();
        for (Object o : sm.entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            cv.put((String) pair.getKey(), String.valueOf(pair.getValue()));
        }
        return db.insert(table, null, cv);
    }

    //Обновляет элемент таблицы по ID
    public long update(String table, int id, Map sm) {
        ContentValues cv = new ContentValues();
        for (Object o : sm.entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            cv.put("" + pair.getKey(), "" + pair.getValue());
        }
        return db.update(table, cv, "id=" + id, null);
    }

    //Добавляет или обновляет элемент в таблицу
    public void insertOrUpdate(String table, String where, HashMap<String, String> map) {
        Cursor bd_item = query(table, null, where, null, null, null, null);
        if (bd_item != null && bd_item.moveToFirst()) {//Элемент в таблице есть - обновляем данные
            update(table, bd_item.getInt(bd_item.getColumnIndex("id")), map);
            bd_item.close();
        } else {//Элемента в таблиц нет - добавляем его
            insert(table, map);
        }
    }

    //Удаляет по запросу элемент из таблицы
    public int delete(String table, String whereClause, String[] whereArgs) {
        return db.delete(table, whereClause, whereArgs);
    }

    //Поучает Cursor по запросу из таблицы
    @Nullable
    public Cursor query(String table, String[] columns, String selection,
                        String[] selectionArgs, String groupBy, String having,
                        String orderBy) {
        if (orderBy == null)
            orderBy = "id DESC";
        return db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
    }

    //Получает кол-во элементов в таблице по запросу
    public int getCount(String table, String where) {
        Cursor c = db.query(table, null, where, null, null, null, null);
        int ret = c.getCount();
        c.close();
        return ret;
    }

    //Удаляет элемент по ID из таблицы
    public int removeById(String table, int id) {
        return db.delete(table, "id = " + id, null);
    }

    public void close() {
        try {
            db.endTransaction();
        } catch (IllegalStateException ignored) {
            db.close();
        }
        db = null;
    }

    private class DBHelper extends SQLiteOpenHelper {

        DBHelper(Context context) {
            super(context, "eshop_db", null, 1);
        }

        //При создании базы данных
        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d(LOG_TAG, "--- onCreate database ---");

            Log.d(LOG_TAG, "--- Create com_eshop_vendors");
            db.execSQL("CREATE TABLE com_eshop_vendors (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "original_id INTEGER NOT NULL," +
                    "parent_id INTEGER NOT NULL," +
                    "level INTEGER NOT NULL," +
                    "is_enabled INTEGER NOT NULL DEFAULT '1'," +
                    "title TEXT DEFAULT NULL," +
                    "icon TEXT," +
                    "description TEXT," +
                    "url TEXT DEFAULT NULL," +
                    "ordering INTEGER DEFAULT NULL);" +
                    "CREATE INDEX IF NOT EXISTS uniq ON com_eshop_vendors (id, original_id);"
            );
            ////////////////////
            Log.d(LOG_TAG, "--- Create com_eshop_categories");
            db.execSQL("CREATE TABLE com_eshop_categories (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "original_id INTEGER NOT NULL," +
                    "is_enabled INTEGER NOT NULL DEFAULT '1'," +
                    "title TEXT NOT NULL," +
                    "ordering INTEGER DEFAULT NULL," +
                    "level INTEGER NOT NULL," +
                    "parent_id INTEGER DEFAULT NULL," +
                    "description TEXT," +
                    "icon TEXT," +
                    "meta_keys TEXT," +
                    "meta_desc TEXT," +
                    "url TEXT DEFAULT NULL," +
                    "tpl TEXT DEFAULT NULL);" +
                    "CREATE INDEX IF NOT EXISTS uniq ON com_eshop_categories (id, original_id);"
            );
            /*
            ///////////////////
            Log.d(LOG_TAG, "--- Create com_eshop_chars");
            db.execSQL("CREATE TABLE com_eshop_chars (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "original_id INTEGER NOT NULL," +
                    "is_enabled INTEGER NOT NULL DEFAULT '1'," +
                    "title TEXT NOT NULL," +
                    "type TEXT NOT NULL," +
                    "`group` TEXT NOT NULL," +
                    "units TEXT NOT NULL," +
                    "is_custom INTEGER NOT NULL DEFAULT '1'," +
                    "is_published INTEGER NOT NULL DEFAULT '1'," +
                    "`values` TEXT," +
                    "categories TEXT," +
                    "ordering INTEGER DEFAULT NULL);" +
                    "CREATE INDEX IF NOT EXISTS uniq ON com_eshop_chars (id, original_id);"
            );
            /////////////////////
            Log.d(LOG_TAG, "--- Create com_eshop_delivery_methods");
            db.execSQL("CREATE TABLE com_eshop_delivery_methods (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "original_id INTEGER NOT NULL," +
                    "is_enabled INTEGER NOT NULL DEFAULT '1'," +
                    "ordering INTEGER DEFAULT NULL," +
                    "title TEXT NOT NULL," +
                    "description TEXT," +
                    "icon TEXT," +
                    "price FLOAT DEFAULT NULL);" +
                    "CREATE INDEX IF NOT EXISTS uniq ON com_eshop_delivery_methods (id, original_id);"
            );
            ////////////////////
            Log.d(LOG_TAG, "--- Create com_eshop_items");
            db.execSQL("CREATE TABLE com_eshop_items (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "original_id INTEGER NOT NULL," +
                    "is_enabled INTEGER NOT NULL DEFAULT '1'," +
                    "ordering INTEGER DEFAULT NULL," +
                    "category_id INTEGER DEFAULT NULL," +
                    "art_no TEXT DEFAULT NULL," +
                    "title TEXT NOT NULL," +
                    "date_pub DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    "price FLOAT DEFAULT NULL," +
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
                    "tpl TEXT DEFAULT NULL);" +
                    "CREATE INDEX IF NOT EXISTS uniq ON com_eshop_items (id, original_id);"
            );
            ////////////////////
            Log.d(LOG_TAG, "--- Create com_eshop_payment_types");
            db.execSQL("CREATE TABLE com_eshop_payment_types (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "original_id INTEGER NOT NULL," +
                    "is_enabled INTEGER NOT NULL DEFAULT '1'," +
                    "ordering INTEGER DEFAULT NULL," +
                    "name TEXT DEFAULT NULL," +
                    "title TEXT DEFAULT NULL," +
                    "description TEXT," +
                    "icon TEXT NOT NULL," +
                    "options TEXT NOT NULL);" +
                    "CREATE INDEX IF NOT EXISTS uniq ON com_eshop_payment_types (id, original_id);"
            );
*/
        }

        //При обновлении приложения
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS eshop_db");
            onCreate(db);
        }
    }
}