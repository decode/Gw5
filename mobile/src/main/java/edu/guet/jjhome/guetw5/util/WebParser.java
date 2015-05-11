package edu.guet.jjhome.guetw5.util;

import android.util.JsonReader;
import android.util.Log;

import com.activeandroid.ActiveAndroid;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.guet.jjhome.guetw5.model.Contact;
import edu.guet.jjhome.guetw5.model.Item;

public class WebParser {
    private Document doc;

    public WebParser(String page) {
        doc = Jsoup.parse(page);
    }

    /**
     * Get item information from page
     * TODO: If item existed, pass the process.
     * @return
     */
    public ArrayList<Item> parseItemList() {
        Elements notices = doc.select("tbody > tr");
        Elements columns;
        ArrayList<Item> items = new ArrayList<>();
        Item item;
        DateFormat source_format = new SimpleDateFormat(AppConstants.DATE_FORMAT_SOURCE);
        for (Element notice : notices) {
            columns = notice.select("td");
            if (columns.size() == 6) {

                item = prepareItem(columns);

                item.msg_type = AppConstants.MSG_ALL;

                // Read status
                Element read_status = columns.get(0).select("span").first();
                item.read_status = read_status.text().trim();


                // Receiver
                item.setReceiver(columns.get(1).text().trim());

                // Emergency
                item.setEmergency(columns.get(2).text().trim());

                // Importance
                item.setImportance(columns.get(3).text().trim());

                // Sender
                item.setSender(columns.get(4).text().trim());

                // Publish time
                try {
                    Date date = source_format.parse(columns.get(5).text().trim());
                    item.setSent_at(date.getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                item.save();
                items.add(item);
            }
        }
        return items;
    }

    public ArrayList<Item> parseCommonList() {
        Elements notices = doc.select("tbody > tr:not(.warning)");
        Elements columns;
        ArrayList<Item> items = new ArrayList<>();
        Item item;
        DateFormat source_format = new SimpleDateFormat(AppConstants.DATE_FORMAT_SOURCE);
        for (Element notice : notices) {
            columns = notice.select("td");
            if (columns.size() == 5) {

                item = prepareItem(columns);

                item.msg_type = AppConstants.MSG_PUBLIC;

                // Emergency
                item.setEmergency(columns.get(1).text().trim());

                // Importance
                item.setImportance(columns.get(2).text().trim());

                // Sender
                item.setSender(columns.get(3).text().trim());

                // Publish time
                try {
                    Date date = source_format.parse(columns.get(4).text().trim());
                    item.setSent_at(date.getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                item.save();
                items.add(item);
            }
        }
        return items;
    }

    private Item prepareItem(Elements columns) {
        Element msg_content = columns.get(0).select("a").first();
        String source = msg_content.attr("href");
        String message_id;
        Item item;
        if (source.contains("Details/")) {
            message_id = source.substring(source.indexOf("Details/") + 8, source.length());
            item = Item.fetchItem(message_id);

            // Message id
            item.message_id = message_id;
        } else {
            item = new Item();
        }

        // Message content
        item.title = msg_content.text().trim();

        // Source
        item.source = msg_content.attr("href");

        return item;
    }

    public Item parseMessageDetail(String message_id) {
        Item item = Item.fetchItem(message_id);
        Element content = doc.select("div.notice-body").first();
        item.content = content.text();

//        Element unread_role = doc.select("div.notice-trace > div.alert.alert-error").first();
//        Element read_role = doc.select("div.notice-trace > div.alert.alert-success").first();
        return item;
    }

    public String parseCreateMessagePage() {
        Element positionId = doc.select("input#CreatorPositionId").first();
        return positionId.attr("value");

    }

    public void parseDeptTree() {
        Element deptTree = doc.select("#positiontreejson").first();
        try {
            JSONArray json = new JSONArray(deptTree.data());
            JSONObject dept;
            JSONObject role;
            JSONObject user;
            JSONObject user_data;
            JSONArray children;
            String name;
            String code;
            String dept_code;
            String dept_name;
            Contact contact;

            for (int i = 0; i < json.length(); i++) {
                dept = json.getJSONObject(i);
                role = dept.getJSONObject("data");
                dept_code = role.getString("Code");
                dept_name = role.getString("Name");
                Log.d("code: ", dept_code);
                Log.d("role: ", dept_name);

                children = dept.getJSONArray("children");
                Log.d("children length: ", String.valueOf(children.length()));
                for(int j=0; j<children.length(); j++) {
                    user = children.getJSONObject(j);
                    user_data = user.getJSONObject("data");
                    code = user_data.getString("Code");
                    name = user_data.getString("Name");
                    Log.d("user code: ", code);
                    Log.d("user name: ", name);

                    contact = Contact.fetchContact(code);
                    contact.code = code;
                    contact.name = name;
                    contact.dept_code = dept_code;
                    contact.dept_name = dept_name;
                    contact.save();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}

