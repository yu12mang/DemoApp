package com.example.administrator.demoapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

public class ChildrenToChildrenHandlerActivity extends Activity {

    private Handler handler;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_children_to_children);

        oneThread.start();

        findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                twoThread.start();
            }
        });


    }


    private Thread oneThread = new Thread(){

        @SuppressLint("HandlerLeak")
        @Override
        public void run() {
            super.run();
            Looper.prepare();

            handler = new Handler(Looper.myLooper()){
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    final boolean isMainThread = isMainThread();
                    final int i = msg.what;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"是否是子线程:"+!isMainThread +"--接收消息为："+i,Toast.LENGTH_LONG).show();
                        }
                    });
                }
            };

            Looper.loop();//开始轮循
        }
    };
    private Thread twoThread = new Thread(){

        @Override
        public void run() {
            super.run();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            handler.sendEmptyMessage(100);
        }
    };

    public boolean isMainThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }


}
