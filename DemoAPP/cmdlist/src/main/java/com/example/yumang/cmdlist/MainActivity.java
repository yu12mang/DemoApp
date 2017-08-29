package com.example.yumang.cmdlist;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.ntk.nvtkit.NVTKitModel;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void cmd3031(View v) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                final String result = NVTKitModel.customFunctionForCommand("http://192.168.1.254/?custom=1&cmd=3031&str=all");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                        dialog.setMessage(result);
                        dialog.create().show();
                    }
                });

            }
        }).start();
    }
}
