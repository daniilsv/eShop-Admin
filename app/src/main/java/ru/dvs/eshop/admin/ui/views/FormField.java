package ru.dvs.eshop.admin.ui.views;


import android.content.Context;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by MSI1 on 23.01.2017.
 */


public abstract class FormField {
    public String type;
    public boolean haveValues = false;
    protected String title;
    protected ArrayList values;
    protected View field;

    protected FormField(String title) {
        this.title = title;
    }

    public abstract View getView(Context context);

    public abstract Object getData();

    public void setValues() {

    }
}


//NOTE: View view = new StringField(id, title).getView();

