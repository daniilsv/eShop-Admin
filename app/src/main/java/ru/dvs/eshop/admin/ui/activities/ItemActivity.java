package ru.dvs.eshop.admin.ui.activities;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;

import ru.dvs.eshop.R;
import ru.dvs.eshop.admin.Core;
import ru.dvs.eshop.admin.ui.fragments.ItemEditFragment;
import ru.dvs.eshop.admin.ui.fragments.ItemViewFragment;
import ru.dvs.eshop.admin.ui.fragments.VendorsFragment;
import ru.dvs.eshop.admin.utils.Permissions;

public class ItemActivity extends AppCompatActivity {
    public static int EDIT_IMAGE_SELECT = 1;
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
        if (getIntent().getBooleanExtra("is_adding", false))
            finish();
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
        curFragment = fragment;
        ft.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Core.getInstance().setActivity(this);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;
        if (requestCode == EDIT_IMAGE_SELECT) {
            Uri selectedImageUri = data.getData();
            String imageUri = Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN ||
                    Permissions.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ?
                    getPath(selectedImageUri) : "null";
            ((ItemEditFragment) curFragment).onActivityImageResult(imageUri);
        }
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = new CursorLoader(this, uri, projection, null, null, null).loadInBackground(); //managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

}
