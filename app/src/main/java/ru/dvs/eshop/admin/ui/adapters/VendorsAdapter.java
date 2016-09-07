package ru.dvs.eshop.admin.ui.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import ru.dvs.eshop.R;
import ru.dvs.eshop.admin.data.components.eshop.Vendor;
import ru.dvs.eshop.admin.ui.activities.ItemActivity;
import ru.dvs.eshop.admin.ui.views.recyclerViewHelpers.ItemTouchHelperAdapter;
import ru.dvs.eshop.admin.ui.views.recyclerViewHelpers.ItemTouchHelperViewHolder;
import ru.dvs.eshop.admin.ui.views.recyclerViewHelpers.OnStartDragListener;

public class VendorsAdapter extends RecyclerView.Adapter<VendorsAdapter.ItemViewHolder>
        implements ItemTouchHelperAdapter {
    private final Activity mActivity;
    private final OnStartDragListener mDragStartListener;
    private ArrayList<Vendor> mItems = new ArrayList<>();

    public VendorsAdapter(Activity activity, ArrayList<Vendor> items, OnStartDragListener dragStartListener) {
        mActivity = activity;
        mDragStartListener = dragStartListener;
        mItems = items;
    }

    public ArrayList<Vendor> getItems() {
        return mItems;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_vendor, parent, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(mActivity, view);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {
        holder.textView.setText(mItems.get(position).title);
        holder.handleView.setImageDrawable(mItems.get(position).icons.get("small"));

        // Start a drag whenever the handle view it touched
        holder.handleView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(holder);
                }
                return false;
            }
        });
    }


    @Override
    public void onItemDismiss(int position) {
        mItems.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mItems, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder, View.OnClickListener {
        public final TextView textView;
        public final ImageView handleView;
        private final Activity mActivity;

        public ItemViewHolder(Activity activity, View itemView) {
            super(itemView);
            mActivity = activity;
            textView = (TextView) itemView.findViewById(R.id.title);
            handleView = (ImageView) itemView.findViewById(R.id.image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }

        @Override
        public void onClick(View v) {
            mActivity.startActivity(new Intent(mActivity, ItemActivity.class));
        }
    }
}

