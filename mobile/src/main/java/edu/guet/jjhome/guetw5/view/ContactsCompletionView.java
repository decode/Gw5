package edu.guet.jjhome.guetw5.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tokenautocomplete.TokenCompleteTextView;

import edu.guet.jjhome.guetw5.R;
import edu.guet.jjhome.guetw5.model.Contact;

public class ContactsCompletionView extends TokenCompleteTextView {
    public ContactsCompletionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View getViewForObject(Object object) {
        Contact p = (Contact)object;

        LayoutInflater l = (LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        LinearLayout view = (LinearLayout)l.inflate(R.layout.contact_token, (ViewGroup)ContactsCompletionView.this.getParent(), false);
        ((TextView)view.findViewById(R.id.name)).setText(p.getName());

        return view;
    }

    @Override
    protected Object defaultObject(String completionText) {
        //Stupid simple example of guessing if we have an email or not
        int index = completionText.indexOf('@');
        Log.d("completion text", completionText);
        if (index == -1) {
            return new Contact(completionText, completionText.replace(" ", "") + "@example.com");
        } else {
            return new Contact(completionText.substring(0, index), completionText);
        }
    }
}
