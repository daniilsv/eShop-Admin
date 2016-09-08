package ru.dvs.eshop.admin.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ru.dvs.eshop.R;
import ru.dvs.eshop.admin.data.components.eshop.Vendor;
import ru.dvs.eshop.admin.ui.activities.ItemActivity;
import ru.dvs.eshop.admin.ui.adapters.VendorsAdapter;

/**
 * Created by Никита on 04.09.2016.
 */
public class ItemViewFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment_view = inflater.inflate(R.layout.fragment_item_view, container, false);

        Toolbar toolbar = (Toolbar) fragment_view.findViewById(R.id.toolbar);
        ((ItemActivity) getActivity()).setSupportActionBar(toolbar);
        ((CollapsingToolbarLayout) fragment_view.findViewById(R.id.collapsing_toolbar)).setTitle("Текст тулбара");


        ArrayList<Vendor> vendors = Vendor.getVendors();
        final VendorsAdapter adapter = new VendorsAdapter(getActivity(), vendors, null);

        RecyclerView recyclerView = (RecyclerView) fragment_view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        return fragment_view;
    }

}
