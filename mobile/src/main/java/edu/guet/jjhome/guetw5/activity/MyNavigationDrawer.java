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
        User u = new Select().from(User.class).where("current=?", true).orderBy("ID ASC").executeSingle();
        MaterialAccount account;
        if (u != null) {
            account = new MaterialAccount(this.getResources(), u.username, u.department, R.drawable.ic_profile, R.drawable.black_gradient);

            this.addBottomSection(newSection(getString(R.string.nav_item_logout),
                    new MaterialSectionListener() {
                        @Override
                        public void onClick(MaterialSection materialSection) {
                            User u = new Select().from(User.class).where("current=?", true).orderBy("ID ASC").executeSingle();
                            if (u != null) {
                                u.delete();
                            }
                        }
                    }
            ));
        }
        else {
            account = new MaterialAccount(this.getResources(), "Not log in", "", R.drawable.ic_profile, R.drawable.black_gradient);
            this.addBottomSection(newSection(getString(R.string.nav_item_login),
                    new MaterialSectionListener() {
                        @Override
                        public void onClick(MaterialSection materialSection) {
                            User u = new Select().from(User.class).where("current=?", true).orderBy("ID ASC").executeSingle();
                            if (u != null) {
                                u.delete();
                            }
                        }
                    }
            ));
        }
        this.addAccount(account);
        this.addDivisor();

        MaterialSection section_all = newSection(getString(R.string.nav_item_message), OverviewFragment.newInstance(AppConstants.NOTICE_ALL));
        MaterialSection section_public = newSection(getString(R.string.nav_item_public), OverviewFragment.newInstance(AppConstants.NOTICE_PUBLIC));
        MaterialSection section_outbox = newSection(getString(R.string.nav_item_outbox), OverviewFragment.newInstance(AppConstants.NOTICE_ALL));
        this.addSection(section_all);
        this.addSection(section_public);
        this.addSection(section_outbox);

//        this.addBottomSection(newSection(getString(R.string.title_activity_settings), new Intent(this, SettingsActivity.class)));
//        this.addBottomSection(newSection(getString(R.string.title_activity_settings), new SettingsFragment()));

        setDefaultSectionLoaded(0);
        disableLearningPattern();
        enableToolbarElevation();
        setBackPattern(MaterialNavigationDrawer.BACKPATTERN_BACK_ANYWHERE);
    }

    @Override
    public void onBackPressed(){
        moveTaskToBack(true);
    }
}
