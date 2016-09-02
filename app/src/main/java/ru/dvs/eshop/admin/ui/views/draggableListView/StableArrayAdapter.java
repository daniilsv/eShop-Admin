package ru.dvs.eshop.admin.ui.views.draggableListView;

/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.HashMap;
import java.util.List;

import ru.dvs.eshop.R;

public class StableArrayAdapter<T> extends ArrayAdapter<T> {

    final int INVALID_ID = -1;
    protected LayoutInflater lInflater;
    private HashMap<T, Integer> mIdMap = new HashMap<>();

    public StableArrayAdapter(Context context, int textViewResourceId, List<T> objects) {
        super(context, textViewResourceId, objects);
        for (int i = 0; i < objects.size(); ++i) {
            mIdMap.put(objects.get(i), i);
        }
        lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public long getItemId(int position) {
        if (position < 0 || position >= mIdMap.size()) {
            return INVALID_ID;
        }
        T item = getItem(position);
        return mIdMap.get(item);
    }

    protected View getView(T item, View view) {
        return view;
    }

    @Override
    public View getView(int position, View viewF, ViewGroup parent) {
        View view = lInflater.inflate(R.layout.row_vendor, parent, false);
        T item = getItem(position);
        view = getView(item, view);
        return view;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
