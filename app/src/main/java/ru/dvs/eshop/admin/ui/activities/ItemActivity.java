package ru.dvs.eshop.admin.ui.activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import ru.dvs.eshop.R;
import ru.dvs.eshop.admin.Core;
import ru.dvs.eshop.admin.ui.fragments.ItemViewFragment;
import ru.dvs.eshop.admin.ui.fragments.VendorsFragment;

public class ItemActivity extends AppCompatActivity {
    private Fragment curFragment;
    private VendorsFragment vendorsFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        Core core = Core.getInstance();
        core.setActivity(this);

        ItemViewFragment itemViewFragment = new ItemViewFragment();
        itemViewFragment.setArguments(getIntent().getExtras());
        placeFragment(itemViewFragment, false);

    }

    //При нажатии кнопки назад
    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
        } else {
            getFragmentManager().popBackStack();
        }
    }

    //Обновление текуущего фрагмента
    public void placeFragment(Fragment fragment, boolean addToBackStack) {
        if (fragment == null)
            return;
        FragmentTransaction ft = getFragmentManager().
                beginTransaction().
                setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out).
                replace(R.id.item_frame, fragment);
        if (addToBackStack)
            ft.addToBackStack(null);
        ft.commit();
    }

}
