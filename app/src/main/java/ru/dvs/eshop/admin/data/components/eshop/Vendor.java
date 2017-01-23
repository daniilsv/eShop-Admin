package ru.dvs.eshop.admin.data.components.eshop;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.TextView;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;
import ru.dvs.eshop.admin.R;
import ru.dvs.eshop.admin.data.components.Model;
import ru.dvs.eshop.admin.data.components.ParentableModel;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class Vendor extends ParentableModel {
    public int ordering = 0;
    public String description = null;
    public String url = null;

    public Vendor() {
        super("eshop", "vendor", "com_eshop_vendors");
    }

    public Vendor(Context context) {
        super("eshop", "vendor", "com_eshop_vendors");
        setContext(context);
        title = "";
        description = "";
        isEnabled = true;
        ordering = 0;
        url = "";
        iconHrefs = new HashMap<>();
    }

    public String toString() {
        return title;
    }

    @Override
    public HashMap getHashMap() {
        return new ObjectMapper().convertValue(new Data(this), HashMap.class);
    }

    public Model parseCursorFromDB(Cursor cursor) {
        return new Data(cursor).item;
    }

    @Override
    public void fillViewForList(View itemView) {
        ((TextView) itemView.findViewById(R.id.title)).setText(title);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Data extends Model.Data {
        @JsonIgnore
        Vendor mItem;
        @JsonIgnore
        HashMap<String, String> iconHrefs;

        @JsonProperty("original_id")
        int originalId;
        @JsonProperty("is_enabled")
        boolean isEnabled;
        int ordering;
        @JsonProperty("parent_id")
        int parentId;
        int level;
        String title;
        String description;
        String url;
        String icon;

        @JsonCreator
        public Data(@JsonProperty("id") int originalId,
                    @JsonProperty("is_enabled") String isEnabled,
                    @JsonProperty("ordering") int ordering,
                    @JsonProperty("parent_id") int parentId,
                    @JsonProperty("level") int level,
                    @JsonProperty("title") String title,
                    @JsonProperty("description") String description,
                    @JsonProperty("icon") Object icon,
                    @JsonProperty("url") String url) {
            mItem = new Vendor();
            setItemVar("originalId", originalId);
            setItemVar("isEnabled", isEnabled.equals("1"));
            setItemVar("ordering", ordering);
            setItemVar("parentId", parentId);
            setItemVar("level", level);
            setItemVar("title", title);
            setItemVar("description", description);
            setItemVar("url", url);
            if (icon instanceof LinkedHashMap) {
                mItem.iconHrefs = new HashMap<>();
                mItem.iconHrefs.put("small", (String) ((LinkedHashMap) icon).get("small"));
                mItem.iconHrefs.put("normal", (String) ((LinkedHashMap) icon).get("normal"));
                mItem.iconHrefs.put("big", (String) ((LinkedHashMap) icon).get("big"));
            } else {
                mItem.iconHrefs = new HashMap<>();
            }
            item = mItem;
        }

        Data(Vendor item) {
            mItem = item;
            setVarByItem("originalId");
            setVarByItem("isEnabled");
            setVarByItem("ordering");
            setVarByItem("parentId");
            setVarByItem("level");
            setVarByItem("title");
            setVarByItem("description");
            setVarByItem("url");
            icon = new JSONObject(mItem.iconHrefs).toString();
        }

        Data(Cursor cursor) {
            this.cursor = cursor;
            mItem = new Vendor();
            setItemVar("local_id", cursorGetInt("id"));
            setItemVar("originalId", cursorGetInt("original_id"));
            setItemVar("isEnabled", cursorGetInt("is_enabled") == 1);
            setItemVar("ordering", cursorGetInt("ordering"));
            setItemVar("parentId", cursorGetInt("parent_id"));
            setItemVar("level", cursorGetInt("level"));
            setItemVar("title", cursorGetString("title"));
            setItemVar("description", cursorGetString("description"));
            setItemVar("url", cursorGetString("url"));
            mItem.iconHrefs = new HashMap<>();
            try {
                JSONObject icon = new JSONObject(cursorGetString("icon"));
                mItem.iconHrefs.put("small", icon.getString("small"));
                mItem.iconHrefs.put("normal", icon.getString("normal"));
                mItem.iconHrefs.put("big", icon.getString("big"));
            } catch (JSONException ignored) {
            }
            item = mItem;
        }

        public String getIs_enabled() {
            return isEnabled ? "1" : "0";
        }

        protected void setVarByItem(String name) {
            try {
                Data.class.getField(name).set(this, mItem.getClass().getField(name).get(mItem));
            } catch (Exception ignored) {
            }
        }

        protected void setItemVar(String name, Object value) {
            try {
                mItem.getClass().getField(name).set(mItem, value);
            } catch (Exception ignored) {
            }
        }
    }
}