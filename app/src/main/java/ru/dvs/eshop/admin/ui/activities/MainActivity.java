package ru.dvs.eshop.admin.ui.activities;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import butterknife.BindView;
import butterknife.ButterKnife;
import ru.dvs.eshop.admin.R;
import ru.dvs.eshop.admin.data.Site;
import ru.dvs.eshop.admin.ui.fragments.TypeListFragment;

public class MainActivity extends AppCompatActivity {
    public static Site site;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_menu)
    NavigationView leftNavMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        leftNavMenu.setItemIconTintList(null);

        leftNavMenu.setNavigationItemSelectedListener(new LeftNavigationMenuItemListener());

        View leftNavMenuHeaderView = getLayoutInflater().inflate(R.layout.nav_header_main, leftNavMenu, false);
        if (leftNavMenuHeaderView != null) leftNavMenu.addHeaderView(leftNavMenuHeaderView);

        site = new Site();
    }

    //При возвращении из другой активности
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

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

    @Override
    protected void onResume() {
        super.onResume();
    }

    //При выборе фрагмента в левом меню
    private class LeftNavigationMenuItemListener implements NavigationView.OnNavigationItemSelectedListener {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Bundle arguments = new Bundle();
            Fragment curFragment = null;
            switch (item.getItemId()) {
                case R.id.menu_categories:
                    curFragment = new TypeListFragment();
                    arguments.putString("type", "categories");
                    break;
                case R.id.menu_vendors:
                    curFragment = new TypeListFragment();
                    arguments.putString("type", "vendors");
                    break;
            }
            //Устанавливаем новый фрагмент
            if (curFragment != null) {
                curFragment.setArguments(arguments);
                getFragmentManager().
                        beginTransaction().
                        setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out).
                        replace(R.id.main_frame, curFragment).
                        commit();
            }
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
    }
}