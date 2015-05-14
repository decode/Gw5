package edu.guet.jjhome.guetw5.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.loopj.android.http.RequestParams;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.nispok.snackbar.listeners.EventListener;
import com.tokenautocomplete.FilteredArrayAdapter;
import com.tokenautocomplete.TokenCompleteTextView;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import java.util.ArrayList;
import java.util.List;

import edu.guet.jjhome.guetw5.R;
import edu.guet.jjhome.guetw5.model.Contact;
import edu.guet.jjhome.guetw5.model.User;
import edu.guet.jjhome.guetw5.util.AppConstants;
import edu.guet.jjhome.guetw5.util.WebService;
import edu.guet.jjhome.guetw5.view.ContactsCompletionView;

public class CreateMessageActivity extends ActionBarActivity implements Validator.ValidationListener {

    private WebService web;
    private Handler handler;
    private BetterSpinner spinner_urgency;
    private BetterSpinner spinner_important;
    private BetterSpinner spinner_limit;
    private BetterSpinner spinner_trace;
    private Switch switchSms;
    @NotEmpty(message = "请指定至少一个联系人")
    private ContactsCompletionView completionView;
    @NotEmpty(message = "请输入标题")
    private EditText editSubject;
    @NotEmpty(message = "请输入消息正文")
    private EditText editBody;

    private CheckBox cb1;
    private CheckBox cb2;
    private CheckBox cb3;
    private CheckBox cb4;
    private CheckBox cb5;
    private Validator validator;
    private String created_message_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_message);

//        AppUtils.syncCookies(this);

        validator = new Validator(this);
        validator.setValidationListener(this);

        processView();
    }

    private void processView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.message_toolbar);
        toolbar.setTitle(R.string.title_activity_create_message);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (User.currentUser().position_id == null || Contact.getAllContact().length == 0) {
            handler = new Handler(new MsgHandler());
            web = new WebService(getBaseContext(), handler);
            web.makeCreatePage();
        }

        spinner_urgency = (BetterSpinner) findViewById(R.id.spinnerUrgency);
        spinner_important = (BetterSpinner) findViewById(R.id.spinnerImportant);
        spinner_limit = (BetterSpinner) findViewById(R.id.spinnerLimit);
        spinner_trace = (BetterSpinner) findViewById(R.id.spinnerTrace);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.urgency, android.R.layout.simple_dropdown_item_1line);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.important, android.R.layout.simple_dropdown_item_1line);
        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this,
                R.array.limit, android.R.layout.simple_dropdown_item_1line);
        ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(this,
                R.array.trace, android.R.layout.simple_dropdown_item_1line);
        spinner_urgency.setAdapter(adapter1);
        spinner_important.setAdapter(adapter2);
        spinner_limit.setAdapter(adapter3);
        spinner_trace.setAdapter(adapter4);

        Contact[] contact = Contact.getAllContact();
//        contact = new Contact[] {
//                new Contact("aaaaa", "1111111"),
//                new Contact("abaaa", "1111111"),
//                new Contact("acaaa", "1111111"),
//                new Contact("ddd-aaa", "1111111"),
//                new Contact("bbbbb", "1111111"),
//                new Contact("ccccc", "1111111"),
//                new Contact("ddddd", "1111111")
//        };

        FilteredArrayAdapter<Contact> adapter = new FilteredArrayAdapter<Contact>(this, android.R.layout.simple_list_item_1, contact) {
            @Override
            protected boolean keepObject(Contact obj, String mask) {
                mask = mask.toLowerCase();
                return obj.getName().toLowerCase().contains(mask);
            }
        };

        completionView = (ContactsCompletionView)findViewById(R.id.searchView);
        completionView.setAdapter(adapter);
        completionView.setTokenClickStyle(TokenCompleteTextView.TokenClickStyle.Delete);
        completionView.setThreshold(1);
        completionView.allowDuplicates(false);

        editSubject = (EditText) findViewById(R.id.editTitle);
        editBody = (EditText) findViewById(R.id.editContent);
        
        switchSms = (Switch) findViewById(R.id.switchSms);

        cb1 = (CheckBox) findViewById(R.id.checkBox1);
        cb2 = (CheckBox) findViewById(R.id.checkBox2);
        cb3 = (CheckBox) findViewById(R.id.checkBox3);
        cb4 = (CheckBox) findViewById(R.id.checkBox4);
        cb5 = (CheckBox) findViewById(R.id.checkBox5);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_message, menu);
        return super.onCreateOptionsMenu(menu);
//        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_send:
                validator.validate();
                break;
            default:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void makePost() {
        List<Object> list = completionView.getObjects();
        Contact receiver;
        ArrayList<String> receivers = new ArrayList<>();
        for(int i=0; i<list.size(); i++) {
            receiver = (Contact) list.get(i);
            if (!Contact.existed(receiver.getName())) {
                Toast.makeText(this, R.string.notify_no_contact + receiver.name, Toast.LENGTH_SHORT).show();
                return;
            }
            receivers.add(receiver.getCode());
        }
        Log.d("send message to: ", receivers.toString());

        String subject = editSubject.getText().toString();
        String body = editBody.getText().toString();
        String sms = String.valueOf(switchSms.isChecked());

        String urgency = spinner_urgency.getText().toString();
        if (urgency.equals("紧急"))
            urgency = "1";
        else
            urgency = "0";

        String important = spinner_important.getText().toString();
        if (important.equals("重要"))
            important = "1";
        else
            important = "0";
        String limit = spinner_limit.getText().toString();
        if (limit.contains("不限制"))
            limit = "0";
        else
            limit = "1";
        String trace = spinner_trace.getText().toString();
        if (trace.contains("全部"))
            trace = "1";
        else
            trace = "0";

        Log.d("subject:", subject);
        Log.d("body:", body);
        Log.d("sms:", sms);

        Log.d("urgency:", urgency);
        Log.d("important:", important);
        Log.d("limit:", limit);
        Log.d("trace:", trace);

        ArrayList<String> directions = new ArrayList<>();
        if (cb1.isChecked())
            directions.add(cb1.getText().toString());
        if (cb2.isChecked())
            directions.add(cb2.getText().toString());
        if (cb3.isChecked())
            directions.add(cb3.getText().toString());
        if (cb4.isChecked())
            directions.add(cb4.getText().toString());
        if (cb5.isChecked())
            directions.add(cb5.getText().toString());
        if (directions.size() == 0)
            directions.add(cb1.getText().toString());
        Log.d("direction:", directions.toString());


        RequestParams params = new RequestParams();
        // 标题
        params.put("Subject", subject);
        // 正文
        params.put("Body", body);

        // 工作方向: Array 0: 科研
        params.put("Directions", directions.toArray(new String[directions.size()]));

        // 发布人: 553f212d-44b2-458a-9c05-03bd6b021156
        params.put("CreatorPositionId", User.currentUser().position_id);

        // 限制选项: 1
        params.put("LimitOption", limit);

        // 跟踪选项: 0
        params.put("TraceOption", trace);

        //
        params.put("forPositions", "on");

        // 发送短信提醒: false
        params.put("RemindOnCreate", sms);

        // 阅读超时时间: ""-null
        params.put("Deadline", "");

        // HoursBeforeDeadLIne: null

        // 紧急度
        params.put("UrgencyOption", urgency);

        // 重要度
        params.put("ImportantOption", important);

        // Array 0: 03-553f212d44b2458a9c0503bd6b021156
        params.put("Targets", receivers.toArray(new String[receivers.size()]));

        Log.d("message params:", params.toString());

//        web.postCreateMessage(params);
        showSnackbar();
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

    @Override
    public void onValidationSucceeded() {
        makePost();
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
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
                case AppConstants.STAGE_POST_FAILED:
                    Toast.makeText(getBaseContext(), R.string.stage_post_failed, Toast.LENGTH_LONG).show();
                    break;
                case AppConstants.STAGE_POST_SUCCESS:
                    Toast.makeText(getBaseContext(), R.string.stage_post_success, Toast.LENGTH_SHORT).show();
                    web.getCreatedMessageId();
                    break;
                case AppConstants.STAGE_GET_MESSAGE_ID:
                    created_message_id = msg.getData().getString(AppConstants.STAGE_GET_MESSAGE_ID_KEY);
                    if (created_message_id != null) showSnackbar();
                    else finish();
                    break;
                case AppConstants.STAGE_UNDO_MESSAGE_SUCCESS:
                    Toast.makeText(getBaseContext(), R.string.stage_undo_success, Toast.LENGTH_LONG).show();
                    finish();
                    break;
            }
            return false;
        }
    }

    private void showSnackbar() {
        SnackbarManager.show(
                Snackbar.with(getApplicationContext())
                        .text(R.string.notify_ask_undo)
                        .actionLabel(R.string.action_undo)
                        .duration(AppConstants.SNACKBAR_DURATION)
                        .actionListener(new ActionClickListener() {
                            @Override
                            public void onActionClicked(Snackbar snackbar) {
                                if (created_message_id != null)
                                    web.undo(created_message_id);
                            }
                        })
                        .eventListener(new EventListener() {
                            @Override
                            public void onShow(Snackbar snackbar) {
                            }

                            @Override
                            public void onShowByReplace(Snackbar snackbar) {
                            }

                            @Override
                            public void onShown(Snackbar snackbar) {
                            }

                            @Override
                            public void onDismiss(Snackbar snackbar) {
                            }

                            @Override
                            public void onDismissByReplace(Snackbar snackbar) {
                            }

                            @Override
                            public void onDismissed(Snackbar snackbar) {
                                finish();
                            }
                        }) // Snackbar's EventListener
                , this);
    }
}
