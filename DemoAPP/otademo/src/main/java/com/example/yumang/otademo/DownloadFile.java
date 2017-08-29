package com.example.yumang.otademo;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Administrator on 2017/7/18.
 */

class DownloadFile extends AsyncTask<String, String, String> {

    public final static int REQUEST_DOWNLOAD_FILE_FINISHED = 0X123;
    public final static int REQUEST_DOWNLOAD_FILE_CANCELED = 0X456;
    public final static int REQUEST_DOWNLOAD_FILE_EXISTS = 0X789;
    private String file_name;
    private String path;
    private Handler handler;


    public DownloadFile(Handler handler){
        this.handler = handler;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
//            showDialog(progress_bar_type);
    }

    @Override
    protected String doInBackground(String... params) {

        int count;
        try {
            URL url = new URL(params[0]);
            file_name = params[1];
            path = params[2];

            File file = null;
            file = new File(path + "/" + file_name);

            if (file.exists()){
                handler.sendEmptyMessage(REQUEST_DOWNLOAD_FILE_EXISTS);

            }
            URLConnection conection = url.openConnection();
            conection.connect();
            // this will be useful so that you can show a tipical 0-100%
            // progress bar
            int lenghtOfFile = conection.getContentLength();
            // download the file
            InputStream input = new BufferedInputStream(url.openStream(), 8192);
            // Output stream
            OutputStream output;


            output = new FileOutputStream(path + "/" + file_name);

            byte data[] = new byte[1024];
            long total = 0;
            while ((count = input.read(data)) != -1) {
                total += count;

                publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                // writing data to file
                output.write(data, 0, count);
                if (isCancelled()) {
                    // flushing output
                    output.flush();
                    // closing streams
                    output.close();
                    input.close();
                }
            }

            // flushing output
            output.flush();
            // closing streams
            output.close();
            input.close();

        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
        }

        return null;
    }


    @Override
    protected void onCancelled() {

        File f = new File(path + "/" + file_name);
        if (f.exists()) {
            f.delete();
        }
        handler.sendEmptyMessage(REQUEST_DOWNLOAD_FILE_CANCELED);

    }

    String temp = "-1";
    protected void onProgressUpdate(String... progress) {
        String curProgress = progress[0];
        if (!curProgress.equalsIgnoreCase(temp) ){
            Message msg = new Message();
            msg.arg1 = Integer.valueOf(curProgress);
            handler.sendMessage(msg);

        }
        temp = curProgress;
    }

    @Override
    protected void onPostExecute(String file_url) {
        handler.sendEmptyMessage(REQUEST_DOWNLOAD_FILE_FINISHED);
        Log.e("logan——tag","download sucess");
        // dismiss the dialog after the file was downloaded
//            dismissDialog(progress_bar_type);

    }

}
