package com.example.administrator.demoapp;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainToChildrenHandlerActivity extends Activity {

    private MyThread thread = new MyThread();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_to_children);
        thread.start();

        findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Handler mHandler = new Handler(thread.looper) {
                    public void handleMessage(android.os.Message msg) {
                        final boolean isMainThread = isMainThread();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),"当前是否是子线程："+!isMainThread,Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                };
                mHandler.sendEmptyMessage(1);
            }
        });
    }

    class MyThread extends Thread {
        private Looper looper;//取出该子线程的Looper

        @Override
        public void run() {
            Looper.prepare();//创建该子线程的Looper
            looper = Looper.myLooper();//取出该子线程的Looper
            Looper.loop();//只要调用了该方法才能不断循环取出消息
        }
    }

    public boolean isMainThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }

}
