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
 * Created by MSI1 on 25.08.2016.
 */
public class PaymentType extends Model {

    int original_id;
    int id;
    boolean is_enabled;
    int ordering; //Порядок вывода(сортировка)
    String name;
    String description;
    String title;
    //Иконки - original, big, small
    HashMap<String, String> icons_href; //Ссылки на иконки
    HashMap<String, Drawable> icons; //Сами иконки в памяти устройства
    String options;
    public PaymentType() {
        super();
    }

    private PaymentType(Cursor c) {
        super();

        id = c.getInt(c.getColumnIndex("id"));
        original_id = c.getInt(c.getColumnIndex("original_id"));
        is_enabled = c.getInt(c.getColumnIndex("is_enabled")) == 1;
        ordering = c.getInt(c.getColumnIndex("ordering"));
        name = c.getString(c.getColumnIndex("name"));
        title = c.getString(c.getColumnIndex("title"));
        description = c.getString(c.getColumnIndex("description"));
        String icon = c.getString(c.getColumnIndex("icon"));
        icons_href = new HashMap<>();
        icons = new HashMap<>();
        options = c.getString(c.getColumnIndex("options"));

        try {
            JSONObject icon_node = new JSONObject(icon);
            Iterator<String> icon_keys = icon_node.keys();
            if (icon_keys != null)
                while (icon_keys.hasNext()) {
                    String key = icon_keys.next();
                    String href = icon_node.getString(key);
                    icons_href.put(key, href);
                    String tmp[] = href.split("/");
                    Drawable d = Drawable.createFromPath(Core.getStorageDir() + "/icons/payment_types/" + original_id + "/" + tmp[tmp.length - 1]);
                    icons.put(key, d);
                }
        } catch (JSONException ignored) {
        }
    }

    //Получение загруженных лидов из БД
    public static ArrayList<PaymentType> getPaymentType() {
        ArrayList<PaymentType> ret = new ArrayList<>();
        Cursor c = DB.query("com_eshop_payment_types", null, null, null, null, null, "ordering ASC");
        if (c == null || !c.moveToFirst()) return ret;
        do {
            ret.add(new PaymentType(c));
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
        task.put("method", "get_payment_type");
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
            /*
            id
            is_enabled
            ordering
            name
            title
            description
            icon
            options
            */
            //Пока есть записи
            while (keys.hasNext()) {
                //Заполняем ассоциативный массив
                JSONObject item = node_root.getJSONObject(keys.next());
                int orig_id = item.getInt("id");
                HashMap<String, String> map = new HashMap<>();
                map.put("original_id", item.getInt("id") + "");
                map.put("is_enabled", item.getInt("is_enabled") + "");
                map.put("ordering", item.getInt("ordering") + "");
                map.put("name", item.getString("name") + "");
                map.put("title", item.getString("title") + "");
                map.put("description", item.getString("description") + "");
                JSONObject icon_node = new JSONObject(item.getString("icon"));
                Iterator<String> icon_keys = icon_node.keys();
                if (icon_keys != null)
                    while (icon_keys.hasNext()) {
                        String key = icon_keys.next();
                        if (key.equals("big") || key.equals("small") || key.equals("original")) {
                            String href = "/upload/" + icon_node.getString(key);
                            icons_href.put(key, href);
                            String tmp[] = href.split("/");
                            new FILEQuery(site.host + href, Core.getStorageDir() + "/icons/payment_types/" + orig_id + "/" + tmp[tmp.length - 1]).execute();
                        }
                    }
                String icon = new JSONObject(icons_href).toString();
                map.put("icon", icon);
                map.put("options", item.getString("options") + "");

                //Проверяем таблицу на наличие элемента с таким original_id
                Cursor bd_item = DB.query("com_eshop_payment_types", null, "original_id=" + orig_id, null, null, null, null);
                if (bd_item != null && bd_item.moveToFirst()) {//Элемент в таблице есть - обновляем данные
                    DB.update("com_eshop_payment_types", bd_item.getInt(bd_item.getColumnIndex("id")), map);
                } else {//Элемента в таблиц нет - добавляем его
                    DB.insert("com_eshop_payment_types", map);
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
"id":"2",
"is_enabled":"1",
"ordering":null,
"name":"in_cash",
"title":"\u041d\u0430\u043b\u0438\u0447\u043d\u044b\u043c\u0438",
"description":"\u041d\u0430\u043b\u0438\u0447\u043d\u044b\u0439 \u0440\u0430\u0441\u0447\u0435\u0442 \u043f\u043e \u0444\u0430\u043a\u0442\u0443 \u043f\u0440\u043e\u0434\u0430\u0436\u0438",
"icon":[],
"options":{"admin":"newnewnew"},
"backend":{
    "type":"fieldset",
    "childs":[{
        "title":"\u0428\u0430\u0431\u043b\u043e\u043d",
        "sql":"varchar({max_length}) NULL DEFAULT NULL",
        "filter_type":"str",
        "var_type":"string",
        "name":"options:admin",
        "element_name":"options[admin]",
        "filter_hint":false,
        "element_title":"\u0428\u0430\u0431\u043b\u043e\u043d",
        "is_public":true,
        "cache_sql":null,
        "allow_index":true,
        "is_denormalization":false,
        "item":null,
        "is_virtual":false,
        "is_hidden":false,
        "rules":[["required"]],
        "options":[],
        "data":[],
        "class":"string",
        "id":"options_admin"}]
    }
}

*/
