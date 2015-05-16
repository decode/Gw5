package edu.guet.jjhome.guetw5.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.guet.jjhome.guetw5.R;
import edu.guet.jjhome.guetw5.model.Item;
import edu.guet.jjhome.guetw5.util.AppConstants;
import edu.guet.jjhome.guetw5.util.WebService;

public class DetailsActivity extends ActionBarActivity {
    private Item item;

    private TextView text_status;
    private TextView text_title;
    private TextView text_content;
    private TextView text_receiver;
    private TextView text_emergency;
    private TextView text_importance;

    private Handler handler;
    private String message_id;
    private FloatingActionMenu menu_float;
    private FloatingActionButton fab_reply;
    private FloatingActionButton fab_browser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Toolbar toolbar = (Toolbar) findViewById(R.id.details_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        processViews();

        if (getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE) {
            // If the screen is now in landscape mode, we can show the
            // dialog in-line with the list so we don't need this activity.
            finish();
            return;
        }

        if (savedInstanceState == null) {

//            FragmentManager fragmentManager = getSupportFragmentManager();
//            FragmentTransaction ft = fragmentManager.beginTransaction();            // During initial setup, plug in the details fragment.
//            DetailsFragment details = new DetailsFragment();
//            details.setArguments(getIntent().getExtras());
//
//            getSupportFragmentManager().beginTransaction().add(R.layout.item_detail, details).commit();
            item = (Item) getIntent().getSerializableExtra("item");
            text_title.setText(item.title);
            text_receiver.setText(getString(R.string.hint_message_to) + item.getReceiver());
            text_emergency.setText(item.getEmergency());
            text_importance.setText(item.getImportance());

            DateFormat df = new SimpleDateFormat(AppConstants.DATE_FORMAT_DEST);
            Date d = new Date(item.getSent_at());

            toolbar.setTitle(item.sender + " - " + df.format(d));

            if (item.content.length() < 1) {
                handler = new Handler(new MsgHandler());
                WebService web = new WebService(getBaseContext(), handler);
                message_id = item.message_id;
                web.readMessage(message_id);
            }
            else {
                text_content.setText(item.content);
            }
        }
    }

    private void processViews() {
        text_status = (TextView) findViewById(R.id.text_status);
        text_title = (TextView) findViewById(R.id.text_title);
        text_content = (TextView) findViewById(R.id.text_content);
        text_receiver= (TextView) findViewById(R.id.text_receiver);
        text_emergency = (TextView) findViewById(R.id.text_Emergency);
        text_importance = (TextView) findViewById(R.id.text_importance);

        fab_reply = (FloatingActionButton) findViewById(R.id.fab_reply);
        fab_reply.setOnClickListener(clickListener);
        fab_browser = (FloatingActionButton) findViewById(R.id.fab_browser);
        fab_browser.setOnClickListener(clickListener);

        menu_float = (FloatingActionMenu) findViewById(R.id.menu_float);
//        menu_float.addMenuButton(fab_reply);
//        menu_float.addMenuButton(fab_browser);
    }


    private class MsgHandler implements Handler.Callback {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case AppConstants.STAGE_GET_PAGE:
                    text_status.setText(getString(R.string.stage_refresh));
                    text_status.setVisibility(View.VISIBLE);
                    break;
                case AppConstants.STAGE_GET_ERROR:
                    text_status.setText(getString(R.string.state_get_error));
                    text_status.setVisibility(View.VISIBLE);
                    break;
                case AppConstants.STAGE_GET_SUCCESS:
                    text_status.setVisibility(View.INVISIBLE);
                    item = Item.fetchItem(message_id);
                    text_content.setText(item.content);
                    break;
                case AppConstants.STAGE_NOT_LOGIN:
                    text_status.setText(getString(R.string.stage_not_login));
                    text_status.setVisibility(View.VISIBLE);
                    break;
            }
            return false;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return super.onCreateOptionsMenu(menu);
//        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = menuItem.getItemId();
        switch (id) {
            case R.id.action_open_browser:
                openInBrowser();
                break;
            default:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void openInBrowser() {
        Intent i = new Intent(Intent.ACTION_VIEW,
                Uri.parse(item.source));
        Log.d("open in browser:", item.source);
        startActivity(i);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.fab_reply:
                    Intent intent = new Intent(getBaseContext(), CreateMessageActivity.class);
                    intent.putExtra("receiver", item.sender);
                    startActivity(intent);
                    break;
                case R.id.fab_browser:
                    openInBrowser();
                    break;
            }
        }
    };
}
