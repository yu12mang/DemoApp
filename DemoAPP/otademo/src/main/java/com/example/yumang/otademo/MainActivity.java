package com.example.yumang.otademo;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private final static String curFWVersion = "1.0.30.216";
    private final static String FW_FILE_NAME = "FW55WIFI.bin";
    private final static String ROOT_PATH = Environment.getExternalStorageDirectory().toString();


    private TextView tvResult = null;
    private DownloadFile mDownload;

    private boolean isDownloadFinshed = true;

    private ProgressDialog dialog ;
    private Handler progressHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (dialog.isShowing()){
                dialog.setProgress(msg.arg1);
            }
            if (msg.what == DownloadFile.REQUEST_DOWNLOAD_FILE_CANCELED){

                isDownloadFinshed = false;
                dialog.dismiss();
            }
            if (msg.what == DownloadFile.REQUEST_DOWNLOAD_FILE_FINISHED){
                dialog.dismiss();
                isDownloadFinshed = true;

            }

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvResult = (TextView) findViewById(R.id.tv_result);

    }

    public void queryFW(View v){

        new Thread(new Runnable() {
            @Override
            public void run() {
                final OTAItem item =  OTAHelper.getOTAVersion();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (item == null){
                            tvResult.setText("结果为NULL");
                        }else{

                            if (curFWVersion.equalsIgnoreCase(item.getFWVersion())){
                                tvResult.setText("已经是最新固件 不需要更新！");
                            }else{

                                File file = new File(ROOT_PATH+"/"+FW_FILE_NAME);
                                if (file.exists()){
                                    isDownloadFinshed = true;
                                    tvResult.setText("文件已存在");
                                    return;
                                }
                                dialog = new ProgressDialog(MainActivity.this);
                                dialog.setTitle("提示信息");
                                dialog.setMessage("正在下载、、、");
                                dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                //设置点击进度条外部，不响应；
                                dialog.setCancelable(false);
                                dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        mDownload.onCancelled();
                                    }
                                });
                                dialog.show();
                                mDownload = new DownloadFile(progressHandler);
                                mDownload.execute(item.getFWUrl(),FW_FILE_NAME,ROOT_PATH);


                            }


                        }
                    }
                });


            }
        }).start();
    }

    public void upload(View v){
        if (isDownloadFinshed){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String result = HttpUtil.requestByGet("http://192.168.1.254/?custom=1&cmd=3035");
                    Log.e("logan_tag",result);
                    try {
                        Thread.sleep(100);
                        String result1 = HttpUtil.requestByGet("http://192.168.1.254/?custom=1&cmd=3001&par=2");
                        Log.e("logan_tag",result1);
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    UploadFileToDevice.upload();

                }
            }).start();
        }else{
            Toast.makeText(getApplicationContext(),"下载未完成",Toast.LENGTH_SHORT).show();
        }

    }

}
