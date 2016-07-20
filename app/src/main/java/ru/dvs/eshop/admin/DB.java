package ru.dvs.eshop.admin;

/**
 * Created by MSI1 on 10.06.2016.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.Map;

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
    void setContext(Context _context) {
        context = _context;
    }

    //Подключаемся к БД
    void connect() {
        DBHelper dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    private class DBHelper extends SQLiteOpenHelper {

        DBHelper(Context context) {
            super(context, "appLP", null, 1);
        }

        //При создании базы данных
        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d(LOG_TAG, "--- onCreate database ---");



            Log.d(LOG_TAG, "--- Create landings");
            db.execSQL("CREATE TABLE landings (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "site_id INTEGER NOT NULL," +
                    "last_widget_id INTEGER NOT NULL," +
                    "last_lead_id INTEGER NOT NULL," +
                    "last_statistics_id INTEGER NOT NULL," +
                    "name TEXT NOT NULL," +
                    "title TEXT NOT NULL," +
                    "options TEXT NOT NULL," +
                    "positions TEXT NOT NULL," +
                    "published INTEGER NOT NULL);" +
                    "CREATE UNIQUE INDEX uniq ON landings(site_id, name);"
            );



        }

        //При обновлении приложения
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS appLP");
            onCreate(db);
        }
    }
}