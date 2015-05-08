package edu.guet.jjhome.guetw5.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.ViewPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.ViewPagerItems;
import com.ogaclejapan.smarttablayout.utils.v4.Bundler;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import java.util.ArrayList;

import edu.guet.jjhome.guetw5.R;
import edu.guet.jjhome.guetw5.adapter.ItemAdapter;
import edu.guet.jjhome.guetw5.model.Item;
import edu.guet.jjhome.guetw5.model.User;
import edu.guet.jjhome.guetw5.util.AppConstants;
import edu.guet.jjhome.guetw5.util.WebService;

public class MainActivity extends ActionBarActivity
        implements DrawerFragment.FragmentDrawerListener {

    private String mTitle;
    private DrawerLayout mDrawerLayout;

    private DrawerFragment drawerFragment;
    private Toolbar mToolbar;
    private AccountHeader.Result headerResult;
    private OverviewFragment fragment;
    private ViewPager viewPager;
    private SmartTabLayout viewPagerTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        processView();

//        Intent intent = new Intent(this, MyNavigationDrawer.class);
//        startActivity(intent);
    }

    private void processView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initDrawer();

//        viewPager = (ViewPager) findViewById(R.id.viewpager);
//        viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);
//
//        final Bundler bundler1 = new Bundler();
//        bundler1.putString("msg_type", AppConstants.MSG_ALL);
//        bundler1.putString("read_status", AppConstants.MSG_STATUS_UNREAD);
//        final Bundler bundler2 = new Bundler();
//        bundler2.putString("msg_type", AppConstants.MSG_ALL);
//        bundler2.putString("read_status", AppConstants.MSG_STATUS_READ);
//        final Bundler bundler3 = new Bundler();
//        bundler3.putString("msg_type", AppConstants.MSG_PUBLIC);
//
//
//        final FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
//                getSupportFragmentManager(), FragmentPagerItems.with(getBaseContext())
//                .add(AppConstants.MSG_STATUS_UNREAD, OverviewFragment.class, bundler1.get())
//                .add(AppConstants.MSG_STATUS_READ, OverviewFragment.class, bundler2.get())
//                .add(AppConstants.MSG_PUBLIC, OverviewFragment.class, bundler3.get())
//                .create());
//        viewPager.setAdapter(adapter);
//        viewPagerTab.setViewPager(viewPager);



//        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//
//        drawerFragment = (DrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
//        drawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
//        drawerFragment.setDrawerListener(this);

//        displayView(0);
    }

    private void initDrawer() {
        headerResult = new AccountHeader()
                .withActivity(this)
                .withHeaderBackground(R.drawable.black_gradient)
                .addProfiles(
                        initAccount()
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();


        Drawer.Result result = new Drawer()
                .withActivity(this)
                .withToolbar(mToolbar)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.nav_item_unread),
                        new PrimaryDrawerItem().withName(R.string.nav_item_read),
                        new PrimaryDrawerItem().withName(R.string.nav_item_public),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName(R.string.nav_item_setting)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        switch (position) {
                            case 0:
                                fragment = OverviewFragment.newInstance(AppConstants.MSG_ALL, AppConstants.MSG_STATUS_UNREAD);
                                break;
                            case 1:
                                fragment = OverviewFragment.newInstance(AppConstants.MSG_ALL, AppConstants.MSG_STATUS_READ);
                                break;
                            case 2:
                                fragment = OverviewFragment.newInstance(AppConstants.MSG_PUBLIC, "");
                                break;
                            case 4:
                                startActivity(new Intent(getBaseContext(), SettingsActivity.class));
                                break;
                        }

                        if (fragment != null) {
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.container, fragment);
                            fragmentTransaction.commit();
                        }
                    }
                })
                .build();

        result.setSelection(0, true);
    }


    private ProfileDrawerItem initAccount() {
        User u = new Select().from(User.class).where("current=?", true).orderBy("ID ASC").executeSingle();
        ProfileDrawerItem account;
        if (u != null) {
            account = new ProfileDrawerItem().withName(u.username).withEmail(" ").withIcon(getResources().getDrawable(R.drawable.ic_profile));
        }
        else {
            account = new ProfileDrawerItem().withName("Not Login").withIcon(getResources().getDrawable(R.drawable.ic_profile));
        }
        return account;
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    private void displayView(int position) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);
        switch (position) {
            case 0:
                fragment = OverviewFragment.newInstance(AppConstants.NOTICE_ALL);
                title = getString(R.string.nav_item_message);
                break;
            case 1:
                fragment = OverviewFragment.newInstance(AppConstants.NOTICE_PUBLIC);
                title = getString(R.string.nav_item_public);
                break;
            case 2:
                fragment = OverviewFragment.newInstance(AppConstants.NOTICE_ALL);
                title = getString(R.string.nav_item_outbox);
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, fragment);
            fragmentTransaction.commit();

            // set the toolbar title
            getSupportActionBar().setTitle(title);
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.nav_item_message);
                break;
            case 2:
                mTitle = getString(R.string.nav_item_public);
                break;
            case 3:
                mTitle = getString(R.string.nav_item_outbox);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        if (!mNavigationDrawerFragment.isDrawerOpen()) {
//            // Only show items in the action bar relevant to this screen
//            // if the drawer is not showing. Otherwise, let the drawer
//            // decide what to show in the action bar.
//            getMenuInflater().inflate(R.menu.global, menu);
//            restoreActionBar();
//            return true;
//        }
//        return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.global, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(getBaseContext(), SettingsActivity.class));
        }
        // Click drawer toggle button
//        if ((item.getItemId() == android.R.id.home)) {
//            if (drawerFragment.isDrawerOpen()) {
//                mDrawerLayout.closeDrawer(findViewById(R.id.navigation_drawer));
//            }
//            else {
//                mDrawerLayout.openDrawer(findViewById(R.id.navigation_drawer));
//            }
//        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_main, container, false);
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

    private class MsgHandler implements Handler.Callback {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case AppConstants.STAGE_LOGIN:
                    break;
                case AppConstants.STAGE_GET_PAGE:
                    break;
                case AppConstants.STAGE_GET_ERROR:
                    break;
                case AppConstants.STAGE_GET_SUCCESS:
                    break;
                case AppConstants.STAGE_NOT_LOGIN:
                    break;
                case AppConstants.STAGE_LOGOUT:
                    break;
            }
            return false;
        }
    }


    @Override
    public void onBackPressed(){
        moveTaskToBack(true);
    }
}
