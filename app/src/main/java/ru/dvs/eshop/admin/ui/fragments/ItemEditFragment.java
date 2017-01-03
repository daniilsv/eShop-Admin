package ru.dvs.eshop.admin.ui.fragments;

import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import ru.dvs.eshop.R;
import ru.dvs.eshop.admin.Core;
import ru.dvs.eshop.admin.data.components.Model;
import ru.dvs.eshop.admin.data.components.eshop.Vendor;
import ru.dvs.eshop.admin.ui.activities.ItemActivity;
import ru.dvs.eshop.admin.utils.Function;

import java.util.HashMap;

public class ItemEditFragment extends Fragment {
    Toolbar toolbar;
    ViewGroup insertPointView;
    View fragment_view;
    Model item;
    private boolean mSaveEnabled = false;
    private boolean mIsAdding = false;
    private Button editBut;

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
        editBut = (Button) fragment_view.findViewById(R.id.button_save);
        setButtonState(true, !mIsAdding);
        EditText edText = (EditText) fragment_view.findViewById(R.id.edit_vendor_title);
        edText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mSaveEnabled = s.length() != 0;
                setButtonState(mSaveEnabled, mSaveEnabled);
            }
        });
        if (mIsAdding)
            editBut.setText("INSERT");
        editBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSaveEnabled) {
                    setButtonState(false, false);
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
                        }, new Function() {
                            @Override
                            public void run() {
                                setButtonState(true, mSaveEnabled);
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
                        }, new Function() {
                            @Override
                            public void run() {
                                setButtonState(true, mSaveEnabled);
                            }
                        });
                } else {
                    Core.makeToast(getResources().getString(R.string.hint_button_title_edit_vendor), false);
                }
            }
        });

        return fragment_view;
    }

    private void setButtonState(boolean isEnabled, boolean isActive) {
        editBut.setEnabled(isEnabled);
        if (isActive)
            editBut.setTextColor(ContextCompat.getColor(getActivity(), R.color.pink));
        else
            editBut.setTextColor(ContextCompat.getColor(getActivity(), R.color.cardview_dark_background));
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
