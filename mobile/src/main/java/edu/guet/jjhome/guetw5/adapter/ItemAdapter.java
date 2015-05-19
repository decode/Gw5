package edu.guet.jjhome.guetw5.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.Interval;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import edu.guet.jjhome.guetw5.util.AppConstants;
import edu.guet.jjhome.guetw5.model.Item;
import edu.guet.jjhome.guetw5.R;

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
            viewHolder.tContent.setText(p.title);
//            DateFormat df = new SimpleDateFormat(AppConstants.DATE_FORMAT_DEST);
//            Date d = new Date(p.getSent_at());

//            DateTime dt = new DateTime(p.sent_at, tz);
//            DateTime ct = DateTime.now(tz);
            DateTime dt = new DateTime(p.sent_at, AppConstants.TIME_ZONE);
            DateTime ct = DateTime.now(AppConstants.TIME_ZONE);
            Duration duration = new Interval(dt, ct).toDuration();
            String date_text;
            if (duration.getStandardDays() < AppConstants.DAY_BEFORE) {
                date_text = dt.dayOfWeek().getAsShortText(Locale.CHINESE);
            }
            else {
                date_text = dt.toString(AppConstants.DATE_FORMAT_DEST_SIMPLE);
            }

//            viewHolder.tSent_at.setText(df.format(d));
            viewHolder.tSent_at.setText(date_text);

//            if (p.read_status != null) {
//                if (p.read_status.equals(AppConstants.MSG_STATUS_UNREAD)) {
//                    viewHolder.tSender.setTextColor(Color.BLACK);
//                    viewHolder.tContent.setTextColor(Color.BLACK);
//                    viewHolder.tSent_at.setTextColor(Color.BLACK);
//                } else {
//                    viewHolder.tSender.setTextColor(Color.GRAY);
//                    viewHolder.tContent.setTextColor(Color.GRAY);
//                    viewHolder.tSent_at.setTextColor(Color.GRAY);
//                }
//            }
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
