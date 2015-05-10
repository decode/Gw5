package edu.guet.jjhome.guetw5.util;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.webkit.CookieManager;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import edu.guet.jjhome.guetw5.adapter.ItemAdapter;
import edu.guet.jjhome.guetw5.model.Item;
import edu.guet.jjhome.guetw5.model.User;

public class WebService {

    private final PersistentCookieStore myCookieStore;
    String login_url = "http://guetw5.myclub2.com/Account/LogOn";
    String logout_url = "http://guetw5.myclub2.com/Account/LogOff";
    String common_url = "http://guetw5.myclub2.com/NoticeTask/Notice/Common";
    String person_url = "http://guetw5.myclub2.com/NoticeTask/Notice/AboutMe";
    String message_prefix_url = "http://guetw5.myclub2.com/NoticeTask/Notice/Details/";
    String message_create_url = "http://guetw5.myclub2.com/NoticeTask/Notice/Create";

    private AsyncHttpClient client;

    private Context context;
    private Handler handler;

    private String response;

    public WebService(Context context, Handler handler) {
        client = new AsyncHttpClient();
        myCookieStore = new PersistentCookieStore(context);
        client.setCookieStore(myCookieStore);

        this.context = context;
        this.handler = handler;
    }

    public PersistentCookieStore getCookie() {
        return this.myCookieStore;
    }

    /**
     * TODO: parse json data when success login
     * @param username
     * @param password
     */
    public void login(String username, String password) {

        RequestParams params = new RequestParams();
        params.put("Username", username);
        params.put("Password", password);
        params.put("Remember", "true");

        client.post(login_url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
                Log.d("Login...", "user login");
                Message msg = Message.obtain();
                msg.what = AppConstants.STAGE_LOGIN;
                handler.sendMessage(msg);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                try {
                    String result = new String(response, "UTF-8");
                    Log.d("Login Success", result);

                    parseLoginMessage(result);

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                noticeNetworkError(errorResponse);
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
    }

    public void logout() {
        CookieManager.getInstance().removeAllCookie();

        client.get(logout_url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String result = new String(responseBody, "UTF-8");
                    Log.d("Logout Success", "");
                    Message msg = Message.obtain();
                    msg.what = AppConstants.STAGE_LOGOUT;
                    handler.sendMessage(msg);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                noticeNetworkError(responseBody);
            }
        });
    }

    /**
     * TODO: parse next page to the end.
     * @param adapter
     */
    public void fetchContent(final String conent_type, final ItemAdapter adapter) {
        String dest_url = "";
        switch (conent_type) {
            case AppConstants.MSG_PUBLIC:
                dest_url = common_url;
                break;
            case AppConstants.MSG_ALL:
                dest_url = person_url;
                break;
        }
        Log.d("message type in webservice", dest_url);
        client.get(dest_url, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                // called before request is started
                Log.d("Fetching...", "get page data");
                Message msg = Message.obtain();
                msg.what = AppConstants.STAGE_GET_PAGE;
                handler.sendMessage(msg);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    response = new String(responseBody, "UTF-8");
                    Message msg = Message.obtain();
                    Log.d("Finished fetching", "get page data success");
                    if (isLogin(response)) {
                        WebParser parser = new WebParser(response);
                        ArrayList<Item> items;
                        if (conent_type.equals(AppConstants.MSG_ALL)) {
                            items = parser.parseItemList();
                        } else {
                            items = parser.parseCommonList();
                        }
//                        adapter.clear();
//                        adapter.addAll(items);
//                        adapter.notifyDataSetChanged();

                        msg.what = AppConstants.STAGE_GET_SUCCESS;
                        handler.sendMessage(msg);
                    } else {
                        noticeNotLogin();
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                noticeNetworkError(responseBody);
            }
        });
    }

    public boolean isLogin(String response) {
        return !response.contains("输入数据有误。") && !response.contains("用户名：") && !response.contains("密　码：");
    }

    public void parseLoginMessage(String response) {
        Message msg = Message.obtain();
        if (isLogin(response)) {
            msg.what = AppConstants.STAGE_GET_SUCCESS;
            try {
                JSONObject json = new JSONObject(response);
                String remote_id = json.getString("id");
                String name = json.getString("name");
                String sex = json.getString("sex");
                Log.d("remote_id:   ", remote_id + name + sex);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {
            if (response.contains("登录名错误")) {
                msg.what = AppConstants.STAGE_USERNAME_ERROR;
            }
            else if (response.contains("密码错误")) {
                msg.what = AppConstants.STAGE_PASSWORD_ERROR;
            }
            else {
                msg.what = AppConstants.STAGE_LOGIN_FAILED;
            }
        }
        handler.sendMessage(msg);
    }

    public void readMessage(final String message_id) {
        String url = message_prefix_url + message_id;
        Log.d("Read message detail", url);
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    response = new String(responseBody, "UTF-8");
                    if (isLogin(response)) {
                        WebParser parser = new WebParser(response);
                        Item item = parser.parseMessageDetail(message_id);
                        item.read_status = AppConstants.MSG_STATUS_READ;
                        item.save();
                        Message msg = Message.obtain();
                        msg.what = AppConstants.STAGE_GET_SUCCESS;
                        handler.sendMessage(msg);
                    } else {
                        noticeNotLogin();
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    private void noticeNotLogin() {
        Log.d("Fetch unsuccessful", "not login");
        Message msg = Message.obtain();
        msg.what = AppConstants.STAGE_NOT_LOGIN;
        handler.sendMessage(msg);
    }

    private void noticeNetworkError(byte[] responseBody) {
        try {
            String result = new String(responseBody, "UTF-8");
            Log.d("HTML Error Result", result);
            Message msg = Message.obtain();
            msg.what = AppConstants.STAGE_GET_ERROR;
            Bundle b = new Bundle();
            b.putString(AppConstants.STAGE_GET_ERROR_KEY, "Can't access website");
            handler.sendMessage(msg);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }


    public void fetch(final int conent_type) {
        String dest_url = "";
        switch (conent_type) {
            case AppConstants.NOTICE_PUBLIC:
                dest_url = common_url;
                break;
            case AppConstants.NOTICE_ALL:
                dest_url = person_url;
                break;
        }
        client.get(dest_url, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                // called before request is started
                Log.d("Fetching...", "get page data");
                Message msg = Message.obtain();
                msg.what = AppConstants.STAGE_GET_PAGE;
                handler.sendMessage(msg);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    response = new String(responseBody, "UTF-8");
                    Message msg = Message.obtain();
                    Log.d("Finished fetching", "get page data success");
                    if (isLogin(response)) {
                        WebParser parser = new WebParser(response);
                        ArrayList<Item> items;
                        if (conent_type == AppConstants.NOTICE_ALL) {
                            items = parser.parseItemList();
                        } else {
                            items = parser.parseCommonList();
                        }
                        msg.what = AppConstants.STAGE_GET_SUCCESS;
                        handler.sendMessage(msg);
                    } else {
                        noticeNotLogin();
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                noticeNetworkError(responseBody);
            }

        });
    }

    public void makeCreatePage() {
        client.get(message_create_url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    response = new String(responseBody, "UTF-8");
                    if (isLogin(response)) {
                        WebParser parser = new WebParser(response);
                        String position_id = parser.parseCreateMessagePage();
                        User user = User.currentUser();
                        user.position_id = position_id;
                        user.save();

                        Log.d("position_id", position_id);

                        Message msg = Message.obtain();
                        msg.what = AppConstants.STAGE_GET_SUCCESS;
                        handler.sendMessage(msg);
                    } else {
                        noticeNotLogin();
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                noticeNetworkError(responseBody);
            }
        });
    }

    public void postCreateMessage() {
        RequestParams params = new RequestParams();
        // 标题
        params.put("Subject", "");
        // 正文
        params.put("Body", "");

        // 工作方向: Array 0: 科研
        params.put("Directions", "");

        // 发布人: 553f212d-44b2-458a-9c05-03bd6b021156
        params.put("DisplayCreatorPositionId", "");

        // 限制选项: 1
        params.put("LimitOption", "");

        // 跟踪选项: 0
        params.put("TraceOption", "");

        //
        params.put("forPositions", "on");

        // 发送短信提醒: false
        params.put("RemindOnCreate", "false");

        // 阅读超时时间: ""-null
        params.put("Deadline", "");

        // HoursBeforeDeadLIne: null

        // 紧急度
        params.put("UrgencyOption", "0");

        // 重要度
        params.put("ImportantOption", "0");

        // Array 0: 03-553f212d44b2458a9c0503bd6b021156
        params.put("Targets", "03-553f212d44b2458a9c0503bd6b021156");

        // Return: success: true, message: "发布成功"
        // After create, redirect to url: http://guetw5.myclub2.com/NoticeTask/Notice/Create
        // using get method
        // html page include information:
//        <tbody>
//        <tr>
//        <td>
//        <a href="/NoticeTask/Notice/Details/f02bfefb-c780-47f6-9e8a-3cd99af63b90">123213123</a>
//        </td>
    }

    public void undo() {
        String url = "http://guetw5.myclub2.com/NoticeTask/Notice/Undo";
        // id: "f02bfefb-c780-47f6-9e8a-3cd99af63b90"
        // post method
        // response: success:true message:撤销成功"
    }
}
