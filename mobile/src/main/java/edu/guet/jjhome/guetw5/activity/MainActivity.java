package edu.guet.jjhome.guetw5.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.activeandroid.query.Select;
import com.afollestad.materialdialogs.MaterialDialog;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import edu.guet.jjhome.guetw5.R;
import edu.guet.jjhome.guetw5.model.Contact;
import edu.guet.jjhome.guetw5.model.User;
import edu.guet.jjhome.guetw5.util.AppConstants;
import edu.guet.jjhome.guetw5.util.WebService;

public class MainActivity extends ActionBarActivity {
    private Toolbar mToolbar;
    private AccountHeader.Result headerResult;
    private MessageFragment fragment;

    static int drawerPosition;
    private Drawer.Result drawerResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        processView();
    }

    private void processView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initDrawer();

        if (User.currentUser().position_id == null || Contact.getAllContact().length == 0) {
            Handler handler = new Handler(new MsgHandler());
            WebService web = new WebService(getBaseContext(), handler);
            web.makeCreatePage();

            Log.d("all contact size:", String.valueOf(Contact.getAllContact().length));
        }
    }

    private void initDrawer() {
        headerResult = new AccountHeader()
                .withActivity(this)
                .withHeaderBackground(R.drawable.bg)
                .withHeaderBackgroundScaleType(ImageView.ScaleType.CENTER_CROP)
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


        drawerResult = new Drawer()
                .withActivity(this)
                .withToolbar(mToolbar)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.nav_item_unread),
                        new PrimaryDrawerItem().withName(R.string.nav_item_read),
                        new PrimaryDrawerItem().withName(R.string.nav_item_public),
                        new PrimaryDrawerItem().withName(R.string.nav_item_sent),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName(R.string.nav_item_setting),
                        new SecondaryDrawerItem().withName(R.string.nav_item_help),
                        new SecondaryDrawerItem().withName(R.string.nav_item_about)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        switch (position) {
                            case 0:
                                backupPosition(position);
                                fragment = MessageFragment.newInstance(AppConstants.MSG_ALL, AppConstants.MSG_STATUS_UNREAD);
                                getSupportActionBar().setTitle(R.string.nav_item_unread);
                                break;
                            case 1:
                                backupPosition(position);
                                fragment = MessageFragment.newInstance(AppConstants.MSG_ALL, AppConstants.MSG_STATUS_READ);
                                getSupportActionBar().setTitle(R.string.nav_item_read);
                                break;
                            case 2:
                                backupPosition(position);
                                fragment = MessageFragment.newInstance(AppConstants.MSG_PUBLIC, "");
                                getSupportActionBar().setTitle(R.string.nav_item_public);
                                break;
                            case 3:
                                backupPosition(position);
                                fragment = MessageFragment.newInstance(AppConstants.MSG_SENT, "");
                                getSupportActionBar().setTitle(R.string.nav_item_sent);
                                break;
                            case 5:
                                restorePosition();
                                startActivity(new Intent(getBaseContext(), SettingsActivity.class));
                                break;
                            case 6:
                                restorePosition();
                                startActivity(new Intent(getBaseContext(), AboutActivity.class));
                                break;
                            case 7:
                                restorePosition();
                                showAbout();
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

        drawerResult.setSelection(0, true);
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

    private void backupPosition(int position) {
        drawerPosition = position;
    }

    private void restorePosition() {
        drawerResult.setSelection(drawerPosition, false);
    }

    public void showAbout() {
        new MaterialDialog.Builder(this)
                .title(R.string.dialog_about)
                .content(R.string.dialog_about_content)
                .positiveText(R.string.action_back)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.global, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(getBaseContext(), SettingsActivity.class));
        }

        return super.onOptionsItemSelected(item);
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
