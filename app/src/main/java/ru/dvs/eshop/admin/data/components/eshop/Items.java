package ru.dvs.eshop.admin.data.components.eshop;

/**
 * Created by MSI1 on 22.08.2016.
 */

import android.database.Cursor;
import android.graphics.drawable.Drawable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import ru.dvs.eshop.admin.data.DB;
import ru.dvs.eshop.admin.data.components.Model;
import ru.dvs.eshop.admin.data.network.POSTQuery;

public class PaymentTypes extends Model {
    int original_id;
    int id;
    boolean is_enabled;
    int ordering; //Порядок вывода(сортировка)
    int category_id;
    String art_no;
    String title;
   // DATETIME date_pub; //TODO: WTF!?!?!?
    float price;
    String price_old;
    float quantity;
    String desc_short;
    String desc_full;
    int vendor_id;
    String category_add_id;
    String img;
    HashMap<String, String> images_href; //Ссылки на картинки
    HashMap<String, Drawable> images; //Сами иконки в памяти устройства
    HashMap<String, String> chars; //TODO: Ссылки на символы??!?!?!
    String meta_keys;
    String meta_desc;
    String url;
    String tpl;


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

//TODO: Ask DVS for response syntax number 1

//NOTE: foreach items as item {

                    int orig_id = item.getInt("original_id");
                    HashMap<String, String> map = new HashMap<>();
                    map.put("original_id", "");
                    map.put("is_enabled", "");
                    map.put("name", "");
                    map.put("title", "");
                    map.put("icon", "");
                    map.put("description", "");
                    map.put("url", "");
                    map.put("ordering", "");
                    map.put("options", "");
                    map.put("category_id", "");
                    map.put("art_no", "");
                    map.put("datetime", "");
                    map.put("price", "");
                    map.put("price_old", "");
                    map.put("quantity", "");
                    map.put("desc_short", "");
                    map.put("desc_full", "");
                    map.put("vendor_id", "");
                    map.put("category_add_id", "");
                    map.put("img", "");
                    map.put("images_href", "");
                    map.put("images", "");
                    map.put("chars", "");
                    map.put("meta_keys", "");
                    map.put("meta_desc", "");
                    map.put("url", "");
                    map.put("tpl", "");




                    //Проверяем таблицу на наличие элемента с таким original_id
                    Cursor bd_item = DB.query("com_eshop_items", null, null, null, null, "original_id=" + orig_id, null);
                    if (bd_item != null && bd_item.moveToFirst()) {//Элемент в таблице есть - обновляем данные
                        DB.update("com_eshop_items", bd_item.getInt(bd_item.getColumnIndex("id")), map);
                    } else {//Элемента в таблиц нет - добавляем его
                        DB.insert("com_eshop_items", map);
                    }
//NOTE: }
                } catch (JSONException e) {
                    //ERROR while parse data!
                    e.printStackTrace();
                }
            }
        };
        task.put("controller", "eshop");
        task.put("method", "get_items");
        task.execute();
    }

}