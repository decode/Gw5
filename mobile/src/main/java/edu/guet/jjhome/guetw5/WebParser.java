package edu.guet.jjhome.guetw5;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WebParser {
    private Document doc;

    public WebParser(String page) {
        doc = Jsoup.parse(page);
    }

    public ArrayList<Item> parseItemList() {
        Elements notices = doc.select("tbody > tr");
        Elements columns;
        ArrayList<Item> items = new ArrayList<>();
        Item item;
        DateFormat source_format = new SimpleDateFormat(AppConstants.DATE_FORMAT_SOURCE);
        for (Element notice : notices) {
            columns = notice.select("td");
            if (columns.size() == 6) {
                item = new Item();

                // Read status
                Element read_status = columns.get(0).select("span").first();
                item.setMsg_status(read_status.text().trim());

                // Message content
                Element msg_content = columns.get(0).select("a").first();
                item.setContent(msg_content.text().trim());

                // Source
                item.setSource(msg_content.attr("href"));

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
                item = new Item();

                // Message content
                Element msg_content = columns.get(0).select("a").first();
                item.setContent(msg_content.text().trim());

                // Source
                item.setSource(msg_content.attr("href"));

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

                items.add(item);
            }
        }
        return items;
    }
}

