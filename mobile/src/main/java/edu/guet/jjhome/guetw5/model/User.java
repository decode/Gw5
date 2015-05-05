package edu.guet.jjhome.guetw5.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.Calendar;

@Table(name = "Users")
public class User extends Model {
    @Column(name = "username")
    public String username;

    @Column(name = "password")
    public String password;

    @Column(name = "current")
    public boolean current;

    @Column(name = "login_count")
    public int login_count;

    @Column(name = "last_used")
    public long last_used;

    @Column(name = "initial_at")
    public long initial_at;

    // Other field
    @Column(name = "remote_id")
    public String remote_id;

    @Column(name = "department")
    public String department;

    @Column(name = "role")
    public String role;

    public User() {
        super();
        this.initial_at = Calendar.getInstance().getTimeInMillis();
        this.last_used = Calendar.getInstance().getTimeInMillis();
    }

    public User(String username, String password) {
        super();
        this.username = username;
        this.password = password;
        this.initial_at = Calendar.getInstance().getTimeInMillis();
    }

    /**
     * Search or Create user
     * @param username
     * @param password
     * @return existed user or new user
     */
    public static User fetch(String username, String password) {
        User u = new Select().from(User.class).where("username=?", username).orderBy("ID ASC").executeSingle();
        if (u == null) {
            u = new User(username, password);
        }
        else if (!u.password.equals(password)) {
            u.password = password;
        }
        return u;
    }

    public static User currentUser() {
        return new Select().from(User.class).where("current=?", true).orderBy("ID ASC").executeSingle();
    }
}
