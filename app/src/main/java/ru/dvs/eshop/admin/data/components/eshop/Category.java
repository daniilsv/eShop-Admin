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
import ru.dvs.eshop.admin.R;
import ru.dvs.eshop.admin.data.components.Model;
import ru.dvs.eshop.admin.data.components.ParentableModel;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class Category extends ParentableModel {
    public int ordering = 0;
    public String description = null;
    public String url = null;
    public String metaKeys = null;
    public String metaDesc = null;
    public String tpl = null;
    private HashMap<String, String> iconHrefs = null;

    private Category() {
        super("eshop", "category", "com_eshop_categories");
    }

    public Category(Context context) {
        super("eshop", "category", "com_eshop_categories");
        setContext(context);
        title = "";
        description = "";
        isEnabled = true;
        ordering = 0;
        url = "";
        metaKeys = "";
        metaDesc = "";
        tpl = "";
        iconHrefs = new HashMap<>();
    }

    public String toString() {
        return title;
    }

    @Override
    public HashMap getHashMap() {
        HashMap<String, Object> ret = new ObjectMapper().convertValue(new Data(this), HashMap.class);
        ret.remove("title_");
        return ret;
    }

    @Override
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
        public Category mItem;

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
        @JsonProperty("meta_keys")
        String metaKeys;
        @JsonProperty("meta_desc")
        String metaDesc;
        String tpl;
        String url;
        @JsonProperty("icon")
        HashMap<String, String> iconHrefs;

        @JsonCreator
        public Data(@JsonProperty("id") int originalId,
                    @JsonProperty("is_enabled") String isEnabled,
                    @JsonProperty("ordering") int ordering,
                    @JsonProperty("parent_id") int parentId,
                    @JsonProperty("level") int level,
                    @JsonProperty("title") String title,
                    @JsonProperty("description") String description,
                    @JsonProperty("meta_keys") String metaKeys,
                    @JsonProperty("meta_desc") String metaDesc,
                    @JsonProperty("tpl") String tpl,
                    @JsonProperty("icon") Object icon,
                    @JsonProperty("url") String url) {
            mItem = new Category();
            setItemVar("originalId", originalId);
            setItemVar("isEnabled", isEnabled.equals("1"));
            setItemVar("ordering", ordering);
            setItemVar("parentId", parentId);
            setItemVar("level", level);
            setItemVar("metaKeys", metaKeys);
            setItemVar("metaDesc", metaDesc);
            setItemVar("tpl", tpl);
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

        Data(Category item) {
            mItem = item;
            setVarByItem("originalId");
            setVarByItem("isEnabled");
            setVarByItem("ordering");
            setVarByItem("parentId");
            setVarByItem("level");
            setVarByItem("metaKeys");
            setVarByItem("metaDesc");
            setVarByItem("tpl");
            setVarByItem("title");
            setVarByItem("description");
            setVarByItem("url");
            setVarByItem("iconHrefs");
        }

        Data(Cursor cursor) {
            this.cursor = cursor;
            mItem = new Category();
            setItemVar("local_id", cursorGetInt("id"));
            setItemVar("originalId", cursorGetInt("original_id"));
            setItemVar("isEnabled", cursorGetInt("is_enabled") == 1);
            setItemVar("ordering", cursorGetInt("ordering"));
            setItemVar("parentId", cursorGetInt("parent_id"));
            setItemVar("level", cursorGetInt("level"));
            setItemVar("metaKeys", cursorGetString("meta_keys"));
            setItemVar("metaDesc", cursorGetString("meta_desc"));
            setItemVar("tpl", cursorGetString("tpl"));
            setItemVar("title", cursorGetString("title"));
            setItemVar("description", cursorGetString("description"));
            setItemVar("url", cursorGetString("url"));
            item = mItem;
        }

        public String getIs_enabled() {
            return isEnabled ? "1" : "0";
        }

        public String getMeta_keys() {
            return metaKeys;
        }

        public String getMeta_desc() {
            return metaDesc;
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