package edu.guet.jjhome.guetw5;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.logging.Handler;

public class MessageListLoader extends AsyncTaskLoader<ItemContainer> {

    public MessageListLoader(Context context, Handler handler) {
        super(context);
    }

    @Override
    public ItemContainer loadInBackground() {
        return null;
    }
}
