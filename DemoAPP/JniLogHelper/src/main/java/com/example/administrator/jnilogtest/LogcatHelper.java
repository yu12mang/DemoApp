package com.example.administrator.jnilogtest;


import android.content.Context;
import android.os.Message;

import java.io.BufferedReader;
import java.io.InputStreamReader;


/**
 * log日志统计保存
 */

public class LogcatHelper {

    private static LogcatHelper instance = null;
    private LogDumper mLogDumper = null;
    private int mPId;
    private CustomHandler handler;

    public static LogcatHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (LogcatHelper.class) {
                if (instance == null) {
                    instance = new LogcatHelper(context);
                }
            }
        }
        return instance;
    }

    private LogcatHelper(Context context) {
        mPId = android.os.Process.myPid();
    }

    public LogcatHelper setJniHandler(CustomHandler handler) {
        this.handler = handler;
        return instance;
    }

    public LogcatHelper start() {
        if (mLogDumper == null)
            mLogDumper = new LogDumper();
        mLogDumper.start();
        return instance;
    }

    public void stop() {
        if (mLogDumper != null) {
            mLogDumper.stopLogs();
            mLogDumper = null;
        }
        if (handler != null) {
            handler = null;
        }
    }

    private class LogDumper extends Thread {

        private Process logcatProc;
        private BufferedReader mReader = null;
        private boolean mRunning = true;
        private String cmds = null;

        private LogDumper() {
            cmds = "logcat  | grep \"(" + mPId + ")\"";//打印所有日志信息
            /*
             * 日志等级：*:v , *:d , *:w , *:e , *:f , *:s
             * 显示当前mPID程序的 E和W等级的日志.
             * cmds = "logcat *:e *:w | grep \"(" + mPID + ")\"";
             *  cmds = "logcat -s loganlog111";//打印标签过滤信息
             *cmds = "logcat *:e *:i | grep \"(" + mPID + ")\"";
             * cmds = "logcat loganlog111:V *:S";
             */
        }

        private void stopLogs() {
            mRunning = false;
        }

        @Override
        public void run() {
            try {
                logcatProc = Runtime.getRuntime().exec(cmds);
                mReader = new BufferedReader(new InputStreamReader(logcatProc.getInputStream()), 1024);
                String line = null;
                while (mRunning && (line = mReader.readLine()) != null) {
                    synchronized (LogDumper.this) {
                        if (!mRunning) {
                            break;
                        }
                        //直接跳进下一次循环
                        if (line.length() == 0) {
                            continue;
                        }
                        //该处做过滤
                        if (line.contains("jniLog")) {
                            if (handler != null) {
                                Message msg = handler.obtainMessage(0x123);
                                msg.obj = line;
                                handler.sendMessage(msg);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (logcatProc != null) {
                    logcatProc.destroy();
                    logcatProc = null;
                }
                if (mReader != null) {
                    try {
                        mReader.close();
                        mReader = null;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}

