package edu.guet.jjhome.guetw5;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.io.UnsupportedEncodingException;

public class WebService {

    String login_url = "http://guetw5.myclub2.com/Account/LogOn";
    String common_url = "http://guetw5.myclub2.com/NoticeTask/Notice/Common";

    private AsyncHttpClient client;

    private Context context;
    private Handler handler;

    public WebService(Context context, Handler handler) {
        client = new AsyncHttpClient();
        PersistentCookieStore myCookieStore = new PersistentCookieStore(context);
        client.setCookieStore(myCookieStore);

        this.context = context;
        this.handler = handler;
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
                    Log.d("HTML Result", result);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                try {
                    String result = new String(errorResponse, "UTF-8");
                    Log.d("HTML Result", result);
                    Message msg = Message.obtain();
                    msg.what = AppConstants.STAGE_GET_ERROR;
                    Bundle b = new Bundle();
                    b.putString(AppConstants.STAGE_GET_ERROR_KEY, "Can't access website");
                    handler.sendMessage(msg);
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
    }

    public void fetchContent() {

    }
}
