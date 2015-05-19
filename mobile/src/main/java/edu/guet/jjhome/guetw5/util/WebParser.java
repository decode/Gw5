package edu.guet.jjhome.guetw5.util;

import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import edu.guet.jjhome.guetw5.model.Contact;
import edu.guet.jjhome.guetw5.model.Item;
import edu.guet.jjhome.guetw5.model.User;

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
//        DateFormat source_format = new SimpleDateFormat(AppConstants.DATE_FORMAT_SOURCE);
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
                //                    Date date = source_format.parse(columns.get(5).text().trim());
//                    item.setSent_at(date.getTime());
                item.sent_at = convertDate(columns.get(5).text().trim());

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
//        DateFormat source_format = new SimpleDateFormat(AppConstants.DATE_FORMAT_SOURCE);
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
                //                    Date date = source_format.parse(columns.get(4).text().trim());
//                    item.setSent_at(date.getTime());
                item.sent_at = convertDate(columns.get(4).text().trim());

                item.save();
                items.add(item);
            }
        }
        return items;
    }

    private long convertDate(String d) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern(AppConstants.DATE_FORMAT_SOURCE);
        DateTime dt = fmt.withZone(AppConstants.TIME_ZONE).parseDateTime(d);
        return dt.getMillis();
    }

    private Item prepareItem(Elements columns) {
        Element msg_content = columns.get(0).select("a").first();
        String source = msg_content.attr("href");
        Item item;
        String message_id = messageId(source);
        if (message_id != null) {
            item = Item.fetchItem(message_id);
            // Message id
            item.message_id = message_id;
        } else {
            item = new Item();
        }

        // Message content
        item.title = msg_content.text().trim();

        // Source
        if (!source.contains(WebService.base_url))
            source = WebService.base_url + source;
        item.source = source;

        return item;
    }

    public Item parseMessageDetail(String message_id) {
        Item item = Item.fetchItem(message_id);
        Element content = doc.select("div.notice-body").first();
//        item.content = content.text();
        String page = content.html().replace("/NoticeTask/", WebService.base_url + "/NoticeTask/");
        Log.d("html page: ", content.html());
        item.content = page;

        Elements info = doc.select("span.label.label-success");
        ArrayList<String> directions = new ArrayList<>();
        for(Element direction : info) {
            Log.d("parseMessageDetail", direction.text());
            directions.add(direction.text());
        }
        item.directions =  directions.toArray(new String[directions.size()]).toString();

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

    /**
     * When post a message, fetch the message_id from message list page.
     * @return message id
     */
    public String parseCreatedMessageId() {
        Element element = doc.select("tbody > tr > td > a").first();
        String source = element.attr("href");
        return messageId(source);
    }

    public void parseSendMessage() {
        Elements trs = doc.select("tbody > tr:nth-child(odd)");
        Log.d("column size: ", String.valueOf(trs.size()));
        Elements tds;
        Element td;
        Item item;
        for (Element tr : trs) {

            tds = tr.select("td");
            // title
            td = tds.get(0);
            String source = td.select("a").attr("href");
            String message_id = messageId(source);
            item = Item.fetchItem(message_id);
            item.title = td.text();

            // directions
            // TODO: save message directions
            td = tds.get(1);
            item.directions = td.text();

            // sent time
            td = tds.get(2);
            DateFormat source_format = new SimpleDateFormat(AppConstants.DATE_FORMAT_SOURCE);
            Date date = null;
            try {
                date = source_format.parse(td.text().trim());
                item.setSent_at(date.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // status
            // TODO: save message status
            td = tds.get(3);

            item.msg_type = AppConstants.MSG_SENT;
            item.sender = User.currentUser().username;

            Log.d("Send message information:", item.sender + item.title + item.directions + item.message_id + item.sent_at);
            item.save();
        }
    }

    private String messageId(String url) {
        if (url.contains("Details/")) {
            return url.substring(url.indexOf("Details/") + 8, url.length());
        }
        return null;
    }
}

