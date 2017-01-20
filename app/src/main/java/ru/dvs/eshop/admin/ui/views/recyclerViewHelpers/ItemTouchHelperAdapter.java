package ru.dvs.eshop.admin.ui.views.recyclerViewHelpers;

public interface ItemTouchHelperAdapter {

    boolean onItemMove(int fromPosition, int toPosition);

    void onItemSwiped(int position);
}
