package edu.guet.jjhome.guetw5.model;

public class NavDrawerItem {
    private boolean showNotify;
    private String title;
    private int icon;
    private String count = "0";
    private boolean countVisible = false;


    public NavDrawerItem() {

    }

    public NavDrawerItem(String title, int icon) {
        this.title = title;
        this.icon = icon;
    }

    public NavDrawerItem(boolean showNotify, String title) {
        this.showNotify = showNotify;
        this.title = title;
    }

    public NavDrawerItem(String title, int icon, boolean isCounterVisible, String count){
        this.title = title;
        this.icon = icon;
        this.countVisible = isCounterVisible;
        this.count = count;
    }

    public boolean isShowNotify() {
        return showNotify;
    }

    public void setShowNotify(boolean showNotify) {
        this.showNotify = showNotify;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public boolean isCountVisible() {
        return countVisible;
    }

    public void setCountVisible(boolean countVisible) {
        this.countVisible = countVisible;
    }
}
