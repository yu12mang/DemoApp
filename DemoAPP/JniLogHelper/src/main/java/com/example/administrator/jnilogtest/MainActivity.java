package com.example.administrator.jnilogtest;

import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements CustomHandler.OnReceiveMessageListener {

    static {
        System.loadLibrary("native-lib");
    }
    public native String stringFromJNI();

    private CustomHandler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandler = new CustomHandler<MainActivity>(this);
        mHandler.setOnReceiveMessageListener(this);

        LogcatHelper.getInstance(this).setJniHandler(mHandler).start();

        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogcatHelper.getInstance(this).stop();
    }

    @Override
    public void onReceiveMessage(Message msg) {
        switch(msg.what){
            case 0x123:{
                String line = (String)msg.obj;
                Toast.makeText(MainActivity.this,"jniCall:"+line,Toast.LENGTH_LONG).show();
            }
            break;
        }
    }
}
