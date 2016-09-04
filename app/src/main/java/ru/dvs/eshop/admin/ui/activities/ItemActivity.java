package ru.dvs.eshop.admin.ui.activities;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import ru.dvs.eshop.R;
import ru.dvs.eshop.admin.Core;
import ru.dvs.eshop.admin.ui.fragments.ItemViewFragment;
import ru.dvs.eshop.admin.ui.fragments.VendorsFragment;

/**
 * Created by Никита on 04.09.2016.
 */
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
        itemViewFragment.setItemActivity(this);
        placeFragment(itemViewFragment);
    }

    public void setToolbar(Toolbar tb) {
        setSupportActionBar(tb);
    }

    //При нажатии кнопки назад
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    //Обновление текуущего фрагмента
    private void placeFragment(Fragment fragment) {
        getFragmentManager().beginTransaction().replace(R.id.item_frame, fragment).commit();
    }

    //Переподключает текущий фрагмент
    public void reattachCurFragment(Fragment fragment) {
        getFragmentManager().beginTransaction().detach(curFragment).attach(curFragment).commit();
    }
}
