package ru.dvs.eshop.admin.ui.activities;

import android.Manifest;
import android.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import ru.dvs.eshop.R;
import ru.dvs.eshop.admin.Core;
import ru.dvs.eshop.admin.ui.fragments.PaymentTypesFragment;
import ru.dvs.eshop.admin.ui.fragments.VendorsFragment;
import ru.dvs.eshop.admin.utils.Permissions;

/**
 * Главная активность приложения
 */
public class MainActivity extends AppCompatActivity {
    //private static InfoFragment infoFragment = null;

    private Toolbar toolbar;
    private DrawerLayout drawer;
    private Core core;
    private Fragment curFragment;

    private VendorsFragment vendorsFragment = null;
    private PaymentTypesFragment paymentTypeFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        core = Core.getInstance();
        core.start(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer == null) {
            return;
        }

        NavigationView leftNavMenu = (NavigationView) findViewById(R.id.nav_menu);
        if (leftNavMenu == null) {
            return;
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        leftNavMenu.setItemIconTintList(null);

        leftNavMenu.setNavigationItemSelectedListener(new LeftNavigationMenuItemListener());

        View leftNavMenuHeaderView = getLayoutInflater().inflate(R.layout.nav_header_main, leftNavMenu, false);
        if (leftNavMenuHeaderView != null) {
            leftNavMenu.addHeaderView(leftNavMenuHeaderView);

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN &&
                !Permissions.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Permissions.requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    //При возвращении из другой активности
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       /*
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {

        }
        */
    }

    //При нажатии кнопки назад
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }


    //Обновление текуущего фрагмента
    private void refreshCurFragment() {
        //if (curFragment == null)
        //return;
        //Посылаем в фрагмент команду обновиться
        //if (curFragment instanceof InfoFragment) {
        //    infoFragment.refresh();
        // }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Core.getInstance().setActivity(this);
    }

    //При выборе фрагмента в левом меню
    private class LeftNavigationMenuItemListener implements NavigationView.OnNavigationItemSelectedListener {

        @Override
        public boolean onNavigationItemSelected(MenuItem item) {

            curFragment = null;
            switch (item.getItemId()) {
                case R.id.menu_vendors:
                    curFragment = (vendorsFragment != null) ? vendorsFragment : (vendorsFragment = new VendorsFragment());
                    break;
                case R.id.menu_payment_methods:
                    curFragment = (paymentTypeFragment != null) ? paymentTypeFragment : (paymentTypeFragment = new PaymentTypesFragment());
                    break;
            }
            //Устанавливаем новый фрагмент
            if (curFragment != null)
                getFragmentManager().
                        beginTransaction().
                        setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out).
                        replace(R.id.main_frame, curFragment).
                        commit();

            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
    }
}