/*
 * Copyright 2017 Wand. All Rights Reserved.
 */
package com.cruisecloud.util;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * Created by wand on 2017/8/5.
 */

public abstract class MyHandler<T extends Activity> extends Handler {

    private final WeakReference<T> weakReference;

    public MyHandler(T activity){
        weakReference = new WeakReference<T>(activity);
    }

    @Override
    public final void handleMessage(Message msg) {

        T activity = weakReference.get();
        if(activity == null)
            return;

        handleMessage(activity, msg);
    }

    /**
     * handle message here
     *
     * @param activity
     * @param msg
     */
    protected void handleMessage(T activity, Message msg){};
}
