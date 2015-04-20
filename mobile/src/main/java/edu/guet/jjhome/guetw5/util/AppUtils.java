package edu.guet.jjhome.guetw5.util;

import android.content.Context;
import android.content.SharedPreferences;

import edu.guet.jjhome.guetw5.AppConstants;

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
}
