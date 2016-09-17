package ru.dvs.eshop.admin.ui.fragments;

import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import ru.dvs.eshop.R;
import ru.dvs.eshop.admin.Core;
import ru.dvs.eshop.admin.data.components.Model;
import ru.dvs.eshop.admin.data.components.eshop.Vendor;
import ru.dvs.eshop.admin.ui.activities.ItemActivity;
import ru.dvs.eshop.admin.utils.Function;

public class ItemViewFragment extends Fragment {
    CollapsingToolbarLayout collapsingToolbar;
    ImageView flexibleImage;
    ViewGroup insertPointView;
    FloatingActionButton editFab;
    View fragment_view;
    Model item;

    public void fillData() {
        String type = getArguments().getString("item_type", "-1");
        int itemId = getArguments().getInt("item_id", -1);
        switch (type) {
            case "vendor":
                item = Vendor.getVendorById(itemId);
                collapsingToolbar.setTitle(((Vendor) item).title);
                flexibleImage.setImageDrawable(((Vendor) item).icons.get("big"));
                item.fillViewForReadItem(insertPointView);
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragment_view = inflater.inflate(R.layout.fragment_item_view, container, false);

        Toolbar toolbar = (Toolbar) fragment_view.findViewById(R.id.toolbar);
        ((ItemActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectAnimator.ofFloat(fragment_view, "alpha", 1, 0).
                        setDuration(500).
                        start();
                getActivity().onBackPressed();
            }
        });
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

        collapsingToolbar = (CollapsingToolbarLayout) fragment_view.findViewById(R.id.collapsing_toolbar);
        flexibleImage = (ImageView) fragment_view.findViewById(R.id.flexible_image);
        insertPointView = (ViewGroup) fragment_view.findViewById(R.id.item_frame);

        fillData();
/*
        editFabButton = new FloatingActionButton.Builder(getActivity())
                .withDrawable(getResources().getDrawable(R.drawable.ic_menu_slideshow))
                .withButtonColor(Color.BLACK)
                .withGravity(Gravity.BOTTOM | Gravity.START)
                .withMargins(0, 0, 16, 16)
                .create();

        editFabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItemEditFragment itemEditFragment = new ItemEditFragment();
                itemEditFragment.setArguments(getArguments());
                ((ItemActivity) getActivity()).placeFragment(itemEditFragment, true);
            }
        });
*/

        editFab = (FloatingActionButton) fragment_view.findViewById(R.id.fab_edit);
        editFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItemEditFragment itemEditFragment = new ItemEditFragment();
                itemEditFragment.setArguments(getArguments());
                ((ItemActivity) getActivity()).placeFragment(itemEditFragment, true);
            }
        });

        Button setEnabled = (Button) fragment_view.findViewById(R.id.is_visible);
        setEnabled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setFieldOnSite("is_enabled", (!item.is_enabled) ? "1" : "0", new Function() {
                    @Override
                    public void run() {
                        Core.makeToast("Changed visible", false);
                        item.is_enabled = !item.is_enabled;
                    }
                });
            }
        });

        return fragment_view;
    }

    @Override
    public void onPause() {
        super.onPause();
        editFab.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        editFab.setVisibility(View.VISIBLE);
        ObjectAnimator.ofFloat(fragment_view, "alpha", 0, 1).
                setDuration(500).
                start();
    }
}
