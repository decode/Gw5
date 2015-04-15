package edu.guet.jjhome.guetw5;

import java.io.Serializable;
import java.util.Calendar;

public class Item implements Serializable {
    private long id;
    private long message_id;
    private String sender;
    private String content;
    private long sent_at;
    private String source;

    public Item() {
        sender = "";
        content = "";
        sent_at = Calendar.getInstance().getTimeInMillis();
    }

    public Item(long id, String sender, String content, long sent_at) {
        this.id = id;
        this.sender = sender;
        this.content = content;
        this.sent_at = sent_at;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public long getMessage_id() {
        return message_id;
    }

    public void setMessage_id(long message_id) {
        this.message_id = message_id;
    }
}
