package edu.guet.jjhome.guetw5.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Select;

import java.util.ArrayList;

import edu.guet.jjhome.guetw5.R;
import edu.guet.jjhome.guetw5.adapter.ItemAdapter;
import edu.guet.jjhome.guetw5.model.Item;
import edu.guet.jjhome.guetw5.model.User;
import edu.guet.jjhome.guetw5.util.AppConstants;
import edu.guet.jjhome.guetw5.util.WebService;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

public class OverviewFragment extends Fragment {
    private static final String ARG_TYPE = "type";

    private int param_type;

    private ItemAdapter itemAdapter;

    private int selectedCount = 0;

    private Handler handler;
    private TextView txt_status;
    private ListView lv_items;

    // TODO: Rename and change types and number of parameters
    public static OverviewFragment newInstance(int view_type) {
        OverviewFragment fragment = new OverviewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TYPE, view_type);
        fragment.setArguments(args);
        return fragment;
    }

    public OverviewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            param_type = getArguments().getInt(ARG_TYPE);
        }

        setHasOptionsMenu(true);

        ArrayList<Item> items = new ArrayList<>();
        itemAdapter = new ItemAdapter(getActivity().getBaseContext(), items);

        handler = new Handler(new MsgHandler());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_overview, container, false);

        lv_items = (ListView) rootView.findViewById(R.id.listView);
        txt_status = (TextView) rootView.findViewById(R.id.text_status);

        lv_items.setAdapter(itemAdapter);

        lv_items.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Item item = itemAdapter.getItem(position);
                if (selectedCount > 0) {

                } else {
                    Intent intent = new Intent(".Details");
                    intent.putExtra("item", item);
                    startActivity(intent);
                }
            }
        });


        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        ((MainActivity) activity).onSectionAttached(
//                getArguments().getInt(ARG_SECTION_NUMBER));
//        try {
//            mListener = (OnItemSelectedListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }

    private class MsgHandler implements Handler.Callback {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case AppConstants.STAGE_LOGIN:
                    break;
                case AppConstants.STAGE_GET_PAGE:
                    txt_status.setText("updating...");
                    txt_status.setVisibility(View.VISIBLE);
                    break;
                case AppConstants.STAGE_GET_ERROR:
                    txt_status.setText("Get page error, please check your web connection");
                    txt_status.setVisibility(View.VISIBLE);
                    break;
                case AppConstants.STAGE_GET_SUCCESS:
                    ((MaterialNavigationDrawer)getActivity()).getCurrentSection().setNotificationsText("New");
                    txt_status.setVisibility(View.INVISIBLE);
                    break;
                case AppConstants.STAGE_NOT_LOGIN:
                    txt_status.setText("You are not login. Please re-login");
                    txt_status.setVisibility(View.VISIBLE);
                    break;
            }
            return false;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        menu.clear();
        inflater.inflate(R.menu.overview, menu);
        final User u = new Select().from(User.class).where("current=?", true).orderBy("ID ASC").executeSingle();
        MenuItem menu_login = menu.findItem(R.id.action_login);
        MenuItem menu_logout = menu.findItem(R.id.action_logout);
        if (u != null) {
            menu_login.setVisible(false);
            menu_logout.setVisible(true);
        }
        else {
            menu_logout.setVisible(true);
            menu_logout.setVisible(false);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_overview_settings) {
//            startActivity(new Intent(getActivity().getBaseContext(), SettingsActivity.class));
//        }

        if (id == R.id.action_overview_refresh) {
            Toast.makeText(getActivity().getBaseContext(), "Fragment Fetch content.", Toast.LENGTH_SHORT).show();
            WebService web = new WebService(getActivity().getBaseContext(), handler);
            web.fetchContent(param_type ,itemAdapter);
        }
        if (id == R.id.action_login) {
            startActivityForResult(new Intent(getActivity(), LoginActivity.class), 1);
        }
        if (id == R.id.action_logout) {
            User u = User.CurrentUser();
            if (u != null) {
                u.delete();
                MyNavigationDrawer my_drawer = (MyNavigationDrawer) this.getActivity();
                my_drawer.accountChange();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

}
