package edu.guet.jjhome.guetw5.util;

import org.joda.time.DateTimeZone;

import java.util.TimeZone;

public class AppConstants {
    public static final String MY_ACCOUNT_TYPE = "edu.guet.accounts";

    public static final String PREF_SELECTED_ACCOUNT_EMAIL = "AccountEmail";
    public static final String APP_PREF_NM = "AccountPreferences";

    public static final String DATE_FORMAT_SOURCE = "yyyy/M/d H:mm";
    public static final String DATE_FORMAT_DEST = "yyyy/MM/dd HH:mm";
    public static final String DATE_FORMAT_DEST_SIMPLE = "M月d日";
    public static final int DAY_BEFORE = 4;
    public static final DateTimeZone TIME_ZONE = DateTimeZone.forTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));

    public static final int NOTICE_PUBLIC = 1;
    public static final int NOTICE_ALL = 2;

    public static final int STAGE_LOGIN = 0;
    public static final int STAGE_GET_PAGE = 1;
    public static final int STAGE_GET_ERROR = 2;
    public static final int STAGE_GET_SUCCESS = 3;
    public static final int STAGE_NOT_LOGIN = 5;
    public static final int STAGE_USERNAME_ERROR = 6;
    public static final int STAGE_PASSWORD_ERROR = 7;
    public static final int STAGE_LOGIN_FAILED = 8;
    public static final int STAGE_LOGOUT = 9;
    public static final int STAGE_POST_FAILED = 10;
    public static final int STAGE_POST_SUCCESS = 11;
    public static final int STAGE_GET_MESSAGE_ID = 12;
    public static final int STAGE_UNDO_MESSAGE_SUCCESS = 13;

    public static final String STAGE_GET_ERROR_KEY = "ERROR_MSG";
    public static final String STAGE_GET_MESSAGE_ID_KEY = "MESSAGE_ID";

    public static final String MSG_ALL = "all";
    public static final String MSG_PUBLIC = "public";
    public static final String MSG_SENT = "sent";

    public static final String MSG_LIST = "MESSAGES";

    public static final String PREF_AUTOLOGIN = "pref_auto_login";

    public static final String MSG_STATUS_READ = "已读";
    public static final String MSG_STATUS_UNREAD = "未读";

    public static final long SNACKBAR_DURATION = 5000;

}
