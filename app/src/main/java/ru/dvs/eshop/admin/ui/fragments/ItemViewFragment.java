package ru.dvs.eshop.admin.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.dvs.eshop.R;
import ru.dvs.eshop.admin.ui.activities.ItemActivity;

/**
 * Created by Никита on 04.09.2016.
 */
public class ItemViewFragment extends Fragment {
    public Toolbar toolbar;
    private ItemActivity mItemActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment_view = inflater.inflate(R.layout.fragment_item_view, container, false);
        toolbar = (Toolbar) fragment_view.findViewById(R.id.toolbar);
        mItemActivity.setToolbar(toolbar);
        // ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle();
        return fragment_view;
    }

    public void setItemActivity(ItemActivity itemActivity) {
        mItemActivity = itemActivity;
    }
}
