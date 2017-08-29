package com.example.yumang.otademo;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Administrator on 2017/7/18.
 */

public class HttpUtil {

//

    /**
     * 通过HttpUrlConnection发送GET请求
     *
     * @return
     */
    public static String requestByGet(String path) {
        try {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(2000);

            conn.setRequestMethod("GET");
            int code = conn.getResponseCode();
            if (code == 200) {
                InputStream is = conn.getInputStream(); // 字节流转换成字符串
                return streamToString(is);
            } else {
                Log.e("LOGAN_LOG", "request fail");
                return "request fail";
            }
        } catch (Exception e) {
            Log.e("LOGAN_LOG", "request fail");
            e.printStackTrace();
            return "request fail";
        }
    }
    public static String requestByGetForUpdateFW(String path) {
        try {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            int code = conn.getResponseCode();
            if (code == 200) {
                InputStream is = conn.getInputStream(); // 字节流转换成字符串
                return streamToString(is);
            } else {
                Log.e("LOGAN_LOG", "request fail");
                return "request fail";
            }
        } catch (Exception e) {
            Log.e("LOGAN_LOG", "request fail");
            e.printStackTrace();
            return "request fail";
        }
    }


    /**
     * 将输入流转换成字符串
     *
     * @param is 从网络获取的输入流
     * @return
     */
    public static String streamToString(InputStream is) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            baos.close();
            is.close();
            byte[] byteArray = baos.toByteArray();
            return new String(byteArray);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }




}
