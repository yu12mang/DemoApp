package com.example.yumang.otademo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

/**
 * Created by Administrator on 2017/7/18.
 */

public class UploadFileToDevice {
    private static  String path = Environment.getExternalStorageDirectory().toString() + "/FW55WIFI.bin";
    private static  String DeviceIp ="192.168.1.254";


    public static void upload(){

        ParseResult persons = null;
        String result = HttpUtil.requestByGet("http://192.168.1.254/?custom=1&cmd=3026");
        if (result != null) {
            persons = XmlPullParseUtil.parse(result);
        }
        if (persons != null) {
            File file = new File(path);
            String response = null;
            InputStreamReader in = null;
            HttpURLConnection conn = null;

            String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
            String PREFIX = "--", LINE_END = "\r\n";
            String CONTENT_TYPE = "multipart/form-data"; // 内容类型
            String CHARSET = "utf-8"; // 设置编码
            try {
                //发送GET请求
                URL url = new URL("http://" +DeviceIp );
                Log.d("sheng", "response = " + url.toString());
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setRequestProperty("connection", "keep-alive");
                conn.setRequestProperty("Charsert", "UTF-8");
                conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
                long FW_size = 0;
                //获取conn的输出流

                long fw_size = file.length();
                OutputStream os = conn.getOutputStream();
                StringBuffer sb = new StringBuffer();
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                /**
                 * 这里重点注意： name里面的值为服务端需要key 只有这个key 才可以得到对应的文件
                 * filename是文件的名字，包含后缀名的 比如:abc.png
                 */
                sb.append("Content-Disposition: form-data; name=\"uploadfile\"; filename=\""
                        + file.getName() + "\"" + LINE_END);
                sb.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINE_END);
                sb.append(LINE_END);
                os.write(sb.toString().getBytes());

                DataInputStream dataIn = new DataInputStream(new FileInputStream(file));
                //获取两个键值对name=孙群和age=27的字节数组，将该字节数组作为请求体
                //将请求体写入到conn的输出流中
                int bytes = 0;
                byte[] bufferOut = new byte[1024];
                while ((bytes = dataIn.read(bufferOut)) != -1) {
                    os.write(bufferOut, 0, bytes);
                    FW_size += bytes;
                    Bundle bundle = new Bundle();
                    bundle.putInt("read", bytes);
                    bundle.putLong("long", fw_size);
//                    Message msg = handler.obtainMessage();
//                    msg.what = STS_DOWNLoad_PROCESS;
//                    msg.setData(bundle);
//                    handler.sendMessage(msg);
                }
                dataIn.close();
                os.write(LINE_END.getBytes());
                byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
                os.write(end_data);
                //记得调用输出流的flush方法
                os.flush();
                //关闭输出流
                os.close();
                /**
                 * 获取响应码 200=成功 当响应成功，获取响应的流
                 */
                int res = conn.getResponseCode();
                Log.e("logan_tag", "upload success:" + res);

                //当调用getInputStream方法时才真正将请求体数据上传至服务器
                InputStream is = conn.getInputStream();
                in = new InputStreamReader(is);
                BufferedReader bufferedReader = new BufferedReader(in);
                StringBuilder strBuffer = new StringBuilder();
                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    strBuffer.append(line);
                }
                response = strBuffer.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (response != null) {
//                InputStream is = new ByteArrayInputStream(response.getBytes());
//                persons = XmlParser.parse(is);
                persons = XmlPullParseUtil.parse(response);
                if (persons != null) {
                    response = HttpUtil.requestByGetForUpdateFW("http://192.168.1.254/?custom=1&cmd=3013");
                    Log.e("logan_tag",""+response);
                    persons = XmlPullParseUtil.parse(response);

                    if (persons != null && persons.getStatus().equals("0")) {
                        Log.e("logan_tag","固件更新成功");
                    } else {

                        if (file.exists()) {
                            file.delete();
                        }

                    }

                }
            }
        }
    }
}
