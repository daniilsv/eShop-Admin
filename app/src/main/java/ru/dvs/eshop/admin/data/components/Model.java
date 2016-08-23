package ru.dvs.eshop.admin.data.components;

import ru.dvs.eshop.admin.Core;
import ru.dvs.eshop.admin.data.Site;

public class Model {
    public Site site;

    public Model() {
        site = Core.getInstance().site;
    }

}
