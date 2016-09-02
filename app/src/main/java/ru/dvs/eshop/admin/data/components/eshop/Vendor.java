package ru.dvs.eshop.admin.data.components.eshop;

import android.database.Cursor;
import android.graphics.drawable.Drawable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import ru.dvs.eshop.admin.Core;
import ru.dvs.eshop.admin.data.DB;
import ru.dvs.eshop.admin.data.Site;
import ru.dvs.eshop.admin.data.components.Model;
import ru.dvs.eshop.admin.data.network.FILEQuery;
import ru.dvs.eshop.admin.data.network.POSTQuery;

/**
 * Производитель
 */
public class Vendor extends Model {
    public int id;
    public boolean is_enabled;
    public String title;
    public HashMap<String, Drawable> icons; //Сами иконки в памяти устройства
    public String description;
    public int ordering; //Порядок вывода(сортировка)
    public String url;
    int original_id;
    HashMap<String, String> icons_href; //Ссылки на иконки
    //Иконки = original, big, small

    public Vendor() {
        super();

    }

    private Vendor(Cursor c) {
        super();

        id = c.getInt(c.getColumnIndex("id"));
        original_id = c.getInt(c.getColumnIndex("original_id"));
        is_enabled = c.getInt(c.getColumnIndex("is_enabled")) == 1;
        title = c.getString(c.getColumnIndex("title"));
        description = c.getString(c.getColumnIndex("description"));
        url = c.getString(c.getColumnIndex("url"));
        ordering = c.getInt(c.getColumnIndex("ordering"));
        String icon = c.getString(c.getColumnIndex("icon"));
        icons_href = new HashMap<>();
        icons = new HashMap<>();
        try {
            JSONObject icon_node = new JSONObject(icon);
            Iterator<String> icon_keys = icon_node.keys();
            if (icon_keys != null)
                while (icon_keys.hasNext()) {
                    String key = icon_keys.next();
                    String href = icon_node.getString(key);
                    icons_href.put(key, href);
                    String tmp[] = href.split("/");
                    Drawable d = Drawable.createFromPath(Core.getStorageDir() + "/icons/vendors/" + original_id + "/" + tmp[tmp.length - 1]);
                    icons.put(key, d);
                }
        } catch (JSONException ignored) {
        }
    }

    //Получение загруженных лидов из БД
    public static ArrayList<Vendor> getVendors() {
        ArrayList<Vendor> ret = new ArrayList<>();
        Cursor c = DB.query("com_eshop_vendors", null, null, null, null, null, "ordering ASC");
        if (c == null || !c.moveToFirst()) return ret;
        do {
            ret.add(new Vendor(c));
        } while (c.moveToNext());
        c.close();
        return ret;
    }

    public static void loadFromSite() {
        Site site = Core.getInstance().site;
        POSTQuery task = new POSTQuery(site.host, site.token) {
            @Override
            protected void onPostExecute(Void voids) {
                if (status != 0)
                    return;
                parseResponse(response);
            }
        };
        task.put("controller", "eshop");
        task.put("method", "get_vendor");
        task.execute();
    }

    private static void parseResponse(String response) {
        Site site = Core.getInstance().site;
        HashMap<String, String> icons_href = new HashMap<>();
        try {
            //Распарсиваем полученную JSON-строку
            JSONObject node_root = new JSONObject(response);
            Iterator<String> keys = node_root.keys();
            if (keys == null)
                return;
            //Пока есть записи
            while (keys.hasNext()) {
                //Заполняем ассоциативный массив
                JSONObject item = node_root.getJSONObject(keys.next());
                int orig_id = item.getInt("id");
                HashMap<String, String> map = new HashMap<>();
                map.put("original_id", item.getInt("id") + "");
                map.put("is_enabled", item.getInt("is_enabled") + "");
                map.put("title", item.getString("title") + "");
                JSONObject icon_node = new JSONObject(item.getString("icon"));
                Iterator<String> icon_keys = icon_node.keys();
                if (icon_keys != null)
                    while (icon_keys.hasNext()) {
                        String key = icon_keys.next();
                        if (key.equals("big") || key.equals("small") || key.equals("original")) {
                            String href = "/upload/" + icon_node.getString(key);
                            icons_href.put(key, href);
                            String tmp[] = href.split("/");
                            new FILEQuery(site.host + href, Core.getStorageDir() + "/icons/vendors/" + orig_id + "/" + tmp[tmp.length - 1]).execute();
                        }
                    }
                String icon = new JSONObject(icons_href).toString();
                map.put("icon", icon);
                map.put("description", item.getString("description") + "");
                map.put("url", item.getString("url") + "");
                map.put("ordering", item.getInt("ordering") + "");
                //Проверяем таблицу на наличие элемента с таким original_id
                Cursor bd_item = DB.query("com_eshop_vendors", null, "original_id=" + orig_id, null, null, null, null);
                if (bd_item != null && bd_item.moveToFirst()) {//Элемент в таблице есть - обновляем данные
                    DB.update("com_eshop_vendors", bd_item.getInt(bd_item.getColumnIndex("id")), map);
                } else {//Элемента в таблиц нет - добавляем его
                    DB.insert("com_eshop_vendors", map);
                }

            }
        } catch (JSONException e) {
            //ERROR while parse data!
            e.printStackTrace();
        }
    }

}

/*
NOTE: Примерно в таком виде сайт отдает данные
{
    "id":"1",
    "is_enabled":"1",
    "title":"\u041f\u0435\u0440\u0432\u044b\u0439 \u043f\u0440\u043e\u0438\u0437\u0432\u043e\u0434\u0438\u0442\u0435\u043b\u044c",
    "icon":{
        "original":"000\/u1\/001\/9fe03690.png",
        "small":"000\/u1\/001\/4f0e08bb.png",
        "big":"000\/u1\/001\/25c71a2f.png"
    },
    "description":"\u041d\u0435\u043a\u043e\u0435 \u043e\u043f\u0438\u0441\u0430\u043d\u0438\u0435",
    "url":"http:\/\/ya.ru\/",
    "ordering":"2"
}
*/