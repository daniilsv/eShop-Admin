package ru.dvs.eshop.admin.data.components;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ru.dvs.eshop.admin.Core;
import ru.dvs.eshop.admin.data.DB;
import ru.dvs.eshop.admin.data.Site;
import ru.dvs.eshop.admin.data.network.FILEQuery;
import ru.dvs.eshop.admin.data.network.POSTQuery;

public class Model {
    public int id;
    public int original_id;
    protected Site site;
    protected String mController;
    protected String mType;
    private String mWhere = null;
    private String mOrder = null;

    public Model(String controller, String type) {
        mController = controller;
        mType = type;
        site = Core.getInstance().site;
    }

    protected Object newInstance(Cursor c) {
        return null;
    }

    public void getFromSite(HashMap<String, String> additional) {
        POSTQuery task = new POSTQuery(site.host, site.token, mController, "get") {
            @Override
            protected void onPostExecute(Void voids) {
                if (status != 0)
                    return;
                parseResponseGet(response);
            }
        };
        task.put("what", mType);
        if (additional != null)
            for (Map.Entry o : additional.entrySet()) {
                task.put((String) o.getKey(), (String) o.getValue());
            }
        task.execute();
    }

    public void parseResponseGet(String response) {
    }

    public void parseResponseReorder(String response, ArrayList<Model> arr) {
    }

    public ArrayList getFromDataBase(String table) {
        ArrayList ret = new ArrayList();
        Cursor c = DB.query("com_" + table, null, mWhere, null, null, null, mOrder);
        if (c == null || !c.moveToFirst()) return ret;
        do {
            ret.add(newInstance(c));
        } while (c.moveToNext());
        c.close();
        cleanDB();
        return ret;
    }

    public void cleanDB() {
        mWhere = null;
        mOrder = null;
    }

    public Model filter(String where) {
        mWhere = where;
        return this;
    }

    public Model orderBy(String key, String order) {
        mOrder = "`" + key + "` " + order;
        return this;
    }

    public void reorderItems(ArrayList<Integer> items, final ArrayList arr) {
        POSTQuery task = new POSTQuery(site.host, site.token, mController, "reorder") {
            @Override
            protected void onPostExecute(Void voids) {
                if (status != 0)
                    return;
                parseResponseReorder(response, arr);
            }
        };
        task.put("what", mType);
        task.put("items", items);
        task.execute();
    }

    public String loadIconsFromSite(String icons, String folder) {
        HashMap<String, String> icons_href = new HashMap<>();
        try {
            JSONObject icon_node = new JSONObject(icons);
            Iterator<String> icon_keys = icon_node.keys();
            if (icon_keys != null)
                while (icon_keys.hasNext()) {
                    String key = icon_keys.next();
                    if (key.equals("big") || key.equals("small") || key.equals("normal")) {
                        String href = "/upload/" + icon_node.getString(key);
                        icons_href.put(key, href);
                        String tmp[] = href.split("/");
                        new FILEQuery(site.host + href, Core.getStorageDir() + "/icons/" + folder + "/" + tmp[tmp.length - 1]).execute();
                    }
                }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject(icons_href).toString();
    }
}


