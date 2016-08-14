package ru.dvs.eshop.admin.data.components.eshop;

import java.util.HashMap;

/**
 * Производитель
 */
public class Vendor {
    int original_id;
    int id;
    boolean is_enabled;
    String title;
    HashMap<String, String> icon; //original, big, small
    String description;
    int ordering;

    public void loadFromSite() {
        /*
        POSTQuery task = new POSTQuery(/*SITE*//*);
        task.put("controller", "api");
        task.put("method", "ping");
        task.execute();
        */
        //#OR
        /*
        POSTQuery task = new POSTQuery(/*SITE*//*) {
                @Override
                protected void onPostExecute(Void voids) {
                    //here you have access to (int status) and (String response)
                }
            };
        task.put("controller", "api");
        task.put("method", "ping");
        task.execute();
        */
    }

}
