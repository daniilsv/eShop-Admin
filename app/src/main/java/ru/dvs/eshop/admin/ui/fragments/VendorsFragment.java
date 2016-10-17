package ru.dvs.eshop.admin.ui.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ru.dvs.eshop.R;
import ru.dvs.eshop.admin.data.components.Model;
import ru.dvs.eshop.admin.data.components.eshop.Vendor;
import ru.dvs.eshop.admin.ui.activities.ItemActivity;
import ru.dvs.eshop.admin.ui.adapters.ModelAdapter;
import ru.dvs.eshop.admin.ui.views.recyclerViewHelpers.SimpleItemTouchHelperCallback;
import ru.dvs.eshop.admin.utils.Function;

public class VendorsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    SwipeRefreshLayout mSwipeRefreshLayout;
    ModelAdapter adapter;
    RecyclerView recyclerView;
    ArrayList<Model> vendors;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment_view = inflater.inflate(R.layout.fragment_vendors, container, false);

        vendors = new Vendor().getItems();

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

        return fragment_view;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.updateItemInUse();
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
        vendors = new Vendor().getItems();
        adapter.setItems(vendors);
        recyclerView.swapAdapter(adapter, false);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.over_items, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getTitle().toString()) {
            case "+":
                addVendorItem();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addVendorItem() {
        Vendor newV = new Vendor();
        newV.ordering = vendors.size() + 1;
        newV.addToDB();
        vendors.add(newV);
        adapter.setItems(vendors);
        recyclerView.swapAdapter(adapter, false);
        mSwipeRefreshLayout.setRefreshing(false);

        adapter.setItemPositionInUse(newV.ordering - 1);
        Intent intent = new Intent(getActivity(), ItemActivity.class);
        intent.putExtra("item_type", newV.type);
        intent.putExtra("item_id", newV.id);
        intent.putExtra("is_adding", true);
        startActivity(intent);
    }

}