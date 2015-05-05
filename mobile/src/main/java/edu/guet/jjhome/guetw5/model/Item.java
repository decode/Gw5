package edu.guet.jjhome.guetw5.model;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

import edu.guet.jjhome.guetw5.util.AppConstants;

@Table(name = "Items")
public class Item extends Model implements Serializable {

    @Column(name = "message_id")
    public String message_id; // record message unique code

    @Column(name = "source")
    public String source; // record url

    @Column(name = "msg_type")
    public String msg_type;

    @Column(name = "msg_status")
    public String msg_status;

    @Column(name = "read_status")
    public String read_status;

    @Column(name = "sender")
    public String sender;

    @Column(name = "receiver")
    public String receiver;

    @Column(name = "title")
    public String title;

    @Column(name = "content")
    public String content;

    @Column(name = "sent_at")
    public long sent_at;

    @Column(name = "emergency")
    public String emergency;

    @Column(name = "importance")
    public String importance;

    public Item() {
        message_id = "";
        source = "";
        msg_status = "";
        sender = "";
        receiver = "";
        content = "";
        sent_at = Calendar.getInstance().getTimeInMillis();
        emergency = "";
        importance = "";
    }

    public Item(String sender, String content, long sent_at) {
        this.sender = sender;
        this.content = content;
        this.sent_at = sent_at;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getSent_at() {
        return sent_at;
    }

    public void setSent_at(long sent_at) {
        this.sent_at = sent_at;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public String getEmergency() {
        return emergency;
    }

    public void setEmergency(String emergency) {
        this.emergency = emergency;
    }

    public String getImportance() {
        return importance;
    }

    public void setImportance(String importance) {
        this.importance = importance;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMsg_status() {
        return msg_status;
    }

    public void setMsg_status(String msg_status) {
        this.msg_status = msg_status;
    }

    public static List<Item> getItems(String msg_type) {
        return new Select().from(Item.class).where("msg_type = ?", msg_type).orderBy("sent_at DESC").execute();
    }

    public static Item fetchItem(String message_id) {
        Item item = new Select().from(Item.class).where("message_id = ?", message_id).orderBy("id ASC").executeSingle();
        if (item == null) {
            item = new Item();
            Log.d("Not find existed item", "create new");
        }
        return item;
    }

    public static List<Item> getItemsByReadStatus(String read_status) {
        return new Select().from(Item.class)
                .where("read_status = ?", read_status)
                .orderBy("sent_at DESC").execute();
    }

    public static List<Item> getItemsByType(String msg_type) {
        return new Select().from(Item.class)
                .where("msg_type = ?", msg_type)
                .orderBy("read_status DESC")
                .execute();
    }
}