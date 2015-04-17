package edu.guet.jjhome.guetw5;

import java.io.Serializable;
import java.util.Calendar;

public class Item implements Serializable {
    private long id;
    private String message_id; // record message unique code
    private String source; // record url
    private String msg_status;
    private String sender;
    private String receiver;
    private String content;
    private long sent_at;
    private String emergency;
    private String importance;

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
}
