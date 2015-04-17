package edu.guet.jjhome.guetw5;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DetailsActivity extends ActionBarActivity {
    private Item item;

    private TextView text_sender;
    private TextView text_content;
    private TextView text_sent_at;
    private TextView text_receiver;
    private TextView text_emergency;
    private TextView text_importance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

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
            text_sender.setText(item.getSender());
            text_content.setText(item.getContent());
            text_receiver.setText(item.getReceiver());
            text_emergency.setText(item.getEmergency());
            text_importance.setText(item.getImportance());

            DateFormat df = new SimpleDateFormat(AppConstants.DATE_FORMAT_DEST);
            Date d = new Date(item.getSent_at());
            text_sent_at.setText(df.format(d));
        }

    }

    private void processViews() {
        text_sender = (TextView) findViewById(R.id.text_sender);
        text_content = (TextView) findViewById(R.id.text_content);
        text_sent_at = (TextView) findViewById(R.id.text_sent_at);
        text_receiver= (TextView) findViewById(R.id.text_receiver);
        text_emergency = (TextView) findViewById(R.id.text_Emergency);
        text_importance = (TextView) findViewById(R.id.text_importance);
    }
}
