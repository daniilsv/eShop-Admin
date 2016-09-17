package ru.dvs.eshop.admin.ui.fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ru.dvs.eshop.R;
import ru.dvs.eshop.admin.data.components.Model;
import ru.dvs.eshop.admin.data.components.eshop.Vendor;
import ru.dvs.eshop.admin.ui.adapters.ModelAdapter;
import ru.dvs.eshop.admin.ui.views.FloatingActionButton;
import ru.dvs.eshop.admin.ui.views.recyclerViewHelpers.SimpleItemTouchHelperCallback;
import ru.dvs.eshop.admin.utils.Function;

public class VendorsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    SwipeRefreshLayout mSwipeRefreshLayout;
    ModelAdapter adapter;
    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment_view = inflater.inflate(R.layout.fragment_vendors, container, false);


        ArrayList<Model> vendors = Vendor.getVendors();

        adapter = new ModelAdapter(getActivity(), vendors, R.layout.row_vendor);

        recyclerView = (RecyclerView) fragment_view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mSwipeRefreshLayout = (SwipeRefreshLayout) fragment_view.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter, true, false);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        FloatingActionButton fabButton = new FloatingActionButton.Builder(getActivity())
                .withDrawable(getResources().getDrawable(R.drawable.ic_menu_send))
                .withButtonColor(Color.MAGENTA)
                .withGravity(Gravity.BOTTOM | Gravity.END)
                .withMargins(0, 0, 16, 16)
                .create();
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Model> arr = adapter.getItems();
                new Vendor().reorderItems(arr, null);
            }
        });

        return fragment_view;
    }


    @Override
    public void onRefresh() {
        new Vendor().getFromSite(null, new Function() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onItemsLoadComplete();
                    }
                });
            }
        });
    }

    void onItemsLoadComplete() {
        ArrayList<Model> vendors = Vendor.getVendors();
        adapter.setItems(vendors);
        recyclerView.swapAdapter(adapter, false);
        mSwipeRefreshLayout.setRefreshing(false);
    }

}
