package ru.dvs.eshop.admin.ui.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.*;
import butterknife.BindView;
import butterknife.ButterKnife;
import ru.dvs.eshop.admin.R;
import ru.dvs.eshop.admin.data.components.Model;
import ru.dvs.eshop.admin.data.components.ParentableModel;
import ru.dvs.eshop.admin.data.components.eshop.Category;
import ru.dvs.eshop.admin.data.components.eshop.Vendor;
import ru.dvs.eshop.admin.ui.activities.ItemActivity;
import ru.dvs.eshop.admin.ui.adapters.SimpleModelAdapter;
import ru.dvs.eshop.admin.ui.views.floatingAction.FloatingActionButton;
import ru.dvs.eshop.admin.ui.views.recyclerViewHelpers.ItemTouchHelperCallback;
import ru.dvs.eshop.admin.utils.Callback;

import java.util.ArrayList;

public class TypeListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.save_position_button)
    FloatingActionButton mSaveButton;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    Callback.ISimple onItemMovedListener = new Callback.ISimple() {
        @Override
        public void run() {
            //mSaveButton.setVisibility(View.VISIBLE);
        }
    };
    private String type;
    private int parentId;
    private ArrayList<Model> items = new ArrayList<>();
    private SimpleModelAdapter mAdapter;


    SimpleModelAdapter.ItemViewHolder.OnItemViewHolderClickListener onItemClickListener = new SimpleModelAdapter.ItemViewHolder.OnItemViewHolderClickListener() {
        @Override
        public void onClick(SimpleModelAdapter.ItemViewHolder itemViewHolder, Model item, View v) {
            mAdapter.setItemPositionInUse(itemViewHolder.getAdapterPosition());

            if (item instanceof ParentableModel) {
                Fragment curFragment = new TypeListFragment();
                Bundle arguments = new Bundle();
                arguments.putString("type", type);
                arguments.putInt("parent_id", item.originalId);
                curFragment.setArguments(arguments);
                getActivity().getFragmentManager().
                        beginTransaction().
                        setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out).
                        setBreadCrumbTitle(item.title).
                        replace(R.id.main_frame, curFragment).
                        addToBackStack(null).
                        commit();
            } else {
                Intent intent = new Intent(getActivity().getApplicationContext(), ItemActivity.class);
                Bundle options = new Bundle();
                options.putString("type", type);
                options.putInt("id", item.originalId);
                startActivity(intent, options);
            }
        }

    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment_view = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.bind(this, fragment_view);
        type = getArguments().getString("type");
        parentId = getArguments().getInt("parent_id", -1);

        mAdapter = new SimpleModelAdapter(getActivity().getApplicationContext(), items, R.layout.row_item);
        mAdapter.setOnItemClickListener(onItemClickListener);

        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        swipeRefreshLayout.setOnRefreshListener(this);

        ItemTouchHelperCallback callback = new ItemTouchHelperCallback(mAdapter, true, false);
        callback.setOnItemMovedListener(onItemMovedListener);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        return fragment_view;
    }

    private void fillItems(final boolean isFromQuery) {
        swipeRefreshLayout.setRefreshing(true);
        Model model = null;
        Context context = getActivity().getApplicationContext();
        switch (type) {
            case "categories":
                model = new Category(context);
                break;
            case "vendors":
                model = new Vendor(context);
                break;
        }

        if (model == null) return;

        if (model instanceof ParentableModel) {
            if (parentId == -1) model.filter("level=0");
            else model.filter("parent_id=" + parentId);
        }

        items = model.getItems();
        if (!isFromQuery && items.size() == 0) onRefresh();
        if (items.size() != 0) {
            mAdapter.setItems(items);
            recyclerView.swapAdapter(mAdapter, false);
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        fillItems(false);
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
        switch (item.getItemId()) {
            case R.id.refresh_button:
                onRefresh();
                break;
            case R.id.add_button:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        Callback.ISuccessError callback = new Callback.ISuccessError() {
            @Override
            public void onSuccess() {
                fillItems(true);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onError() {
                swipeRefreshLayout.setRefreshing(false);
            }
        };
        Context context = getActivity().getApplicationContext();
        Model model = null;
        switch (type) {
            case "categories":
                model = new Category(context);
                break;
            case "vendors":
                model = new Vendor(context);
                break;
        }
        if (model != null)
            model.queryItemsFromSite(null, callback);
    }
}