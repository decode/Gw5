package edu.guet.jjhome.guetw5;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class ItemAdapter extends ArrayAdapter<Item> {
    private int resource;
    private List<Item> items;

    public ItemAdapter(Context context, int resource, List<Item> items) {
        super(context, resource, items);
        this.resource = resource;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout itemView;
        if (convertView == null) {
//            LayoutInflater li;
//            li = LayoutInflater.from(getContext());
//            v = li.inflate(R.layout.item, null);

            itemView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater li = (LayoutInflater) getContext().getSystemService(inflater);
            li.inflate(resource, itemView, true);
        }
        else {
            itemView = (LinearLayout) convertView;
        }

        Item p = getItem(position);
        if (p != null) {
            TextView tSender = (TextView) itemView.findViewById(R.id.sender);
            TextView tContent = (TextView) itemView.findViewById(R.id.content);
            TextView tSent_at = (TextView) itemView.findViewById(R.id.sent_at);

            if (tSender != null) {
                tSender.setText(p.getSender());
            }
            if (tContent != null) {
                tContent.setText(p.getContent());
            }
            if (tSent_at!= null) {
                tContent.setText((int) p.getSent_at());
            }
        }

        return itemView;
    }

    public void set(int index, Item item) {
        if(index>=0 && index<items.size()) {
            items.set(index, item);
            notifyDataSetChanged();
        }
    }

    public Item get(int index) {
        return items.get(index);
    }
}
