package ru.dvs.eshop.admin.ui.fragments;

import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.HashMap;

import ru.dvs.eshop.R;
import ru.dvs.eshop.admin.Core;
import ru.dvs.eshop.admin.data.components.Model;
import ru.dvs.eshop.admin.data.components.eshop.Vendor;
import ru.dvs.eshop.admin.ui.activities.ItemActivity;
import ru.dvs.eshop.admin.utils.Function;

public class ItemEditFragment extends Fragment {
    Toolbar toolbar;
    ViewGroup insertPointView;
    View fragment_view;
    Model item;
    private boolean mIsAdding = false;
    private ItemViewFragment mitemViewFragment;

    public void fillData() {
        String type = getArguments().getString("item_type", "-1");
        int itemId = getArguments().getInt("item_id", -1);
        switch (type) {
            case "vendor":
                item = new Vendor().getItemById(itemId);
                toolbar.setTitle(((Vendor) item).title);
                item.fillViewForEditItem(insertPointView);
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragment_view = inflater.inflate(R.layout.fragment_item_edit, container, false);

        toolbar = (Toolbar) fragment_view.findViewById(R.id.toolbar);
        ((ItemActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectAnimator.ofFloat(fragment_view, "alpha", 1, 0).
                        setDuration(500).
                        start();
                if (mIsAdding) {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("original_id", -1 + "");
                    item.parseResponseEdit(null, map);
                    getActivity().finish();
                }
                getActivity().onBackPressed();
            }
        });
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

        insertPointView = (ViewGroup) fragment_view.findViewById(R.id.item_frame);

        fillData();
        Button editBut = (Button) fragment_view.findViewById(R.id.button_save);
        if (mIsAdding)
            editBut.setText("INSERT");
        editBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);
                HashMap map = item.parseEditItem(insertPointView);
                if (mIsAdding)
                    item.addToSite(map, new Function() {
                        @Override
                        public void run() {
                            Core.makeToast("Inserted", false);
                            ObjectAnimator.ofFloat(fragment_view, "alpha", 1, 0).
                                    setDuration(500).
                                    start();
                            getActivity().onBackPressed();
                        }
                    });
                else
                    item.editOnSite(map, new Function() {
                        @Override
                        public void run() {
                            Core.makeToast("Updated", false);
                            ObjectAnimator.ofFloat(fragment_view, "alpha", 1, 0).
                                    setDuration(500).
                                    start();
                            getActivity().onBackPressed();
                        }
                    });

            }
        });
        return fragment_view;
    }

    public void setIsAdding(boolean isAdding) {
        mIsAdding = isAdding;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        ObjectAnimator.ofFloat(fragment_view, "alpha", 0, 1).
                setDuration(500).
                start();
    }
}
