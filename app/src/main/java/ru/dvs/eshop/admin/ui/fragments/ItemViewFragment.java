package ru.dvs.eshop.admin.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.dvs.eshop.R;

/**
 * Created by Никита on 04.09.2016.
 */
public class ItemViewFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment_view = inflater.inflate(R.layout.fragment_item_view, container, false);
        return fragment_view;
    }

}
