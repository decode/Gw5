package edu.guet.jjhome.guetw5.model;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.activeandroid.util.SQLiteUtils;

import java.io.Serializable;
import java.util.List;

@Table(name = "Contacts")
public class Contact extends Model implements Serializable {
    @Column(name = "name")
    public String name;

    @Column(name = "code")
    public String code;

    @Column(name = "dept_name")
    public String dept_name;

    @Column(name = "dept_code")
    public String dept_code;

    public Contact(String n, String c) {
        name = n;
        code = c;
    }

    public Contact() {

    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return name;
    }

    public static Contact fetchContact(String code) {
        Contact contact = new Select().from(Contact.class).where("code = ?", code).orderBy("id ASC").executeSingle();

        if (contact == null) {
            contact = new Contact();
            Log.d("Not find existed contact", "create new");
        }
        else {
        }
        return contact;
    }

    public static Contact[] getAllContact() {
        List<Model> contacts = new Select().from(Contact.class).orderBy("id ASC").execute();
        Contact[] contact = new Contact[contacts.size()];
        return contacts.toArray(contact);
    }

    public static boolean existed(String name) {
        List<Model> user = new Select().from(Contact.class).where("name = ?", name).execute();
        return user.size() > 0;
    }

    public static Contact currentContact() {
//        Contact c = new Select().from(Contact.class).where("name LIKE ?", User.currentUser().username).executeSingle();
        return findContactByName(User.currentUser().username);
    }

    public static Contact findContactByName(String name) {
        Contact c = (Contact) SQLiteUtils.rawQuery(Contact.class,
                "SELECT * from Contacts where name LIKE ?",
                new String[]{'%' + name + '%'}).get(0);
        Log.d("current contact: ", c.name);
        return c;
    }
}
