package edu.guet.jjhome.guetw5.activity;

import android.os.Bundle;

import edu.guet.jjhome.guetw5.util.AppConstants;
import edu.guet.jjhome.guetw5.R;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialAccount;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;

public class MyNavigationDrawer extends MaterialNavigationDrawer{
    @Override
    public void init(Bundle bundle) {
        MaterialAccount account = new MaterialAccount(this.getResources(), "JJHome", "aaa@gmail.com", R.drawable.ic_profile, R.drawable.black_gradient);
        this.addAccount(account);

        MaterialSection section_all = newSection(getString(R.string.nav_item_message), OverviewFragment.newInstance(AppConstants.NOTICE_ALL));
        MaterialSection section_public = newSection(getString(R.string.nav_item_public), OverviewFragment.newInstance(AppConstants.NOTICE_PUBLIC));
        MaterialSection section_outbox = newSection(getString(R.string.nav_item_outbox), OverviewFragment.newInstance(AppConstants.NOTICE_ALL));
        this.addSection(section_all);
        this.addSection(section_public);
        this.addSection(section_outbox);

        this.addBottomSection(newSection(getString(R.string.title_activity_settings), OverviewFragment.newInstance(AppConstants.NOTICE_ALL)));

        setDefaultSectionLoaded(0);
        disableLearningPattern();
        setBackPattern(MaterialNavigationDrawer.BACKPATTERN_BACK_ANYWHERE);
    }

    @Override
    public void onBackPressed(){
        moveTaskToBack(true);
    }
}
