package ru.dvs.eshop.admin.ui.activities;

import android.app.Fragment;
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

        //intent
        //what
        //id

        //itemView
        //itemEdit
        //(Button)DeleteItem


        placeFragment(itemViewFragment);
    }

    //При нажатии кнопки назад
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    //Обновление текуущего фрагмента
    private void placeFragment(Fragment fragment) {
        if (fragment == null)
            return;
        getFragmentManager().beginTransaction().replace(R.id.item_frame, fragment).commit();
    }

}
