package com.example.eventbusdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tvHello = (TextView) findViewById(R.id.tv_hello);
        tvHello.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SecondActivity.class));
            }
        });

        EventBus.getDefault().register(this);
        Bundle bundle = new Bundle();
        bundle.putString("string","i am string");
        bundle.putInt("int",1);
        EventBus.getDefault().post(bundle);
        newThread();
    }

    /*
        eventBus回调的地方
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Object obj) {

        if (obj instanceof Bundle){
            Toast.makeText(getApplicationContext(),((Bundle) obj).getString("string"),Toast.LENGTH_SHORT).show();
        }else if (obj instanceof customObj){
            Toast.makeText(getApplicationContext(),((customObj) obj).toString(),Toast.LENGTH_SHORT).show();
        }else if (obj instanceof String){
            Toast.makeText(getApplicationContext(),(String)obj,Toast.LENGTH_SHORT).show();

        }
    }



    private void newThread(){
        new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    Thread.sleep(3*1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                EventBus.getDefault().post(new customObj());
            }
        }).start();
    }

    private class customObj{
        int i = 0;
        String s = null;

        public void setInt(int i){
            this.i = i;
        }
        public void setString(String s){
            this.s = s;
        }

        @Override
        public String toString() {
            return "customObj{" +
                    "s='" + s + '\'' +
                    ", i=" + i +
                    '}';
        }
    }
}