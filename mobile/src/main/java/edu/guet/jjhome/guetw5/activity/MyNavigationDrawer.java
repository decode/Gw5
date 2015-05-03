package edu.guet.jjhome.guetw5.activity;

import android.content.Intent;
import android.os.Bundle;

import com.activeandroid.query.Select;

import edu.guet.jjhome.guetw5.R;
import edu.guet.jjhome.guetw5.model.User;
import edu.guet.jjhome.guetw5.util.AppConstants;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialAccount;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;
import it.neokree.materialnavigationdrawer.elements.listeners.MaterialSectionListener;

public class MyNavigationDrawer extends MaterialNavigationDrawer{
    @Override
    public void init(Bundle bundle) {

        MaterialSection section_all = newSection(getString(R.string.nav_item_message), OverviewFragment.newInstance(AppConstants.NOTICE_ALL));
        MaterialSection section_public = newSection(getString(R.string.nav_item_public), OverviewFragment.newInstance(AppConstants.NOTICE_PUBLIC));
        MaterialSection section_outbox = newSection(getString(R.string.nav_item_outbox), OverviewFragment.newInstance(AppConstants.NOTICE_ALL));
        this.addSection(section_all);
        this.addSection(section_public);
        this.addSection(section_outbox);

        initAccountStatus();

        this.addBottomSection(newSection(getString(R.string.title_activity_settings), new Intent(this, SettingsActivity.class)));

        setDefaultSectionLoaded(0);
        disableLearningPattern();
        enableToolbarElevation();
        setBackPattern(MaterialNavigationDrawer.BACKPATTERN_BACK_ANYWHERE);
    }


    @Override
    public void onBackPressed(){
        moveTaskToBack(true);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            accountChange();
        }
        if (resultCode == RESULT_CANCELED) {
            //Write your code if there's no result
        }
    }

    private void initAccountStatus() {
        User u = new Select().from(User.class).where("current=?", true).orderBy("ID ASC").executeSingle();
        MaterialAccount account;
        MaterialSection section;
        if (u != null) {
            account = new MaterialAccount(this.getResources(), u.username, u.department, R.drawable.ic_profile, R.drawable.ic_home);
        }
        else {
            account = new MaterialAccount(this.getResources(), "Not log in", "", R.drawable.ic_profile, R.drawable.ic_home);
        }
        this.addAccount(account);
    }

    class RemoveAccount implements Runnable {
        @Override
        public void run() {
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    removeAccount(getCurrentAccount());
                    notifyAccountDataChanged();
                }
            });
        }
    }

    protected void accountChange() {
        new Thread(new RemoveAccount()).start();

        User u = User.CurrentUser();
        MaterialAccount account;
        if (u != null) {
            account = new MaterialAccount(this.getResources(), u.username, u.department, R.drawable.ic_profile, R.drawable.ic_home);
        }
        else {
            account = new MaterialAccount(this.getResources(), "Not log in", "", R.drawable.ic_profile, R.drawable.ic_home);
        }
        addAccount(account);
        notifyAccountDataChanged();
//        sectionChange();
    }

    protected void sectionChange() {
        MaterialSection login = getSectionByTitle(getString(R.string.nav_item_login));
        MaterialSection logout = getSectionByTitle(getString(R.string.nav_item_logout));
        if (login != null) {
            login.setTitle(getString(R.string.nav_item_logout));
            login.setOnClickListener(new MaterialSectionListener() {
                @Override
                public void onClick(MaterialSection materialSection) {
                    User u = User.CurrentUser();
                    u.delete();
                    accountChange();
                }
            });
            setSection(login);
        }
        else {
            logout.setTitle(getString(R.string.nav_item_login));
            logout.setOnClickListener(new MaterialSectionListener() {
                @Override
                public void onClick(MaterialSection materialSection) {
                    startActivityForResult(new Intent(getBaseContext(), LoginActivity.class), 1);
                }
            });
            setSection(logout);
        }
    }
}
