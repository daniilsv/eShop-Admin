package ru.dvs.eshop.admin.ui.fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ru.dvs.eshop.R;
import ru.dvs.eshop.admin.data.components.eshop.Vendor;
import ru.dvs.eshop.admin.ui.adapters.VendorsAdapter;
import ru.dvs.eshop.admin.ui.views.FloatingActionButton;
import ru.dvs.eshop.admin.ui.views.recyclerViewHelpers.SimpleItemTouchHelperCallback;

public class VendorsFragment extends Fragment {

    private ItemTouchHelper mItemTouchHelper;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment_view = inflater.inflate(R.layout.fragment_vendors, container, false);


        ArrayList<Vendor> vendors = Vendor.getVendors();

        final VendorsAdapter adapter = new VendorsAdapter(getActivity(), vendors, null);

        RecyclerView recyclerView = (RecyclerView) fragment_view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter, true, false);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);

        FloatingActionButton fabButton = new FloatingActionButton.Builder(getActivity())
                .withDrawable(getResources().getDrawable(R.drawable.ic_menu_send))
                .withButtonColor(Color.MAGENTA)
                .withGravity(Gravity.BOTTOM | Gravity.END)
                .withMargins(0, 0, 16, 16)
                .create();
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Vendor> arr = adapter.getItems();
                new Vendor().reorderItems(arr);
            }
        });

        return fragment_view;
    }


}
