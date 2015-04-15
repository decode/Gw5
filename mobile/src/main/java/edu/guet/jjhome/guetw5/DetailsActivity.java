package edu.guet.jjhome.guetw5;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

public class DetailsActivity extends ActionBarActivity {
    private Item item;

    private TextView text_sender;
    private TextView text_content;
    private TextView text_sent_at;

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
            text_sent_at.setText(String.valueOf(item.getSent_at()));
        }

    }

    private void processViews() {
        text_sender = (TextView) findViewById(R.id.text_sender);
        text_content = (TextView) findViewById(R.id.text_content);
        text_sent_at = (TextView) findViewById(R.id.text_sent_at);
    }
}
