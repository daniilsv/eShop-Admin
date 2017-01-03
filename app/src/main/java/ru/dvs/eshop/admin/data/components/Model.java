package ru.dvs.eshop.admin.data.components;

import android.database.Cursor;
import android.view.View;
import org.json.JSONException;
import org.json.JSONObject;
import ru.dvs.eshop.admin.Core;
import ru.dvs.eshop.admin.data.DB;
import ru.dvs.eshop.admin.data.Site;
import ru.dvs.eshop.admin.data.network.FileGetQuery;
import ru.dvs.eshop.admin.data.network.PostQuery;
import ru.dvs.eshop.admin.utils.Function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Model {
    public final String controller;
    public final String type;
    public final Site site;
    public int id;
    public int original_id;
    public int parent_id;
    public int level;
    public boolean is_enabled;
    protected View editView;
    protected String imageToSave;
    private String mWhere = null;
    private String mOrder = null;
    private ArrayList<Model> mTmpArray = new ArrayList<>();

    public Model(String _controller, String _type) {
        controller = _controller;
        type = _type;
        site = Core.getInstance().site;
    }

    protected Object newInstance(Cursor c) {
        return null;
    }

    public void getFromSite(HashMap<String, String> additional, final Function callbackSuccess, final Function callbackFailed) {
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

    public void parseResponseGet(String response) {
    }

    public void editOnSite(final HashMap<String, String> data, final Function callbackSuccess, final Function callbackFailed) {
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

    public void parseResponseEdit(String response, HashMap<String, String> data) {
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

    public void addToSite(final HashMap<String, String> data, final Function callbackSuccess, final Function callbackFailed) {
        PostQuery task = new PostQuery(site.host, site.token, controller, "add." + type) {
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
        task.put("data", data);
        task.execute();
    }

    public void addToSite(HashMap<String, String> data, final Function callback) {
        addToSite(data, callback, callback);
    }

    public void parseResponseAdd(String response, HashMap<String, String> data) {
    }

    public void deleteFromSite(int orig_id, final Function callbackSuccess, final Function callbackFailed) {
        PostQuery task = new PostQuery(site.host, site.token, controller, "delete." + type) {
            @Override
            protected void onPostExecute(Void voids) {
                if (status != 0) {
                    if (callbackFailed != null)
                        callbackFailed.run();
                    return;
                }
                parseResponseDelete(response);
                if (callbackSuccess != null)
                    callbackSuccess.run();
            }
        };
        task.put("id", orig_id + "");
        task.execute();
    }

    public void deleteFromSite(int orig_id, final Function callback) {
        deleteFromSite(orig_id, callback, callback);
    }

    public void parseResponseDelete(String response) {
    }

    public void reorderOnSite(final ArrayList arr, final Function callbackSuccess, final Function callbackFailed) {
        ArrayList<Integer> items = new ArrayList<>();
        for (Object item : arr) {
            items.add(((Model) item).original_id);
        }
        PostQuery task = new PostQuery(site.host, site.token, controller, "reorder." + type) {
            @Override
            protected void onPostExecute(Void voids) {
                if (status != 0) {
                    if (callbackFailed != null)
                        callbackFailed.run();
                    return;
                }
                parseResponseReorder(response, arr);
                if (callbackSuccess != null)
                    callbackSuccess.run();
            }
        };
        task.put("what", type);
        task.put("items", items);
        task.execute();
    }

    public void reorderOnSite(ArrayList arr, final Function callback) {
        reorderOnSite(arr, callback, callback);
    }

    public void parseResponseReorder(String response, ArrayList<Model> arr) {
    }

    public HashMap<String, String> getHashMap() {
        return new HashMap<>();
    }

    public ArrayList getItems() {
        return null;
    }

    public Model getItemById(int id) {
        return null;
    }

    public void addToDB() {
    }

    public void deleteFromDB() {
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

    public String loadIconsFromSite(String icons, String folder) {
        HashMap<String, String> icons_href = new HashMap<>();
        try {
            if (icons.length() == 2)
                return new JSONObject(icons_href).toString();
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

    protected ArrayList<Model> makeTypeChildsTree(HashMap<Integer, Model> items) {

        HashMap<Integer, ArrayList<Model>> parent_items = new HashMap<>();

        for (int id : items.keySet()) {
            Model item = items.get(id);
            if (parent_items.get(item.parent_id) == null) {
                ArrayList<Model> t = new ArrayList<>();
                t.add(item);
                parent_items.put(item.parent_id, t);
            } else {
                ArrayList<Model> t = parent_items.get(item.parent_id);
                t.add(item);
                parent_items.put(item.parent_id, t);
            }
        }
        fillTypeChildBranch(parent_items, 0, 0);
        ArrayList<Model> ret = mTmpArray;
        mTmpArray = new ArrayList<>();

        return ret;

    }

    private void fillTypeChildBranch(HashMap<Integer, ArrayList<Model>> parent_items, int cur_id, int level) {
        if (!parent_items.containsKey(cur_id)) {
            return;
        }
        for (Model p_c : parent_items.get(cur_id)) {
            p_c.level = level;
            mTmpArray.add(p_c);
            fillTypeChildBranch(parent_items, p_c.original_id, level + 1);
        }
    }


    public void fillViewForListItem(View view) {
    }

    public void fillViewForReadItem(View insertPointView) {
    }

    public void fillViewForEditItem(View insertPointView) {
    }

    public void setImageByActivity(String imageUri) {
    }

    public HashMap parseEditItem(View containerView) {
        return null;
    }

    public void uploadIcon() {
    }

    public Model refresh() {
        return getItemById(id);
    }
}


