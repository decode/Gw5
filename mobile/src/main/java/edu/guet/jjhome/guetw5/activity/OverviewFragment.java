package edu.guet.jjhome.guetw5.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
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

import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;

import edu.guet.jjhome.guetw5.R;
import edu.guet.jjhome.guetw5.adapter.ItemAdapter;
import edu.guet.jjhome.guetw5.model.Item;
import edu.guet.jjhome.guetw5.model.User;
import edu.guet.jjhome.guetw5.util.AppConstants;
import edu.guet.jjhome.guetw5.util.WebService;

public class OverviewFragment extends Fragment {
    private static final String ARG_TYPE = "type";

    private int param_type;

    private ItemAdapter itemAdapter;

    private int selectedCount = 0;

    private Handler handler;
    private TextView txt_status;
    private ListView lv_items;

    WebService web;
    private String msg_type;
    private String read_status;
    private FloatingActionButton fab;

    // TODO: Rename and change types and number of parameters
    public static OverviewFragment newInstance(int view_type) {
        OverviewFragment fragment = new OverviewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TYPE, view_type);
        fragment.setArguments(args);
        Log.d("new Instance view_type", " ");
        return fragment;
    }
    public static OverviewFragment newInstance(String msg_type, String read_status) {
        OverviewFragment fragment = new OverviewFragment();

        Bundle args = new Bundle();
        args.putString("msg_type", msg_type);
        args.putString("read_status", read_status);
        fragment.setArguments(args);

        Log.d("new Instance msg_type", " ");

        return fragment;
    }

    public OverviewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().getString("msg_type") != null) {
            msg_type = getArguments().getString("msg_type");
        }
        else {
            msg_type = "all";
        }

        if (getArguments().getString("read_status") != null) {
            read_status = getArguments().getString("read_status");
        }
        else {
            read_status = "";
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

        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(clickListener);

        Toast.makeText(getActivity().getBaseContext(), R.string.action_refresh_status, Toast.LENGTH_SHORT).show();
        web = new WebService(getActivity().getBaseContext(), handler);
        web.fetchContent(msg_type);

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
                    txt_status.setText(R.string.action_refresh_status);
                    txt_status.setVisibility(View.VISIBLE);
                    break;
                case AppConstants.STAGE_GET_ERROR:
                    txt_status.setText(R.string.state_get_error);
                    txt_status.setVisibility(View.VISIBLE);
                    break;
                case AppConstants.STAGE_GET_SUCCESS:
//                    ((MaterialNavigationDrawer)getActivity()).getCurrentSection().setNotifications(new_count);
                    txt_status.setVisibility(View.INVISIBLE);
                    lv_items.setVisibility(View.VISIBLE);

                    itemAdapter.clear();
//                    itemAdapter.addAll(Item.getItemsByType(msg_type));
                    itemAdapter.addAll(Item.getItemsByTypeAndStatus(msg_type, read_status));
                    Log.d("after success, test msg_type", msg_type);
                    itemAdapter.notifyDataSetChanged();
                    break;
                case AppConstants.STAGE_NOT_LOGIN:
                    txt_status.setText("You are not login. Please re-login");
                    txt_status.setVisibility(View.VISIBLE);
                    lv_items.setVisibility(View.INVISIBLE);
                    break;
                case AppConstants.STAGE_LOGOUT:
                    Toast.makeText(getActivity(), "Logout Success", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getActivity(), LoginActivity.class));
            }
            return false;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        menu.clear();
        inflater.inflate(R.menu.overview, menu);
        User u = User.currentUser();
        MenuItem menu_login = menu.findItem(R.id.action_login);
        MenuItem menu_logout = menu.findItem(R.id.action_logout);
        MenuItem menu_refresh = menu.findItem(R.id.action_overview_refresh);
        if (u != null) {
            menu_login.setVisible(false);
            menu_logout.setVisible(true);
            menu_refresh.setVisible(true);
        }
        else {
            menu_logout.setVisible(true);
            menu_logout.setVisible(false);
            menu_refresh.setVisible(false);
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
            Toast.makeText(getActivity().getBaseContext(), getString(R.string.action_refresh_status), Toast.LENGTH_SHORT).show();
            web = new WebService(getActivity().getBaseContext(), handler);
            web.fetchContent(msg_type);
        }
        if (id == R.id.action_login) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(intent, 1);
        }
        if (id == R.id.action_logout) {
            User u = User.currentUser();
            if (u != null) {
                u.delete();
//                MyNavigationDrawer my_drawer = (MyNavigationDrawer) this.getActivity();
//                my_drawer.accountChange();
                web = new WebService(getActivity().getBaseContext(), handler);
                web.logout();

                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(AppConstants.PREF_AUTOLOGIN, false);
                editor.commit();

                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, 1);


            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.fab:
                    startActivity(new Intent(getActivity().getBaseContext(), CreateMessageActivity.class));
                    break;
            }
        }
    };

}
