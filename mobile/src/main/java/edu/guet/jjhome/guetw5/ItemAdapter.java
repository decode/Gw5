package edu.guet.jjhome.guetw5;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ItemAdapter extends ArrayAdapter<Item> {
    private List<Item> items;

    private static class ViewHolder {
        private TextView tSender;
        private TextView tContent;
        private TextView tSent_at;
    }

    public ItemAdapter(Context context, List<Item> items) {
        super(context, 0, items);
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
            viewHolder.tSender = (TextView) convertView.findViewById(R.id.sender);
            viewHolder.tContent = (TextView) convertView.findViewById(R.id.content);
            viewHolder.tSent_at = (TextView) convertView.findViewById(R.id.sent_at);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Item p = getItem(position);
        if (p != null) {
            viewHolder.tSender.setText(p.getSender());
            viewHolder.tContent.setText(p.getContent());
            DateFormat df = new SimpleDateFormat(AppConstants.DATE_FORMAT_DEST);
            Date d = new Date(p.getSent_at());
            viewHolder.tSent_at.setText(df.format(d));
        }

        return convertView;
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
