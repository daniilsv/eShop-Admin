package ru.dvs.eshop.admin.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import ru.dvs.eshop.R;
import ru.dvs.eshop.admin.data.components.eshop.Vendor;
import ru.dvs.eshop.admin.ui.activities.ItemActivity;

public class ItemViewFragment extends Fragment {
    CollapsingToolbarLayout collapsingToolbar;
    ImageView flexibleImage;
    ViewGroup insertPointView;

    public void fillData() {
        String type = getArguments().getString("item_type", "-1");
        int itemId = getArguments().getInt("item_id", -1);
        switch (type) {
            case "vendor":
                Vendor item = Vendor.getVendorById(itemId);
                collapsingToolbar.setTitle(item.title);
                flexibleImage.setImageDrawable(item.icons.get("big"));
                item.fillViewForReadItem(insertPointView);
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment_view = inflater.inflate(R.layout.fragment_item_view, container, false);

        Toolbar toolbar = (Toolbar) fragment_view.findViewById(R.id.toolbar);
        ((ItemActivity) getActivity()).setSupportActionBar(toolbar);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

        collapsingToolbar = (CollapsingToolbarLayout) fragment_view.findViewById(R.id.collapsing_toolbar);
        flexibleImage = (ImageView) fragment_view.findViewById(R.id.flexible_image);
        insertPointView = (ViewGroup) fragment_view.findViewById(R.id.item_frame);

        fillData();

        return fragment_view;
    }

}
