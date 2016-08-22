package ru.dvs.eshop.admin.data.components.eshop;

import android.database.Cursor;
import android.graphics.drawable.Drawable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import ru.dvs.eshop.admin.data.DB;
import ru.dvs.eshop.admin.data.components.Model;
import ru.dvs.eshop.admin.data.network.POSTQuery;

/**
 * Производитель
 */
public class Vendor extends Model {
    int original_id;
    int id;
    boolean is_enabled;
    String title;
    //Иконки - original, big, small
    HashMap<String, String> icons_href; //Ссылки на иконки
    HashMap<String, Drawable> icons; //Сами иконки в памяти устройства
    String description;
    int ordering; //Порядок вывода(сортировка)

    public void loadFromSite() {
        POSTQuery task = new POSTQuery(site.host, site.token) {
            @Override
            protected void onPostExecute(Void voids) {
                //here you have access to (int status) and (String response)
                if (status != 0) {
                    //ERROR while retrieving data!
                    //response has error code.
                    return;
                }
                try {
                    JSONObject node = new JSONObject(response);

//TODO: Ask DVS for response syntax number 0

//NOTE: foreach items as item {
                    int orig_id = item.getInt("original_id");
                    HashMap<String, String> map = new HashMap<>();
                    map.put("original_id", "");
                    map.put("is_enabled", "");
                    map.put("title", "");
                    map.put("icon", "");
                    map.put("description", "");
                    map.put("url", "");
                    map.put("ordering", "");
                    //Проверяем таблицу на наличие элемента с таким original_id
                    Cursor bd_item = DB.query("com_eshop_vendors", null, null, null, null, "original_id=" + orig_id, null);
                    if (bd_item != null && bd_item.moveToFirst()) {//Элемент в таблице есть - обновляем данные
                        DB.update("com_eshop_vendors", bd_item.getInt(bd_item.getColumnIndex("id")), map);
                    } else {//Элемента в таблиц нет - добавляем его
                        DB.insert("com_eshop_vendors", map);
                    }
//NOTE: }
                } catch (JSONException e) {
                    //ERROR while parse data!
                    e.printStackTrace();
                }
            }
        };
        task.put("controller", "eshop");
        task.put("method", "get_vendors");
        task.execute();
    }

}
