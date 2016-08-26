package ru.dvs.eshop.admin.ui.fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ru.dvs.eshop.R;
import ru.dvs.eshop.admin.data.components.eshop.Vendor;
import ru.dvs.eshop.admin.ui.views.FloatingActionButton;
import ru.dvs.eshop.admin.ui.views.draggableListView.DraggableListView;
import ru.dvs.eshop.admin.ui.views.draggableListView.StableArrayAdapter;

public class VendorsFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment_view = inflater.inflate(R.layout.fragment_vendors, container, false);


        ArrayList<Vendor> vendors = Vendor.getVendors();

        final StableArrayAdapter adapter = new StableArrayAdapter<Vendor>(getActivity(), R.layout.row_vendor, vendors) {
            @Override
            public View getView(Vendor item, View view) {
                ((TextView) view.findViewById(R.id.title)).setText(item.title);
                ((ImageView) view.findViewById(R.id.image)).setImageDrawable(item.icons.get("small"));
                return view;
            }
        };
        final DraggableListView listView = (DraggableListView) fragment_view.findViewById(R.id.listview);
        listView.setElementsList(vendors);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Vendor item = (Vendor) adapter.getItem(position);
                Toast.makeText(getActivity(), position + " " + item.title, Toast.LENGTH_SHORT).show();
            }
        });

        FloatingActionButton fabButton = new FloatingActionButton.Builder(getActivity())
                .withDrawable(getResources().getDrawable(R.drawable.ic_menu_send))
                .withButtonColor(Color.MAGENTA)
                .withGravity(Gravity.BOTTOM | Gravity.END)
                .withMargins(0, 0, 16, 16)
                .create();
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList arr = listView.getElementsList();
                Log.d("GOOD", arr.toString());
            }
        });

        return fragment_view;
    }
}
