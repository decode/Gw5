package edu.guet.jjhome.guetw5.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.guet.jjhome.guetw5.R;

/**
 ** TODO: implement item detials
 */
public class DetailsFragment extends Fragment {
    public static DetailsFragment newInstance(String message_id) {
        DetailsFragment f = new DetailsFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putString("item_id", message_id);
        f.setArguments(args);

        return f;
    }

    public String getShownID() {
        return getArguments().getString("item_id", "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_overview, container, false);

        if (container == null) {
            // We have different layouts, and in one of them this
            // fragment's containing frame doesn't exist.  The fragment
            // may still be created from its saved state, but there is
            // no reason to try to create its view hierarchy because it
            // won't be displayed.  Note this is not needed -- we could
            // just run the code below, where we would create and return
            // the view hierarchy; it would just never be used.
            return null;
        }

//        ScrollView scroller = new ScrollView(getActivity());
//        TextView text = new TextView(getActivity());
//        scroller.addView(text);
//        text.setText("aaa");
        return rootView;
    }
}
