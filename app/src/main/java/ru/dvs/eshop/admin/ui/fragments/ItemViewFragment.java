package ru.dvs.eshop.admin.ui.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ru.dvs.eshop.R;
import ru.dvs.eshop.admin.data.components.eshop.Vendor;
import ru.dvs.eshop.admin.ui.activities.ItemActivity;

public class ItemViewFragment extends Fragment {
    String mType;
    int mItemId;
    CollapsingToolbarLayout collapsing_toolbar;
    ImageView flexible_image;

    public void extractArguments() {
        mType = getArguments().getString("item_type", "-1");
        mItemId = getArguments().getInt("item_id", -1);
    }

    public void fillData(View view) {
        switch (mType) {
            case "vendor":
                Vendor item = Vendor.getVendorById(mItemId);
                collapsing_toolbar.setTitle(item.title);
                flexible_image.setImageDrawable(item.icons.get("big"));
                ViewGroup insertPoint = (ViewGroup) view.findViewById(R.id.item_frame);

                LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                for (int i = 0; i < 15; i++) {
                    View v = vi.inflate(R.layout.row_vendor, null);
                    ((TextView) v.findViewById(R.id.title)).setText(item.title);
                    ((ImageView) v.findViewById(R.id.image)).setImageDrawable(item.icons.get("small"));
                    insertPoint.addView(v, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                }
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        extractArguments();
        View fragment_view = inflater.inflate(R.layout.fragment_item_view, container, false);

        Toolbar toolbar = (Toolbar) fragment_view.findViewById(R.id.toolbar);
        ((ItemActivity) getActivity()).setSupportActionBar(toolbar);
        collapsing_toolbar = (CollapsingToolbarLayout) fragment_view.findViewById(R.id.collapsing_toolbar);
        flexible_image = (ImageView) fragment_view.findViewById(R.id.flexible_image);
        fillData(fragment_view);
        return fragment_view;
    }

}
