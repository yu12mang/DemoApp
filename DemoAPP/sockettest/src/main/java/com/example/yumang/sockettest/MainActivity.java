package com.example.yumang.sockettest;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yumang.sockettest.utils.SocketHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class MainActivity extends AppCompatActivity {

    private String TAG = "LOGAN_TAG";
    private Button btnSendSocket = null;
    private TextView tvResult = null;
    private String deviceIP = "192.168.1.254";
    Socket socket;

    private Handler socketHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x123: {

                    tvResult.setText("正常");
                }
                break;
                case 0x456: {
                    tvResult.setText("异常");

                }

                break;

                default:
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();


    }

    private void initView() {
        btnSendSocket = (Button) findViewById(R.id.btn_sendSocket);
        tvResult = (TextView) findViewById(R.id.tv_result);

        btnSendSocket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ClientThread thread = new ClientThread(socketHandler);
//                new Thread(thread).start();
                SocketHelper helper = new SocketHelper(socketHandler);
            }
        });


    }


}
