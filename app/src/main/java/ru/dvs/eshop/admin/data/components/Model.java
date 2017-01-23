package ru.dvs.eshop.admin.data.components;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.view.View;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;
import ru.dvs.eshop.admin.data.DataBase;
import ru.dvs.eshop.admin.data.Site;
import ru.dvs.eshop.admin.data.network.FileGetQuery;
import ru.dvs.eshop.admin.data.network.PostQuery;
import ru.dvs.eshop.admin.ui.activities.MainActivity;
import ru.dvs.eshop.admin.utils.Callback;
import ru.dvs.eshop.admin.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class Model {
    public final String controller;
    public final String type;
    public final String table;
    public final Site site;
    public int localId;
    public int originalId;
    public boolean isEnabled;
    public String title;
    public HashMap<String, String> iconHrefs = null;
    protected HashMap<String, Drawable> iconsLocal = null;
    private Context mContext = null;
    private String mWhere = null;
    private String mOrder = null;

    public Model(String controller, String type, String table) {
        this.controller = controller;
        this.type = type;
        this.table = table;
        this.site = MainActivity.site;
        this.localId = 0;
        this.originalId = 0;
        iconHrefs = new HashMap<>();
    }

    public Model(String controller, String type, String table, int localId, int originalId) {
        this.controller = controller;
        this.type = type;
        this.table = table;
        this.site = MainActivity.site;
        this.localId = localId;
        this.originalId = originalId;
        iconHrefs = new HashMap<>();
    }

    protected void setContext(Context context) {
        mContext = context;
    }

    public void queryItemsFromSite(final HashMap<String, String> additional, Callback.ISuccessError callback) {
        PostQuery task = new PostQuery(mContext, site, controller, "get." + type) {
            @Override
            public void onSuccess() {
                try {
                    Model item;
                    DataBase db = new DataBase(mContext);

                    if (mResponse.opt("count") == null) {
                        item = parseItemByJson(mResponse.getJSONObject("item").toString());
                        HashMap map = item.getHashMap();
                        db.insertOrUpdate(table, "original_id=" + item.originalId, map);
                    } else {
                        JSONObject items = mResponse.getJSONObject("items");
                        Iterator<String> iterator = items.keys();
                        while (iterator.hasNext()) {
                            item = parseItemByJson(items.getJSONObject(iterator.next()).toString());
                            HashMap map = item.getHashMap();
                            db.insertOrUpdate(table, "original_id=" + item.originalId, map);
                        }
                    }
                    db.close();
                } catch (JSONException ignored) {
                }
            }

            @Override
            public void onError() {

            }
        };
        if (additional != null)
            for (Map.Entry o : additional.entrySet()) {
                task.put((String) o.getKey(), (String) o.getValue());
            }
        if (callback != null)
            task.setCallback(callback);
        task.execute();
    }

    public ArrayList<Model> getItems() {
        ArrayList<Model> items = new ArrayList<>();
        DataBase db = new DataBase(mContext);
        if (mOrder == null) orderBy("ordering", "ASC");
        Cursor cursor = db.query(table, null, mWhere, null, null, null, mOrder);

        if (cursor == null || !cursor.moveToFirst()) return items;
        try {
            do {
                Model item = parseCursorFromDB(cursor);
                item.setContext(mContext);
                items.add(item);
            } while (cursor.moveToNext());
        } finally {
            cursor.close();
            db.close();
            clearDataBase();
        }
        return items;
    }

    public Model getItemById(int localId) {
        return filter("id=" + localId).getItemFiltered();
    }

    public Model getItemFiltered() {
        Model ret = null;
        DataBase db = new DataBase(mContext);
        Cursor cursor = db.query(table, null, mWhere, null, null, null, null);

        if (cursor == null || !cursor.moveToFirst()) return null;
        try {
            ret = parseCursorFromDB(cursor);
            ret.setContext(mContext);
        } finally {
            cursor.close();
            db.close();
            clearDataBase();
        }
        return ret;
    }

    public void deleteItem(int localId) {

    }

    public Model filter(String where) {
        mWhere = where;
        return this;
    }

    public Model orderBy(String key, String order) {
        mOrder = "`" + key + "` " + order;
        return this;
    }

    public void clearDataBase() {
        mWhere = null;
        mOrder = null;
    }

    public abstract HashMap getHashMap();

    public Model parseItemByJson(String json) {
        try {
            Class dataClass = Class.forName(this.getClass().getName() + "$Data");
            Data data = (Data) new ObjectMapper().readValue(json, dataClass);
            return data.item;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void loadIconsFromSite() {
        if (iconHrefs == null) return;
        if (new File(Utils.fileName(iconHrefs.get("small"))).exists())
            return;
        FileGetQuery fileSmall = new FileGetQuery(mContext, site.host + "" + iconHrefs.get("small"), "images/" + type + "/" + Utils.fileName(iconHrefs.get("small")));
        FileGetQuery fileNormal = new FileGetQuery(mContext, site.host + "" + iconHrefs.get("normal"), "images/" + type + "/" + Utils.fileName(iconHrefs.get("normal")));
        FileGetQuery fileBig = new FileGetQuery(mContext, site.host + "" + iconHrefs.get("big"), "images/" + type + "/" + Utils.fileName(iconHrefs.get("big")));
        try {
            fileSmall.join();
            fileNormal.join();
            fileBig.join();
        } catch (InterruptedException ignored) {
        }
    }

    public abstract Model parseCursorFromDB(Cursor cursor);

    public abstract void fillViewForList(View itemView);

    protected static abstract class Data {
        @JsonIgnore
        public Model item;
        @JsonIgnore
        protected Cursor cursor;

        protected final int cursorGetInt(String column) {
            return cursor.getInt(cursor.getColumnIndex(column));
        }

        protected final String cursorGetString(String column) {
            return cursor.getString(cursor.getColumnIndex(column));
        }

        protected abstract void setVarByItem(String name);

        protected abstract void setItemVar(String name, Object value);
    }
}
