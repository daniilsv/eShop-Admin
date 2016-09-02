package ru.dvs.eshop.admin.data.components;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ru.dvs.eshop.admin.Core;
import ru.dvs.eshop.admin.data.DB;
import ru.dvs.eshop.admin.data.Site;
import ru.dvs.eshop.admin.data.network.POSTQuery;

public class Model<E> {
    public Site site;
    protected String mController;
    protected String mType;
    private String mWhere = null;
    private String mOrder = null;

    public Model() {
        mController = mType = null;
        site = Core.getInstance().site;
    }

    public Model(String controller, String type) {
        mController = controller;
        mType = type;
        site = Core.getInstance().site;
    }

    protected E newInstance(Cursor c) {
        return null;
    }

    public void getFromSite(HashMap<String, String> additional) {
        POSTQuery task = new POSTQuery(site.host, site.token) {
            @Override
            protected void onPostExecute(Void voids) {
                if (status != 0)
                    return;
                parseResponse(response);
            }
        };
        task.put("controller", mController);
        task.put("method", "get_" + mType);
        if (additional != null)
            for (Map.Entry o : additional.entrySet()) {
                task.put((String) o.getKey(), (String) o.getValue());
            }
        task.execute();
    }

    public void parseResponse(String response) {
    }

    public ArrayList<E> getFromDataBase(String table) {
        ArrayList<E> ret = new ArrayList<>();
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

    public Model<E> filter(String where) {
        mWhere = where;
        return this;
    }

    public Model<E> orderBy(String key, String order) {
        mOrder = "`" + key + "` " + order;
        return this;
    }

    public void reorderItems(ArrayList arr) {
//TODO: Make site and local updates
    }
}


