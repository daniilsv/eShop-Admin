package ru.dvs.eshop.admin.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import ru.dvs.eshop.R;
import ru.dvs.eshop.admin.Core;
import ru.dvs.eshop.admin.ui.activities.draggableListView.BoardFragment;


/**
 * Главная активность приложения
 */
public class MainActivity extends AppCompatActivity {
    //private static InfoFragment infoFragment = null;

    private Toolbar toolbar;
    private DrawerLayout drawer;
    private Core core;

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
        if (savedInstanceState == null) {
            reattachCurFragment(BoardFragment.newInstance());
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
/*
        View leftNavMenuHeaderView = getLayoutInflater().inflate(R.layout.nav_menu_header, leftNavMenu, false);
        if (leftNavMenuHeaderView != null) {
            leftNavMenu.addHeaderView(leftNavMenuHeaderView);

        }
  */
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

    //Заполняем верхнее меню
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.over_items, menu);
        return true;
    }

    //При выборе пункта в верхнем меню
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        /*switch (id) {
            case R.id.menu_reload:
                refreshCurFragment();
                break;
            case R.id.menu_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.menu_about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
        }*/
        return true;
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

    //Переподключает текущий фрагмент
    public void reattachCurFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment, "fragment").commit();
    }

    //При выборе фрагмента в левом меню
    private class LeftNavigationMenuItemListener implements NavigationView.OnNavigationItemSelectedListener {

        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            curFragment = null;
            switch (item.getItemId()) {
                case R.id.menu_summary:
                    curFragment = (infoFragment != null) ? infoFragment : (infoFragment = new InfoFragment());
                    toolbar.setTitle(Core.getString(R.string.menu_info));
                    break;
            }
            //Устанавливаем новый фрагмент
            if (curFragment != null)
                getFragmentManager().beginTransaction().replace(R.id.main_frame, curFragment).commit();
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
    }
}