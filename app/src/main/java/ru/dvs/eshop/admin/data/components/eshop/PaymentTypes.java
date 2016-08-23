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

/**
 * Виды оплаты
 */
public class PaymentTypes extends Model {
    int original_id;
    int id;
    boolean is_enabled;
    int ordering; //Порядок вывода(сортировка)
    String name;
    String title;
    String description;
    //Иконки - original, big, small
    HashMap<String, String> icons_href; //Ссылки на иконки
    HashMap<String, Drawable> icons; //Сами иконки в памяти устройства
    ArrayList options;


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
                    //Проверяем таблицу на наличие элемента с таким original_id
                    Cursor bd_item = DB.query("com_eshop_payment_types", null, null, null, null, "original_id=" + orig_id, null);
                    if (bd_item != null && bd_item.moveToFirst()) {//Элемент в таблице есть - обновляем данные
                        DB.update("com_eshop_payment_types", bd_item.getInt(bd_item.getColumnIndex("id")), map);
                    } else {//Элемента в таблиц нет - добавляем его
                        DB.insert("com_eshop_payment_types", map);
                    }
//NOTE: }
                } catch (JSONException e) {
                    //ERROR while parse data!
                    e.printStackTrace();
                }
            }
        };
        task.put("controller", "eshop");
        task.put("method", "get_payment_types");
        task.execute();
    }

}
