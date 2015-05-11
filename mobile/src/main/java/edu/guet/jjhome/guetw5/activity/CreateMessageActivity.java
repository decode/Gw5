package edu.guet.jjhome.guetw5.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.loopj.android.http.PersistentCookieStore;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import org.apache.http.cookie.Cookie;

import java.util.List;

import edu.guet.jjhome.guetw5.R;
import edu.guet.jjhome.guetw5.model.Contact;
import edu.guet.jjhome.guetw5.util.AppConstants;
import edu.guet.jjhome.guetw5.util.AppUtils;
import edu.guet.jjhome.guetw5.util.WebService;

public class CreateMessageActivity extends ActionBarActivity {

    private WebService web;
    private Handler handler;
    private BetterSpinner spinner_urgency;
    private BetterSpinner spinner_important;
    private ContactsCompletionView completionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_message);

//        AppUtils.syncCookies(this);

        processView();
    }

    private void processView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.message_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        handler = new Handler(new MsgHandler());
        web = new WebService(getBaseContext(), handler);
        web.makeCreatePage();


        spinner_urgency = (BetterSpinner) findViewById(R.id.spinnerUrgency);
        spinner_important = (BetterSpinner) findViewById(R.id.spinnerImportant);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.urgency, android.R.layout.simple_dropdown_item_1line);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.important, android.R.layout.simple_dropdown_item_1line);
        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this,
                R.array.limit, android.R.layout.simple_dropdown_item_1line);
        ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(this,
                R.array.trace, android.R.layout.simple_dropdown_item_1line);
//        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_urgency.setAdapter(adapter1);
        spinner_important.setAdapter(adapter2);

        Contact[] contact = Contact.getAllContact();

        ArrayAdapter<Contact> adapter = new ArrayAdapter<Contact>(this, android.R.layout.simple_list_item_1, contact);

        completionView = (ContactsCompletionView)findViewById(R.id.searchView);
        completionView.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_message, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showDirections(View view) {
        new MaterialDialog.Builder(this)
                .title(R.string.hint_message_directions)
                .items(R.array.directions)
                .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                        /**
                         * If you use alwaysCallMultiChoiceCallback(), which is discussed below,
                         * returning false here won't allow the newly selected check box to actually be selected.
                         * See the limited multi choice dialog example in the sample project for details.
                         **/
                        Log.d("multi choice:", String.valueOf(text));
                        Log.d("multi choice item:", which.toString());
                        return true;
                    }
                })
                .positiveText(R.string.action_choose)
                .show();
    }

    public void showContacts(View view) {

    }


    private class MsgHandler implements Handler.Callback {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case AppConstants.STAGE_GET_ERROR:
                    break;
                case AppConstants.STAGE_GET_SUCCESS:
                    Log.d("success, parse create message", "----------------------");
                    break;
                case AppConstants.STAGE_NOT_LOGIN:
                    break;
            }
            return false;
        }
    }
}
