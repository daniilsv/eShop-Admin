package ru.dvs.eshop.admin.ui.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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
        holder.setItem(mItems.get(position));
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
        private final TextView mTextView;
        private final ImageView mHandleView;
        private final Activity mActivity;
        private Vendor mItem;

        public ItemViewHolder(Activity activity, View itemView) {
            super(itemView);
            mActivity = activity;
            mTextView = (TextView) itemView.findViewById(R.id.title);
            mHandleView = (ImageView) itemView.findViewById(R.id.image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onItemSelected() {
            itemView.findViewById(R.id.holder).setBackgroundColor(Color.argb(255, 230, 230, 230));
        }

        @Override
        public void onItemClear() {
            itemView.findViewById(R.id.holder).setBackgroundColor(0);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mActivity, ItemActivity.class);
            intent.putExtra("item_type", "vendor");
            intent.putExtra("item_id", mItem.id);
            mActivity.startActivity(intent);
        }

        public void setItem(Vendor item) {
            mItem = item;
            mTextView.setText(item.title);
            mHandleView.setImageDrawable(item.icons.get("small"));
        }
    }
}
