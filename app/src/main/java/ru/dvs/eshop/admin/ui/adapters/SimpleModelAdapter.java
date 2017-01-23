package ru.dvs.eshop.admin.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ru.dvs.eshop.admin.R;
import ru.dvs.eshop.admin.data.components.Model;
import ru.dvs.eshop.admin.ui.views.recyclerViewHelpers.ItemTouchHelperAdapter;
import ru.dvs.eshop.admin.ui.views.recyclerViewHelpers.ItemTouchHelperViewHolder;

import java.util.ArrayList;
import java.util.Collections;

public class SimpleModelAdapter extends RecyclerView.Adapter<SimpleModelAdapter.ItemViewHolder>
        implements ItemTouchHelperAdapter {
    private Context mContext;
    private ItemViewHolder.OnItemViewHolderClickListener mOnItemClickListener = null;
    private int mItemPositionInUse = -1;
    private ArrayList<Model> mItems = new ArrayList<>();
    private int mRowResId;

    public SimpleModelAdapter(Context context, ArrayList<Model> items, int rowResId) {
        mContext = context;
        mItems = items;
        mRowResId = rowResId;
    }

    public boolean updateItemInUse() {
        if (mItemPositionInUse != -1) {
            Model item = mItems.get(mItemPositionInUse);
            item = item.getItemById(item.localId);
            if (item.originalId == -1) {
                item.deleteItem(item.localId);
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
        return new ItemViewHolder(mContext, view, mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {
        holder.setItem(mItems.get(position));
    }

    @Override
    public void onViewRecycled(ItemViewHolder holder) {
        holder.stopProcesses();
    }

    @Override
    public void onItemSwiped(int position) {
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

    public void setOnItemClickListener(ItemViewHolder.OnItemViewHolderClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder, View.OnClickListener {
        private Model mItem;
        private Context mContext;
        private OnItemViewHolderClickListener mOnItemClickListener = null;

        private Thread loadIconsThread;
        private Thread loadContentThread;

        ItemViewHolder(Context context, View itemView, OnItemViewHolderClickListener onItemClickListener) {
            super(itemView);
            mContext = context;
            mOnItemClickListener = onItemClickListener;
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

        public void setItem(Model item) {
            mItem = item;
            if (mItem == null)
                return;

            //TODO: не срабатывает. исправить.
            if (mItem.iconHrefs != null && mItem.iconHrefs.size() != 0) {
                loadIconsThread = new Thread() {
                    @Override
                    public void run() {
                        mItem.loadIconsFromSite();
                    }
                };
                loadIconsThread.start();
            }

            loadContentThread = new Thread() {
                @Override
                public void run() {
                    mItem.fillViewForList(itemView);
                }
            };
            loadContentThread.start();
        }

        void stopProcesses() {
            if (mItem == null)
                return;
            if (loadIconsThread != null && loadIconsThread.getState() == Thread.State.RUNNABLE)
                loadIconsThread.interrupt();
            if (loadContentThread.getState() == Thread.State.RUNNABLE)
                loadContentThread.interrupt();
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null)
                mOnItemClickListener.onClick(this, mItem, v);
        }


        public static abstract class OnItemViewHolderClickListener {
            public abstract void onClick(ItemViewHolder itemViewHolder, Model item, View v);
        }

    }
}
