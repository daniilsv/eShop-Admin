package ru.dvs.eshop.admin.data.components;


import java.util.HashMap;

import ru.dvs.eshop.admin.Core;
import ru.dvs.eshop.admin.data.Site;

public class Model {
    public Site site;

    public Model() {
        site = Core.getInstance().site;
    }

    public HashMap<String, String> get() {
        HashMap<String, String> ret = new HashMap<>();

        return ret;
    }
}
