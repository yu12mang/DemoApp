package com.example.administrator.jnilogtest;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * Created by logan on 2018/3/11.
 */

public class CustomHandler<T extends Activity> extends Handler {

    private WeakReference<T> reference;

    private OnReceiveMessageListener listener;

    public CustomHandler(T activity) {
        reference = new WeakReference<>(activity);
    }

    public T getActivity() {
        return reference.get();
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (listener != null){
            listener.onReceiveMessage(msg);
        }
    }

    public void setOnReceiveMessageListener(OnReceiveMessageListener listener){
        this.listener = listener;
    }

    public interface OnReceiveMessageListener{
        void onReceiveMessage(Message msg);

    }
}
