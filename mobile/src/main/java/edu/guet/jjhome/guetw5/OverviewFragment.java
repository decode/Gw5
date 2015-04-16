package edu.guet.jjhome.guetw5;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnItemSelectedListener} interface
 * to handle interaction events.
 * Use the {@link OverviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OverviewFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String ARG_TYPE = "type";
    private static final String ARG_PARAM = "items";


    // TODO: Rename and change types of parameters
    private String param_type;
    private String mParam2;
    private ArrayList<Item> items;

    private OnItemSelectedListener mListener;

    private ItemAdapter itemAdapter;

    private static final String ARG_SECTION_NUMBER = "section_number";

    private int selectedCount = 0;

    private Handler handler;
    private TextView txt_status;

    // TODO: Rename and change types and number of parameters
    public static OverviewFragment newInstance(String view_type) {
        OverviewFragment fragment = new OverviewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, view_type);
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
            param_type = getArguments().getString(ARG_TYPE);
        }

        ArrayList<Item> items = new ArrayList<>();
        itemAdapter = new ItemAdapter(getActivity().getBaseContext(), items);

        handler = new Handler(new MsgHandler());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_overview, container, false);

        ListView lv = (ListView) rootView.findViewById(R.id.listView);
        txt_status = (TextView) rootView.findViewById(R.id.text_status);

        if(param_type.equals("person")) {
//            ArrayList<Item> items = new ArrayList<>();
//            Item i1 = new Item(1, "sender1", "content1", 1);
//            Item i2 = new Item(2, "sender2", "content2", 1);
//            Item i3 = new Item(3, "sender3", "content3", 1);
//            items.add(i1);
//            items.add(i2);
//            items.add(i3);

            lv.setAdapter(itemAdapter);
        }

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Item item = itemAdapter.getItem(position);
                if(selectedCount > 0) {

                }
                else {
                    Intent intent = new Intent(".Details");
                    intent.putExtra("item", item);
                    startActivity(intent);
                }
            }
        });

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
//            mListener.onItemSelected(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
        try {
            mListener = (OnItemSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnItemSelectedListener {
        // TODO: Update argument type and name
        void onItemSelected(int position);
    }

    public void onItemClick(View v, int position) {
        mListener.onItemSelected(position);
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
                    break;

            }
            return false;
        }
    }
}
