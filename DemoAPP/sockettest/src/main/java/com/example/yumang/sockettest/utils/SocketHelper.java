package com.example.yumang.sockettest.utils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.WorkSource;
import android.util.Log;

import com.ntk.nvtkit.NVTKitModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * Created by Administrator on 2017/7/17.
 */

public class SocketHelper {

    private static String TAG = "LOGAN_TAG";
    private static Socket socket;
    private static String deviceIP = "192.168.1.254";

    private static boolean isWork = false;
    private static boolean isSocketOn = false;

    private static boolean isHeartBeat = true;
    private static int cout = 0;

    private static String buffer;
    private static Handler handler;

    public SocketHelper(Handler handler) {
        this.handler = handler;
        SocketThread thread = new SocketThread();
        thread.start();
    }

    public static void closeSocket() {
        isSocketOn = false;
    }

    public static void sendSocket() {
        if (isSocketOn){
            try {
                Log.e("asdf", "send SockectHB");
                OutputStream outputstream = socket.getOutputStream();
                String s1 = "send SockectHB";
                outputstream.write(s1.getBytes());
                outputstream.flush();
            } catch (IOException ioexception) {
                Log.e(TAG,"sendSocket() IOException");
                handler.sendEmptyMessage(0x456);
                cout++;
                ioexception.printStackTrace();
            }catch (NullPointerException e){
                Log.e(TAG,"sendSocket() NullPointerException");
                handler.sendEmptyMessage(0x456);
                e.printStackTrace();
            }
        }

    }


    public static class SocketThread extends Thread {


        @Override
        public void run() {
            if (!isWork) {
                isWork = true;


                try {
                    closeSocket();
                    Thread.sleep(100);
                    initSocket();
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    handler.sendEmptyMessage(0x456);
                    Log.e(TAG,"SocketThread InterruptedException");
                    e.printStackTrace();
                }


                while (isWork) {
                    try {

                        while (cout < 5) {
                            if (!isWork) {
                                return;
                            }
                            cout++;
                            sendSocket();

                            Thread.sleep(2000);
                        }

                        isHeartBeat = true;
                        if (handler != null) {
//                            Log.e("socket","Socket sucess");
                            handler.sendEmptyMessage(0x123);
                        }
                        String result = NVTKitModel.customFunctionForCommand("http://192.168.1.254/?custom=1&cmd=3016");

                         Thread.sleep(500);
                        while (result == null && isHeartBeat) {

                            Log.e(TAG,"无网 开始进入无网检测");
                            Thread.sleep(1000);
                            Log.e(TAG, "devHeartBeat no response");
                            result = NVTKitModel.customFunctionForCommand("http://192.168.1.254/?custom=1&cmd=3016");
                            if (!isWork) {
                                return;
                            }
//                            if (result.equals("-22")) {
//                                //NVTKitModel.devAPPSessionOpen();
//                            }

                            cout = 0;
                            closeSocket();
                            Thread.sleep(100);
                            initSocket();
                            Thread.sleep(100);
//                        NVTKitModel.videoStop();
//						NVTKitModel.videoStopPlay();
//                        NVTKitModel.videoResumePlay();

                            if (handler != null) {
                                handler.sendEmptyMessage(0x456);
                            }

                        }
                    } catch (InterruptedException e) {
                        handler.sendEmptyMessage(0x456);
                        Log.e(TAG,"SocketThread InterruptedException");
                        e.printStackTrace();
                    }
                }
                isWork = false;
                Log.e(TAG,""+isWork);



            }

        }
    }

    private static void initSocket() {

        if (!isSocketOn) {
            isSocketOn = true;
            new Thread(new Runnable() {

                @Override
                public void run() {
                    int i = 0;
                    byte abyte0[] = new byte[1024];
                    try {
                        socket = new Socket(deviceIP, 3333);
                    } catch (IOException ioexception) {
                        if (handler != null) {
                            Message msg = new Message();
                            handler.sendEmptyMessage(0x456);

                        }
                        ioexception.printStackTrace();
                        return;
                    }
                    try {
                        socket.setSendBufferSize(20);
                    } catch (SocketException socketexception) {
                        handler.sendEmptyMessage(0x456);
                        Log.e(TAG,"initSocket SocketException");
                        socketexception.printStackTrace();
                    }
                    try {
                        InputStream inputstream = socket.getInputStream();
                        while (isSocketOn) {
                            if (inputstream.available() <= 0)
                                continue;
                            int j = inputstream.read(abyte0);
                            String s = new String(abyte0, 0, j);
                            Log.e(TAG, "回复内容" + s);
                            if (handler == null)
                                continue;
                            if (s.contains("AutoTest")) {
                                i++;
                                continue;
                            }
                            Log.e(TAG, "回复内容:" + s);
                        }
                    } catch (IOException ioexception1) {
                        handler.sendEmptyMessage(0x456);
                        Log.e(TAG,"initSocket IOException");
                        ioexception1.printStackTrace();
                    }
                    try {
                        socket.shutdownInput();
                        socket.shutdownOutput();
                        InputStream inputstream1 = socket.getInputStream();
                        OutputStream outputstream = socket.getOutputStream();
                        inputstream1.close();
                        outputstream.close();
                        socket.close();
                    } catch (IOException ioexception2) {
                        handler.sendEmptyMessage(0x456);
                        Log.e(TAG,"initSocket IOException");
                        ioexception2.printStackTrace();
                    }

                }
            }).start();
        }


    }

}
