package ru.dvs.eshop.admin.ui.views.fields;

import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.CardView;
import android.view.View;

import ru.dvs.eshop.admin.ui.views.FormField;

public final class StringField extends FormField {

    protected StringField(String title) {
        super(title);
        this.type = "string";
    }

    @Override
    public View getView(Context context) {
        CardView cv = new CardView(context);
        TextInputLayout child = new TextInputLayout(context);
        child.setHint(title);
        field = new TextInputEditText(context);
        child.addView(field);
        cv.addView(child);
        return cv;
    }

    @Override
    public Object getData() {
        return ((TextInputEditText) field).getText().toString();
    }
}
