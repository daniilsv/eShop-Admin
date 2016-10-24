package ru.dvs.eshop.admin.data.components.eshop;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TextInputEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import ru.dvs.eshop.R;
import ru.dvs.eshop.admin.Core;
import ru.dvs.eshop.admin.data.DB;
import ru.dvs.eshop.admin.data.components.Model;
import ru.dvs.eshop.admin.data.network.FileSendQuery;
import ru.dvs.eshop.admin.ui.activities.ItemActivity;

/**
 * Производитель
 */
public class Vendor extends Model {
    public String title;
    public HashMap<String, Drawable> icons; //Сами иконки в памяти устройства
    public String description;
    public int ordering; //Порядок вывода(сортировка)
    public String url;
    HashMap<String, String> icons_href; //Ссылки на иконки
    //Иконки = normal, big, small

    public Vendor() {
        super("eshop", "vendor");
        title = "";
        description = "";
        is_enabled = true;
        ordering = -1;
        url = "";
        icons_href = new HashMap<>();
        icons = new HashMap<>();

    }

    public Vendor(Cursor c) {
        super("eshop", "vendor");
        id = c.getInt(c.getColumnIndex("id"));
        original_id = c.getInt(c.getColumnIndex("original_id"));
        is_enabled = c.getInt(c.getColumnIndex("is_enabled")) == 1;
        title = c.getString(c.getColumnIndex("title"));
        description = c.getString(c.getColumnIndex("description"));
        url = c.getString(c.getColumnIndex("url"));
        ordering = c.getInt(c.getColumnIndex("ordering"));
        String icon = c.getString(c.getColumnIndex("icon"));
        loadIcons(icon);
    }

    private void loadIcons(String icon) {
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

    public HashMap<String, String> getHashMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("original_id", original_id + "");
        map.put("is_enabled", (is_enabled ? 1 : 0) + "");
        map.put("title", title);
        map.put("description", description);
        map.put("url", url);
        map.put("ordering", ordering + "");
        map.put("icon", new JSONObject(icons_href).toString());
        return map;
    }

    public void addToDB() {
        id = (int) DB.insert("com_eshop_vendors", getHashMap());
    }

    public void deleteFromDB() {
        DB.delete("com_eshop_vendors", "id=" + id, null);
    }

    @Override
    public ArrayList getItems() {
        return orderBy("ordering", "ASC").
                getFromDataBase("eshop_vendors");
    }


    @Override
    public Model getItemById(int id) {
        return (Vendor) getByItemId("eshop_vendors", id);
    }

    @Override
    public void parseResponseGet(String response) {
        ArrayList<Model> items = getItems();
        HashMap<Integer, Boolean> original_ids = new HashMap<>();
        for (Model item : items)
            original_ids.put(item.original_id, true);
        try {
            //Распарсиваем полученную JSON-строку
            JSONObject node_root = new JSONObject(response).getJSONObject("items");
            Iterator<String> keys = node_root.keys();
            if (keys == null)
                return;
            //Пока есть записи
            while (keys.hasNext()) {
                //Заполняем ассоциативный массив
                JSONObject item = node_root.getJSONObject(keys.next());
                HashMap<String, String> map = new HashMap<>();
                map.put("original_id", item.getInt("id") + "");
                map.put("is_enabled", item.getInt("is_enabled") + "");
                map.put("title", item.getString("title") + "");
                map.put("icon", loadIconsFromSite(item.getString("icon"), "vendors/" + item.getInt("id")));
                map.put("description", item.getString("description") + "");
                map.put("url", item.getString("url") + "");
                map.put("ordering", item.getInt("ordering") + "");
                DB.insertOrUpdate("com_eshop_vendors", "original_id=" + item.getInt("id"), map);
                original_ids.remove(item.getInt("id"));
            }
        } catch (JSONException e) {
            //ERROR while parse data!
            e.printStackTrace();
        }
        Set<Integer> keys = original_ids.keySet();
        for (int orig_id : keys) {
            DB.delete("com_eshop_vendors", "original_id=" + orig_id, null);
        }
    }

    @Override
    public void parseResponseReorder(String response, ArrayList arr) {
        int i = 0;
        for (Object item : arr) {
            HashMap<String, String> map = new HashMap<>();
            map.put("ordering", ++i + "");
            DB.update("com_eshop_vendors", ((Model) item).id, map);
        }
    }

    public void parseResponseEdit(String response, HashMap<String, String> data) {
        DB.update("com_eshop_vendors", id, data);
    }

    public void parseResponseAdd(String response, HashMap<String, String> data) {
        DB.update("com_eshop_vendors", id, data);
    }

    @Override
    protected Vendor newInstance(Cursor c) {
        return new Vendor(c);
    }

    public void fillViewForListItem(View view) {
        TextView textView = (TextView) view.findViewById(R.id.title);
        ImageView handleView = (ImageView) view.findViewById(R.id.image);
        View isVisibleView = view.findViewById(R.id.is_visible);
        if (!is_enabled)
            isVisibleView.setVisibility(View.VISIBLE);
        else
            isVisibleView.setVisibility(View.GONE);
        textView.setText(title);
        handleView.setImageDrawable(icons.get("small"));
    }

    @Override
    public void fillViewForReadItem(View insertPointView) {
        LayoutInflater vi = (LayoutInflater) Core.getInstance().activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(R.layout.view_vendor, null);
        ((TextView) v.findViewById(R.id.view_vendor_title)).setText(title);
        ((TextView) v.findViewById(R.id.view_vendor_description)).setText(description);
        ((TextView) v.findViewById(R.id.view_vendor_url)).setText(url);
        ((ViewGroup) insertPointView).addView(v, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    @Override
    public void fillViewForEditItem(View insertPointView) {
        LayoutInflater vi = (LayoutInflater) Core.getInstance().activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        editView = vi.inflate(R.layout.edit_vendor, null);

        ((TextInputEditText) editView.findViewById(R.id.edit_vendor_title)).setText(title);
        ((TextInputEditText) editView.findViewById(R.id.edit_vendor_description)).setText(description);
        ((TextInputEditText) editView.findViewById(R.id.edit_vendor_url)).setText(url);

        ((ImageView) editView.findViewById(R.id.edit_vendor_image)).setImageDrawable(icons.get("normal"));

        editView.findViewById(R.id.button_select_vendor_image).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                Core.getInstance().activity.startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), ItemActivity.EDIT_IMAGE_SELECT);
            }
        });

        ((ViewGroup) insertPointView).addView(editView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    @Override
    public void setImageByActivity(String imageUri) {
        imageToSave = imageUri;
        Drawable drawable = Drawable.createFromPath(imageToSave);
        ((ImageView) editView.findViewById(R.id.edit_vendor_image)).setImageDrawable(drawable);
    }

    @Override
    public HashMap parseEditItem(View containerView) {
        HashMap ret = new HashMap();
        ret.put("ordering", ordering);
        ret.put("is_enabled", 1);
        ret.put("title", ((TextInputEditText) containerView.findViewById(R.id.edit_vendor_title)).getText().toString());
        ret.put("description", ((TextInputEditText) containerView.findViewById(R.id.edit_vendor_description)).getText().toString());
        ret.put("url", ((TextInputEditText) containerView.findViewById(R.id.edit_vendor_url)).getText().toString());
        if (imageToSave != null)
            new FileSendQuery(site.host, controller, imageToSave, type, original_id, "icon") {
                @Override
                protected void onPostExecute(Void aVoid) {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("icon", loadIconsFromSite(response, "vendors/" + original_id));
                    DB.insertOrUpdate("com_eshop_vendors", "original_id=" + original_id, map);
                    loadIcons(response);
                }
            }.execute();
        return ret;
    }
}