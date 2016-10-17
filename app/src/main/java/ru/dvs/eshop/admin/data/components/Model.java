package ru.dvs.eshop.admin.data.components;

import android.database.Cursor;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ru.dvs.eshop.admin.Core;
import ru.dvs.eshop.admin.data.DB;
import ru.dvs.eshop.admin.data.Site;
import ru.dvs.eshop.admin.data.network.FileGetQuery;
import ru.dvs.eshop.admin.data.network.PostQuery;
import ru.dvs.eshop.admin.utils.Function;

public class Model {
    public final String controller;
    public final String type;
    public final Site site;
    public int id;
    public int original_id;
    public boolean is_enabled;
    private String mWhere = null;
    private String mOrder = null;

    public Model(String _controller, String _type) {
        controller = _controller;
        type = _type;
        site = Core.getInstance().site;
    }

    public ArrayList getItems() {
        return null;
    }

    public Model getItemById(int id) {
        return null;
    }

    protected Object newInstance(Cursor c) {
        return null;
    }

    public void getFromSite(HashMap<String, String> additional, final Function callbackSuccess, final Function callbackFailed) {
        PostQuery task = new PostQuery(site.host, site.token, controller, "get") {
    public void getFromSite(HashMap<String, String> additional, final Function callback) {
        PostQuery task = new PostQuery(site.host, site.token, controller, "get." + type) {
            @Override
            protected void onPostExecute(Void voids) {
                if (status != 0) {
                    if (callbackFailed != null)
                        callbackFailed.run();
                    return;
                }
                parseResponseGet(response);
                if (callbackSuccess != null)
                    callbackSuccess.run();
            }
        };
        if (additional != null)
            for (Map.Entry o : additional.entrySet()) {
                task.put((String) o.getKey(), (String) o.getValue());
            }
        task.execute();
    }

    public void getFromSite(HashMap<String, String> additional, final Function callback) {
        getFromSite(additional, callback, callback);
    }

    public void editOnSite(final HashMap<String, String> data, final Function callbackSuccess, final Function callbackFailed) {
        PostQuery task = new PostQuery(site.host, site.token, controller, "edit") {
    public void editOnSite(final HashMap<String, String> data, final Function callback) {
        PostQuery task = new PostQuery(site.host, site.token, controller, "edit." + type) {
            @Override
            protected void onPostExecute(Void voids) {
                if (status != 0) {
                    if (callbackFailed != null)
                        callbackFailed.run();
                    return;
                }
                parseResponseEdit(response, data);
                if (callbackSuccess != null)
                    callbackSuccess.run();
            }
        };
        task.put("what", type);
        task.put("id", original_id + "");
        task.put("data", data);
        task.execute();
    }

    public void editOnSite(HashMap<String, String> data, final Function callback) {
        editOnSite(data, callback, callback);
    }

    public void addToSite(final HashMap<String, String> data, final Function callbackSuccess, final Function callbackFailed) {
        PostQuery task = new PostQuery(site.host, site.token, controller, "add") {
            @Override
            protected void onPostExecute(Void voids) {
                if (status != 0) {
                    if (callbackFailed != null)
                        callbackFailed.run();
                    return;
                }
                parseResponseAdd(response, data);
                if (callbackSuccess != null)
                    callbackSuccess.run();
            }
        };
        task.put("what", type);
        task.put("data", data);
        task.execute();
    }

    public void addToSite(HashMap<String, String> data, final Function callback) {
        addToSite(data, callback, callback);
    }

    public HashMap<String, String> getHashMap() {
        return new HashMap<>();
    }

    public void addToDB() {
    }

    public void setFieldOnSite(String field, String value, Function callbackSuccess, Function callbackFailed) {
        HashMap<String, String> map = new HashMap<>();
        map.put(field, value);
        editOnSite(map, callbackSuccess, callbackFailed);
    }

    public void setFieldOnSite(String field, String value, Function callback) {
        HashMap<String, String> map = new HashMap<>();
        map.put(field, value);
        editOnSite(map, callback, callback);
    }

    public void parseResponseGet(String response) {
    }

    public void parseResponseAdd(String response, HashMap<String, String> data) {
    }

    public void parseResponseEdit(String response, HashMap<String, String> data) {
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

    public Object getByItemId(String table, int id) {
        Cursor c = DB.query("com_" + table, null, "id=" + id, null, null, null, null);
        if (c == null || !c.moveToFirst()) return null;
        cleanDB();
        Object obj = newInstance(c);
        c.close();
        return obj;
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

    public void reorderItems(final ArrayList arr, final Function callback) {
        ArrayList<Integer> items = new ArrayList<>();
        for (Object item : arr) {
            items.add(((Model) item).original_id);
        }
        PostQuery task = new PostQuery(site.host, site.token, controller, "reorder." + type) {
            @Override
            protected void onPostExecute(Void voids) {
                if (status != 0)
                    return;
                parseResponseReorder(response, arr);
                if (callback != null)
                    callback.run();
            }
        };
        task.put("what", type);
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
                        new FileGetQuery(site.host + href, Core.getStorageDir() + "/icons/" + folder + "/" + tmp[tmp.length - 1]).execute();
                    }
                }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject(icons_href).toString();
    }

    public void fillViewForListItem(View view) {
    }

    public void fillViewForReadItem(View insertPointView) {
    }

    public void fillViewForEditItem(View insertPointView) {
    }

    public HashMap parseEditItem(View containerView) {
        return null;
    }

    public Model refresh() {
        return getItemById(id);
    }
}


