package edu.guet.jjhome.guetw5.util;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.webkit.CookieManager;
import android.widget.Toast;

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
    public static final String base_url = "http://guetw5.myclub2.com";
    String login_url = "http://guetw5.myclub2.com/Account/LogOn";
    String logout_url = "http://guetw5.myclub2.com/Account/LogOff";
    String common_url = "http://guetw5.myclub2.com/NoticeTask/Notice/Common";
    String person_url = "http://guetw5.myclub2.com/NoticeTask/Notice/AboutMe";
    String message_prefix_url = "http://guetw5.myclub2.com/NoticeTask/Notice/Details/";
    String message_create_url = "http://guetw5.myclub2.com/NoticeTask/Notice/Create";
    String message_send_url = "http://guetw5.myclub2.com/NoticeTask/Notice/SendNotice";
    String message_created_url = "http://guetw5.myclub2.com/NoticeTask/Notice/Created";
    String message_undo_url = "http://guetw5.myclub2.com/NoticeTask/Notice/Undo";
    String message_created_and_undo_url = "http://guetw5.myclub2.com/NoticeTask/Notice/Created?andundo=true&andundoCheck=on";

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
//        CookieManager.getInstance().removeAllCookie();

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
     * @param conent_type
     */
    public void fetchContent(final String conent_type) {
        String dest_url = "";
        switch (conent_type) {
            case AppConstants.MSG_PUBLIC:
                dest_url = common_url;
                break;
            case AppConstants.MSG_ALL:
                dest_url = person_url;
                break;
            case AppConstants.MSG_SENT:
                dest_url = message_created_and_undo_url;
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

                        switch (conent_type) {
                            case AppConstants.MSG_ALL:
                                parser.parseItemList();
                                break;
                            case AppConstants.MSG_PUBLIC:
                                parser.parseCommonList();
                                break;
                            case AppConstants.MSG_SENT:
                                parser.parseSendMessage();
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

    public boolean isLogin(String response) {
        return !response.contains("输入数据有误。") && !response.contains("用户名：") && !response.contains("密　码：");
    }

    public void parseLoginMessage(String response) {
        Message msg = Message.obtain();
        if (isLogin(response)) {
            msg.what = AppConstants.STAGE_GET_SUCCESS;
            try {
                JSONObject json = new JSONObject(response);
                JSONObject data = json.getJSONObject("data");
                String remote_id = data.getString("Id");
                String name = data.getString("Name");
                String sex = data.getString("Sex");
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
            msg.setData(b);
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

                        // Get all contacts' information
                        parser.parseDeptTree();

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

    public void postCreateMessage(RequestParams params) {

        client.post(message_send_url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Message msg = Message.obtain();
                try {
                    response = new String(responseBody, "UTF-8");
                    Log.d("after post message:", response);
                    JSONObject json = new JSONObject(response);
                    if (json.getString("success").equals("false")) {
                        msg.what = AppConstants.STAGE_POST_FAILED;
                        handler.sendMessage(msg);
                    }

                    msg.what = AppConstants.STAGE_POST_SUCCESS;
                    handler.sendMessage(msg);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });

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

    public void getCreatedMessageId() {

        client.get(message_created_url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    response = new String(responseBody, "UTF-8");
                    WebParser web = new WebParser(response);
                    String message_id = web.parseCreatedMessageId();
                    Log.d("webservice get messge_id after post:", message_id);

                    Message msg = Message.obtain();
                    Bundle b = new Bundle();
                    msg.what = AppConstants.STAGE_GET_MESSAGE_ID;
                    b.putString(AppConstants.STAGE_GET_MESSAGE_ID_KEY, message_id);
                    msg.setData(b);
                    handler.sendMessage(msg);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    public void undo(String id) {
        // id: "f02bfefb-c780-47f6-9e8a-3cd99af63b90"
        // post method
        // response: success:true message:撤销成功"

        RequestParams params = new RequestParams();
        params.put("id", id);
        client.post(message_undo_url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    response = new String(responseBody, "UTF-8");
                    Log.d("undo message", response);

                    Message msg = Message.obtain();
                    msg.what = AppConstants.STAGE_UNDO_MESSAGE_SUCCESS;
                    handler.sendMessage(msg);

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("webservice remove message_id failed", "");
            }
        });
    }

    public void getCreatedMessages() {

        client.get(message_created_and_undo_url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    response = new String(responseBody, "UTF-8");
                    WebParser web = new WebParser(response);
                    web.parseSendMessage();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }
}
