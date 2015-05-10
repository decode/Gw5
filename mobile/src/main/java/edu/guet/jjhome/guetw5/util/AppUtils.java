package edu.guet.jjhome.guetw5.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.loopj.android.http.PersistentCookieStore;

import org.apache.http.cookie.Cookie;

import java.util.List;

public class AppUtils {
    /**
     * Retrieve the account stored for the application.
     *
     * @param context used to access the preferences (usually the Activity)
     * @return stored account name or empty string
     */
    public static String getStoredAccount(Context context) {
        return getStoredProperty(context, AppConstants.PREF_SELECTED_ACCOUNT_EMAIL);
    }

    /**
     * Set the account stored for the application.
     */
    public static void setStoredAccount(Context context, String account) {
        SharedPreferences preferences = context.getSharedPreferences(AppConstants.APP_PREF_NM,
                Context.MODE_PRIVATE);
        preferences.edit().putString(AppConstants.PREF_SELECTED_ACCOUNT_EMAIL, account).commit();
    }

    /**
     * Utility method for retrieving stored properties.
     */
    private static String getStoredProperty(Context context, String propertyName) {
        SharedPreferences preferences = context.getSharedPreferences(AppConstants.APP_PREF_NM,
                Context.MODE_PRIVATE);
        if (preferences == null) {
            return "";
        }
        return preferences.getString(propertyName, "");
    }

    public static void syncCookies(Context context) {

        PersistentCookieStore myCookieStore = new PersistentCookieStore(context);

        List<Cookie> cookies = myCookieStore.getCookies();

        CookieManager cookieManager = CookieManager.getInstance();

        for (int i = 0; i < cookies.size(); i++) {
            Cookie eachCookie = cookies.get(i);
            String cookieString = eachCookie.getName() + "=" + eachCookie.getValue();
            cookieManager.setCookie("http://guetw5.myclub2.com", cookieString);
            Log.i(">>>>>", "cookie : " + cookieString);
        }

        CookieSyncManager.getInstance().sync();
    }
}
