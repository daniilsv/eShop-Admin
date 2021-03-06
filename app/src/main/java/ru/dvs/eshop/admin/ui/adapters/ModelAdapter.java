package ru.dvs.eshop.admin.ui.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ru.dvs.eshop.R;
import ru.dvs.eshop.admin.data.components.Model;
import ru.dvs.eshop.admin.ui.activities.ItemActivity;
import ru.dvs.eshop.admin.ui.views.recyclerViewHelpers.ItemTouchHelperAdapter;
import ru.dvs.eshop.admin.ui.views.recyclerViewHelpers.ItemTouchHelperViewHolder;

import java.util.ArrayList;
import java.util.Collections;

public class ModelAdapter extends RecyclerView.Adapter<ModelAdapter.ItemViewHolder>
        implements ItemTouchHelperAdapter {
    private static int mItemPositionInUse = -1;
    private final Activity mActivity;
    private ArrayList<Model> mItems = new ArrayList<>();
    private int mRowResId;

    public ModelAdapter(Activity activity, ArrayList<Model> items, int rowResId) {
        mActivity = activity;
        mItems = items;
        mRowResId = rowResId;
    }

    public boolean updateItemInUse() {
        if (mItemPositionInUse != -1) {
            Model item = mItems.get(mItemPositionInUse).refresh();
            if (item.original_id == -1) {
                item.deleteFromDB();
                mItems.remove(mItemPositionInUse);
                notifyItemRemoved(mItemPositionInUse);
                return true;
            }
            mItems.set(mItemPositionInUse, item);
            notifyItemChanged(mItemPositionInUse);
            mItemPositionInUse = -1;
            return true;
        }
        return false;
    }

    public ArrayList<Model> getItems() {
        return mItems;
    }

    public void setItems(ArrayList<Model> items) {
        mItems = items;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(mRowResId, parent, false);
        return new ItemViewHolder(mActivity, view);
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

    public void setItemPositionInUse(int itemPositionInUse) {
        mItemPositionInUse = itemPositionInUse;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder, View.OnClickListener {
        private final Activity mActivity;
        private Model mItem;

        ItemViewHolder(Activity activity, View itemView) {
            super(itemView);
            mActivity = activity;
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
            mItemPositionInUse = getAdapterPosition();
            Intent intent = new Intent(mActivity, ItemActivity.class);
            intent.putExtra("item_type", mItem.type);
            intent.putExtra("item_id", mItem.id);
            mActivity.startActivity(intent);
        }

        public void setItem(Model item) {
            mItem = item;
            itemView.setOnClickListener(this);
            mItem.fillViewForListItem(itemView);
        }
    }
}
