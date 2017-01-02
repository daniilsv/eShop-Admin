package ru.dvs.eshop.admin.ui.fragments;

import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.HashMap;

import ru.dvs.eshop.R;
import ru.dvs.eshop.admin.Core;
import ru.dvs.eshop.admin.data.components.Model;
import ru.dvs.eshop.admin.data.components.eshop.Vendor;
import ru.dvs.eshop.admin.ui.activities.ItemActivity;
import ru.dvs.eshop.admin.utils.Function;

public class ItemEditFragment extends Fragment {
    private final int IDD_THREE_BUTTONS = 0;
    Toolbar toolbar;
    ViewGroup insertPointView;
    View fragment_view;
    Model item;
    AlertDialog.Builder ad;
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
        ad = new AlertDialog.Builder(getActivity().getBaseContext());
        ad.setTitle("titile");  // заголовок
        ad.setMessage("message"); // сообщение
        ad.setNegativeButton("Button", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                Toast.makeText(getActivity().getBaseContext(), "Возможно вы правы", Toast.LENGTH_LONG)
                        .show();
            }
        });
        ad.setCancelable(true);
        ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                Toast.makeText(getActivity().getBaseContext(), "Вы ничего не выбрали",
                        Toast.LENGTH_LONG).show();
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
                            item.uploadIcon();
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
                            item.uploadIcon();
                            Core.makeToast("Updated", false);
                            ObjectAnimator.ofFloat(fragment_view, "alpha", 1, 0).
                                    setDuration(500).
                                    start();
                            getActivity().onBackPressed();
                        }
                    });

            }
        });

//        Button addOptions = (Button) fragment_view.findViewById(R.id.button_add_element_payment_type);
//        addOptions.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ad.show();
//            }
//        });
        return fragment_view;
    }


    public void onActivityImageResult(String imageUri) {
        item.setImageByActivity(imageUri);
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
        getActivity().findViewById(R.id.item_floating_button).setVisibility(View.GONE);
        getActivity().findViewById(R.id.item_edit_button).setVisibility(View.GONE);
        getActivity().findViewById(R.id.item_delete_button).setVisibility(View.GONE);
        getActivity().findViewById(R.id.item_set_invisible_button).setVisibility(View.GONE);
    }
}
