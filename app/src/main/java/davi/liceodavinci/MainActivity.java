package davi.liceodavinci;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;

import com.google.firebase.messaging.FirebaseMessaging;

import java.io.File;

import davi.liceodavinci.Communications.Communication;
import davi.liceodavinci.Communications.CommunicationsFragment;
import davi.liceodavinci.Schedule.ScheduleFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Fragment selection = new CommunicationsFragment(this, Communication.COMM_STUDENTS);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        new ConfigurationManager(this);
        deleteCache(this);
        ConfigurationManager.getIstance().setCacheDeleted();

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (ConfigurationManager.getIstance().getCommNotificationEnabled(Communication.COMM_STUDENTS))
            FirebaseMessaging.getInstance().subscribeToTopic(NotificationsManager.COMM_STUDENTS_TOPIC);
        else
            FirebaseMessaging.getInstance().unsubscribeFromTopic(NotificationsManager.COMM_STUDENTS_TOPIC);

        if (ConfigurationManager.getIstance().getCommNotificationEnabled(Communication.COMM_PARENTS))
            FirebaseMessaging.getInstance().subscribeToTopic(NotificationsManager.COMM_PARENTS_TOPIC);
        else
            FirebaseMessaging.getInstance().unsubscribeFromTopic(NotificationsManager.COMM_PARENTS_TOPIC);

        if (ConfigurationManager.getIstance().getCommNotificationEnabled(Communication.COMM_PROFS))
            FirebaseMessaging.getInstance().subscribeToTopic(NotificationsManager.COMM_PROFS_TOPIC);
        else
            FirebaseMessaging.getInstance().unsubscribeFromTopic(NotificationsManager.COMM_PROFS_TOPIC);

        Log.d("comm-stu", String.valueOf(ConfigurationManager.getIstance().getCommNotificationEnabled(Communication.COMM_STUDENTS)));
        Log.d("comm-par", String.valueOf(ConfigurationManager.getIstance().getCommNotificationEnabled(Communication.COMM_PARENTS)));
        Log.d("comm-pro", String.valueOf(ConfigurationManager.getIstance().getCommNotificationEnabled(Communication.COMM_PROFS)));
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (selection != null) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_frame, selection)
                    .commit();
        }

        drawer.closeDrawer(GravityCompat.START);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        uncheckAllMenuItems(navigationView);
        item.setChecked(true);

        if (id == R.id.drawer_agenda) {

        } else if (id == R.id.drawer_schedule) {
            selection = new ScheduleFragment(this);
            getSupportActionBar().setTitle("Orario personale");
        } else if (id == R.id.com_students) {
            selection = new CommunicationsFragment(this, Communication.COMM_STUDENTS);
            getSupportActionBar().setTitle("Comunicati studenti");
        } else if (id == R.id.com_parents) {
            selection = new CommunicationsFragment(this, Communication.COMM_PARENTS);
            getSupportActionBar().setTitle("Comunicati genitori");
        } else if (id == R.id.com_profs) {
            selection = new CommunicationsFragment(this, Communication.COMM_PROFS);
            getSupportActionBar().setTitle("Comunicati docenti");
        } else if (id == R.id.com_saved) {
            selection = new CommunicationsFragment(this, Communication.COMM_SAVED);
            getSupportActionBar().setTitle("Comunicati salvati");
        } else if (id == R.id.drawer_settings) {
            selection = new SettingsFragment();
            getSupportActionBar().setTitle("Impostazioni");
        }

        if (selection != null) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_frame, selection)
                    .commit();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void uncheckAllMenuItems(NavigationView navigationView) {
        final Menu menu = navigationView.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (item.hasSubMenu()) {
                SubMenu subMenu = item.getSubMenu();
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    subMenuItem.setChecked(false);
                }
            } else {
                item.setChecked(false);
            }
        }
    }

    private void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception ignored) {
        }
    }

    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else
            return dir != null && dir.isFile() && dir.delete();
    }
}
